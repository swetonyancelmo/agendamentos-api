package com.swetonyancelmo.agendamentos.config;

import lombok.Builder;

import java.util.UUID;

@Builder
public record JWTUserData(
        UUID userId,
        String email,
        String role
) {
}
