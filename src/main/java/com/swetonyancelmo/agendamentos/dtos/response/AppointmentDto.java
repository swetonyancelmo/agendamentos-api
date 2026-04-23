package com.swetonyancelmo.agendamentos.dtos.response;

import com.swetonyancelmo.agendamentos.models.enums.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentDto(

        UUID id,
        String serviceName,
        BigDecimal servicePrice,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        AppointmentStatus status

) {}