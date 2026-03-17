package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.dtos.request.CreateAppointmentDto;
import com.swetonyancelmo.agendamentos.dtos.response.AppointmentDto;
import com.swetonyancelmo.agendamentos.exceptions.BusinessRuleException;
import com.swetonyancelmo.agendamentos.mapper.AppointmentMapper;
import com.swetonyancelmo.agendamentos.models.Appointment;
import com.swetonyancelmo.agendamentos.models.Business;
import com.swetonyancelmo.agendamentos.models.Customer;
import com.swetonyancelmo.agendamentos.repositories.AppointmentRepository;
import com.swetonyancelmo.agendamentos.repositories.BusinessRepository;
import com.swetonyancelmo.agendamentos.repositories.ServiceRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;
    private final BusinessRepository businessRepository;
    private final AppointmentMapper mapper;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            ServiceRepository serviceRepository,
            BusinessRepository businessRepository,
            AppointmentMapper mapper
    ) {
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
        this.businessRepository = businessRepository;
        this.mapper = mapper;
    }

    public AppointmentDto createAppointment(CreateAppointmentDto dto) {

        Customer customer = (Customer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        var serviceEntity = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        Business business = businessRepository.findById(dto.getBusinessId())
                .orElseThrow(() -> new RuntimeException("Business não encontrado"));

        // valida se serviço pertence ao business
        if (!serviceEntity.getBusiness().getId().equals(dto.getBusinessId())) {
            throw new RuntimeException("Serviço não pertence à empresa");
        }

        LocalTime endTime = dto.getStartTime()
                .plusMinutes(serviceEntity.getDurationInMinutes());

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

    
    public List<AppointmentDto> getBusinessAppointments(UUID businessId) {

        return appointmentRepository
                .findByBusinessIdAndAppointmentDateBetween(
                        businessId,
                        java.time.LocalDate.now(),
                        java.time.LocalDate.now().plusDays(7)
                )
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}