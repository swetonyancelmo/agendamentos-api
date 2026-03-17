package com.swetonyancelmo.agendamentos.controller;

import com.swetonyancelmo.agendamentos.dtos.request.CreateAppointmentDto;
import com.swetonyancelmo.agendamentos.dtos.response.AppointmentDto;
import com.swetonyancelmo.agendamentos.services.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public AppointmentDto createAppointment(@RequestBody CreateAppointmentDto dto) {
        return appointmentService.createAppointment(dto);
    }

    // ✅ cliente logado (sem parâmetro)
    @GetMapping("/customer")
    public List<AppointmentDto> getCustomerAppointments() {
        return appointmentService.getCustomerAppointments();
    }

    // ✅ business COM parâmetro
    @GetMapping("/business/{businessId}")
    public List<AppointmentDto> getBusinessAppointments(
            @PathVariable UUID businessId
    ) {
        return appointmentService.getBusinessAppointments(businessId);
    }
}