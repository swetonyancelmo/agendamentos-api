package com.swetonyancelmo.agendamentos.dtos.response;

import java.util.UUID;

public record BusinessDto(
        UUID id,
        String name,
        String phone,
        String email
) {
}
