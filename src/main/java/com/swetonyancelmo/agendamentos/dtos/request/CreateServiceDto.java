package com.swetonyancelmo.agendamentos.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateServiceDto(
        @NotBlank(message = "O nome do serviço é obrigatório")
        String serviceName,

        String description,

        @NotNull(message = "O preço do serviço é obrigatório")
        @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
        BigDecimal price,

        Integer durationInMinutes
) {
}
