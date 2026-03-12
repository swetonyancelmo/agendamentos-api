package com.swetonyancelmo.agendamentos.dtos.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceDto(
        UUID id,
        String serviceName,
        String description,
        BigDecimal price,
        Integer durationInMinutes,
        UUID businessId
) {
}
