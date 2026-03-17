package com.swetonyancelmo.agendamentos.dtos.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class CreateAppointmentDto {

    private UUID serviceId;
    private UUID businessId;

    private LocalDate appointmentDate;
    private LocalTime startTime;

}