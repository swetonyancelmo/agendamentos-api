package com.swetonyancelmo.agendamentos.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AvailabilityRequestDto(
        @NotNull(message = "O dia da semana é obrigatório")
        DayOfWeek dayOfWeek,

        @NotNull(message = "O horário de início é obrigatório")
        LocalTime startTime,

        @NotNull(message = "O horário de término é obrigatório")
        LocalTime endTime
) {}
