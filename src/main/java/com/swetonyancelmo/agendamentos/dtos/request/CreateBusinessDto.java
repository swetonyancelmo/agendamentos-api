package com.swetonyancelmo.agendamentos.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record CreateBusinessDto(
        @NotBlank(message = "O nome da empresa/dono do serviço é obrigatório")
        String name,

        @NotBlank(message = "O telefone da empresa/dono do serviço é obrigatório")
        @Pattern(
                regexp = "^\\d{11}$",
                message = "Telefone deve ter 11 números (DDD + número)"
        )
        String phone,

        @NotBlank(message = "O email da empresa/dono do serviço é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "A senha da empresa/dono do serviço é obrigatória")
        @Length(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
        String password
) {
}
