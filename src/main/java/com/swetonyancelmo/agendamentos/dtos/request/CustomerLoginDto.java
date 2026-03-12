package com.swetonyancelmo.agendamentos.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CustomerLoginDto(
        @NotBlank(message = "O email do cliente é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "A senha do cliente é obrigatória")
        @Length(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
        String password
) {
}
