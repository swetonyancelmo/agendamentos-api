package com.swetonyancelmo.agendamentos.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateCustomerDto(
        @NotBlank(message = "Nome é obrigatório") String name,
        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "Telefone deve ter 11 dígitos") String phone,
        @NotBlank(message = "E-mail é obrigatório") @Email String email
) {
}
