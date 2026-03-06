package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.dtos.AppointmentRequestDto;
import com.swetonyancelmo.agendamentos.models.Appointment;
import com.swetonyancelmo.agendamentos.models.Customer;
import com.swetonyancelmo.agendamentos.models.enums.AppointmentStatus;
import com.swetonyancelmo.agendamentos.repositories.AppointmentRepository;
import com.swetonyancelmo.agendamentos.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;

    private final List<LocalTime> FIXED_SLOTS = List.of(
            LocalTime.of(8, 0),
            LocalTime.of(10, 0),
            LocalTime.of(14, 0),
            LocalTime.of(16, 0)
    );
    private final AbstractHandlerMethodAdapter abstractHandlerMethodAdapter;

    public List<LocalTime> getAvailableSlots(LocalDate date) {
        List<Appointment> existingAppointments = appointmentRepository
                .findByAppointmentDateAndStatusNot(date, AppointmentStatus.REJECTED);

        List<LocalTime> occupiedTimes = existingAppointments.stream()
                .map(Appointment::getAppointmentTime)
                .toList();

        return FIXED_SLOTS.stream()
                .filter(slot -> !occupiedTimes.contains(slot))
                .collect(Collectors.toList());
    }

    @Transactional
    public Appointment createAppointment(AppointmentRequestDto dto) {
        List<LocalTime> availableSlots = getAvailableSlots(dto.appointmentDate());
        if(!availableSlots.contains(dto.appointmentTime())) {
            throw new RuntimeException("Desculpe, este horário não está disponível. Por favor, escolha outro horário.");
        }

        Customer customer = customerRepository.findByPhone(dto.customerPhone())
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setName(dto.customerName());
                    newCustomer.setPhone(dto.customerPhone());
                    return customerRepository.save(newCustomer);
                });

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setServiceName(dto.serviceName());
        appointment.setAppointmentDate(dto.appointmentDate());
        appointment.setAppointmentTime(dto.appointmentTime());
        appointment.setStatus(AppointmentStatus.PENDING);

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Transactional
    public Appointment updateAppointmentStatus(UUID id, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        appointment.setStatus(newStatus);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        if(newStatus == AppointmentStatus.CONFIRMED) {
            // Simula envio de mensagem para o cliente pelo Whatsapp (por enquanto)
            System.out.println("Notificação: O agendamento para " + appointment.getCustomer().getName() + " no dia " +
                    appointment.getAppointmentDate() + " às " + appointment.getAppointmentTime() + " foi confirmado.");
        }

        return savedAppointment;
    }

}
