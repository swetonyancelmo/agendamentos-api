package com.swetonyancelmo.agendamentos.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailabilityRequestDto(
        @NotNull(message = "A data é obrigatória")
        LocalDate date,

        @NotNull(message = "O horário de início é obrigatório")
        LocalTime startTime,

        @NotNull(message = "O horário de término é obrigatório")
        LocalTime endTime
) {}
