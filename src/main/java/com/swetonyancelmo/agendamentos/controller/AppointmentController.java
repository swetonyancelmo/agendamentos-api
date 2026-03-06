package com.swetonyancelmo.agendamentos.controller;

import com.swetonyancelmo.agendamentos.dtos.AppointmentRequestDto;
import com.swetonyancelmo.agendamentos.models.Appointment;
import com.swetonyancelmo.agendamentos.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/available-slots")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date) {

        List<LocalTime> slots = appointmentService.getAvailableSlots(date);
        return ResponseEntity.ok(slots);
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentRequestDto dto) {
        Appointment newAppointment = appointmentService.createAppointment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAppointment);
    }

}
