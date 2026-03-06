package com.swetonyancelmo.agendamentos.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentRequestDto(
    String customerName,
    String customerPhone,
    String serviceName,
    LocalDate appointmentDate,
    LocalTime appointmentTime
) {
}
