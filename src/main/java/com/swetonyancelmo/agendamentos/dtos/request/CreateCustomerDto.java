package com.swetonyancelmo.agendamentos.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record CreateCustomerDto(
        @NotBlank(message = "Nome é obrigatório") 
        String name,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "Telefone deve ter 11 dígitos") 
        String phone,

        @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail deve ser válido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Length(min = 6, message = "A senha deve conter no mínimo 6 caracteres") 
        String password
) {
}
