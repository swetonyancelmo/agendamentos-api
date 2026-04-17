package com.swetonyancelmo.agendamentos.dtos.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AvailabilityResponseDto(
        UUID id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        UUID businessId
) {}
