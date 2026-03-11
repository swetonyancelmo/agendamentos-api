package com.swetonyancelmo.agendamentos.dtos.response;

import java.util.UUID;

public record ServiceDto(
        UUID id,
        String serviceName,
        String description,
        String price,
        Integer durationInMinutes,
        UUID businessId
) {
}
