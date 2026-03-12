package com.swetonyancelmo.agendamentos.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateCustomerDto(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "^\\d{11}") String phone,
        @NotBlank @Email String email
) {
}
