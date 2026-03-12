package com.swetonyancelmo.agendamentos.dtos.response;

import java.util.UUID;

public record CustomerDto(
        UUID id,
        String name,
        String phone,
        String email
) {
}
