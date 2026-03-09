package com.swetonyancelmo.agendamentos.dtos.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record BusinessLoginDto(
        @NotBlank(message = "O email da empresa/dono do serviço é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "A senha da empresa/dono do serviço é obrigatória")
        @Length(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
        String password
) {
}
