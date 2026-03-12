package com.swetonyancelmo.agendamentos.controller;

import com.swetonyancelmo.agendamentos.controller.docs.AuthControllerDocs;
import com.swetonyancelmo.agendamentos.dtos.request.BusinessLoginDto;
import com.swetonyancelmo.agendamentos.dtos.request.CreateBusinessDto;
import com.swetonyancelmo.agendamentos.dtos.request.CreateCustomerDto;
import com.swetonyancelmo.agendamentos.dtos.request.CustomerLoginDto;
import com.swetonyancelmo.agendamentos.dtos.response.BusinessDto;
import com.swetonyancelmo.agendamentos.dtos.response.CustomerDto;
import com.swetonyancelmo.agendamentos.dtos.response.LoginDto;
import com.swetonyancelmo.agendamentos.services.AuthService;
import com.swetonyancelmo.agendamentos.services.BusinessService;
import com.swetonyancelmo.agendamentos.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Endpoints de autenticação e registro de empresas e clientes")
public class AuthController implements AuthControllerDocs {

    private final BusinessService businessService;
    private final CustomerService customerService;
    private final AuthService authService;

    public AuthController(BusinessService businessService, CustomerService customerService, AuthService authService) {
        this.businessService = businessService;
        this.customerService = customerService;
        this.authService = authService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_BUSINESS')")
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BusinessDto> create(@Valid @RequestBody CreateBusinessDto dto) {
        BusinessDto createdBusiness = businessService.createBusiness(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBusiness);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_BUSINESS')")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginDto> login(@Valid @RequestBody BusinessLoginDto dto) {
        LoginDto loginResponse = authService.businessLogin(dto);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Registrar cliente", description = "Cadastra um novo cliente no sistema")
    @SecurityRequirements
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    @PostMapping(value = "/customer/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CreateCustomerDto dto) {
        CustomerDto createdCustomer = customerService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @Operation(summary = "Login de cliente", description = "Autentica o cliente e retorna o token JWT")
    @SecurityRequirements
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    @PostMapping(value = "/customer/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginDto> customerLogin(@Valid @RequestBody CustomerLoginDto dto) {
        LoginDto loginResponse = authService.customerLogin(dto);
        return ResponseEntity.ok(loginResponse);
    }
}
