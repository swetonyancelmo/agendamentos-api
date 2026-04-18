package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.dtos.request.CreateAppointmentDto;
import com.swetonyancelmo.agendamentos.dtos.request.UpdateAppointmentStatusDto;
import com.swetonyancelmo.agendamentos.dtos.response.AppointmentDto;
import com.swetonyancelmo.agendamentos.exceptions.BusinessRuleException;
import com.swetonyancelmo.agendamentos.mapper.AppointmentMapper;
import com.swetonyancelmo.agendamentos.models.Appointment;
import com.swetonyancelmo.agendamentos.models.Availability;
import com.swetonyancelmo.agendamentos.models.Business;
import com.swetonyancelmo.agendamentos.models.Customer;
import com.swetonyancelmo.agendamentos.models.enums.AppointmentStatus;
import com.swetonyancelmo.agendamentos.repositories.AppointmentRepository;
import com.swetonyancelmo.agendamentos.repositories.AvailabilityRepository;
import com.swetonyancelmo.agendamentos.repositories.BusinessRepository;
import com.swetonyancelmo.agendamentos.repositories.ServiceRepository;

import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final BusinessRepository businessRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AppointmentMapper mapper;
    private final EmailService emailService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            ServiceRepository serviceRepository,
            BusinessRepository businessRepository,
            AvailabilityRepository availabilityRepository,
            AppointmentMapper mapper,
            EmailService emailService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
        this.businessRepository = businessRepository;
        this.availabilityRepository = availabilityRepository;
        this.mapper = mapper;
        this.emailService = emailService;
    }

    public AppointmentDto createAppointment(CreateAppointmentDto dto) {

        Customer customer = (Customer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        var serviceEntity = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new BusinessRuleException("Serviço não encontrado"));

        Business business = businessRepository.findById(dto.getBusinessId())
                .orElseThrow(() -> new BusinessRuleException("Prestador não encontrado"));

        if (!serviceEntity.getBusiness().getId().equals(dto.getBusinessId())) {
            throw new BusinessRuleException("Serviço não pertence a este prestador");
        }

        LocalDateTime slotStart = LocalDateTime.of(dto.getAppointmentDate(), dto.getStartTime());
        if (!slotStart.isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("Não é permitido agendar no passado");
        }

        LocalTime endTime = dto.getStartTime().plusMinutes(serviceEntity.getDurationInMinutes());

        assertFitsAvailabilityWindow(business.getId(), dto.getAppointmentDate(), dto.getStartTime(), endTime);

        List<Appointment> conflicts =
                appointmentRepository.findConflictingAppointments(
                        business.getId(),
                        dto.getAppointmentDate(),
                        dto.getStartTime(),
                        endTime
                );

        if (!conflicts.isEmpty()) {
            throw new BusinessRuleException("Horário já reservado");
        }

        Appointment appointment = new Appointment();

        appointment.setCustomer(customer);
        appointment.setService(serviceEntity);
        appointment.setBusiness(business);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(endTime);

        Appointment saved = appointmentRepository.save(appointment);

        return mapper.toDto(saved);
    }

    private void assertFitsAvailabilityWindow(
            UUID businessId,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        List<Availability> daySlots = availabilityRepository.findAllByBusinessId(businessId).stream()
                .filter(a -> a.getDate().equals(appointmentDate))
                .toList();

        if (daySlots.isEmpty()) {
            throw new BusinessRuleException("Não há disponibilidade cadastrada para esta data");
        }

        boolean fits = daySlots.stream().anyMatch(a ->
                !startTime.isBefore(a.getStartTime()) && !endTime.isAfter(a.getEndTime()));

        if (!fits) {
            throw new BusinessRuleException("Horário fora da disponibilidade do prestador");
        }
    }

    public List<AppointmentDto> getCustomerAppointments() {

        Customer customer = (Customer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return appointmentRepository.findByCustomer(customer)
                .stream()
                .map(mapper::toDto)
                .toList();
    }


    public List<AppointmentDto> getBusinessAppointments(UUID businessId, AppointmentStatus statusFilter) {

        Business business = (Business) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!business.getId().equals(businessId)) {
            throw new BusinessRuleException("Acesso negado");
        }

        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusDays(7);

        List<Appointment> list = statusFilter == null
                ? appointmentRepository.findByBusinessIdAndAppointmentDateBetween(businessId, from, to)
                : appointmentRepository.findByBusinessIdAndAppointmentDateBetweenAndStatus(
                        businessId, from, to, statusFilter);

        return list.stream().map(mapper::toDto).toList();
    }

    @Transactional
    public AppointmentDto updateStatusByBusiness(UUID appointmentId, UpdateAppointmentStatusDto dto) {
        Business business = (Business) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessRuleException("Agendamento não encontrado"));

        if (!appointment.getBusiness().getId().equals(business.getId())) {
            throw new BusinessRuleException("Você não tem permissão para alterar este agendamento");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BusinessRuleException("Só é possível responder agendamentos pendentes");
        }

        if (dto.getStatus() != AppointmentStatus.CONFIRMED && dto.getStatus() != AppointmentStatus.REJECTED) {
            throw new BusinessRuleException("Status inválido. Use CONFIRMED ou REJECTED");
        }

        appointment.setStatus(dto.getStatus());
        if (dto.getStatus() == AppointmentStatus.REJECTED) {
            appointment.setRejectionReason(dto.getRejectionReason());
        } else {
            appointment.setRejectionReason(null);
        }

        Appointment saved = appointmentRepository.save(appointment);

        if (dto.getStatus() == AppointmentStatus.CONFIRMED) {
            emailService.sendConfirmationEmail(saved);
        }

        return mapper.toDto(saved);
    }

    public AppointmentDto cancelByCustomer(UUID appointmentId) {
        Customer customer = (Customer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessRuleException("Agendamento não encontrado"));

        if (!appointment.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException("Você não tem permissão para cancelar este agendamento");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.REJECTED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Este agendamento não pode ser cancelado");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return mapper.toDto(appointmentRepository.save(appointment));
    }
}
