package com.swetonyancelmo.agendamentos.controller;

import com.swetonyancelmo.agendamentos.dtos.request.CreateCustomerDto;
import com.swetonyancelmo.agendamentos.dtos.response.CustomerDto;
import com.swetonyancelmo.agendamentos.services.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@Tag(name = "Customers", description = "Gerenciamento de Clientes")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @GetMapping(value = "/listAll")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    public ResponseEntity<List<CustomerDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping(value = "/search/{email}")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerDto> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @PutMapping(value = "/update/{email}")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerDto> update(@PathVariable String email, @RequestBody @Valid CreateCustomerDto dto) {
        return ResponseEntity.ok(service.update(email, dto));
    }

    @DeleteMapping(value = "/delete/{email}")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String email) {
        service.delete(email);
    }

}
