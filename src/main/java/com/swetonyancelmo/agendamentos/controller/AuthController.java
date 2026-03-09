package com.swetonyancelmo.agendamentos.controller;

import com.swetonyancelmo.agendamentos.controller.docs.AuthControllerDocs;
import com.swetonyancelmo.agendamentos.dtos.request.BusinessLoginDto;
import com.swetonyancelmo.agendamentos.dtos.request.CreateBusinessDto;
import com.swetonyancelmo.agendamentos.dtos.response.BusinessDto;
import com.swetonyancelmo.agendamentos.dtos.response.LoginDto;
import com.swetonyancelmo.agendamentos.services.AuthService;
import com.swetonyancelmo.agendamentos.services.BusinessService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Endpoints de autenticação e registro de empresas")
public class AuthController implements AuthControllerDocs {

    private final BusinessService businessService;
    private final AuthService authService;

    public AuthController(BusinessService businessService, AuthService authService) {
        this.businessService = businessService;
        this.authService = authService;
    }

    @Override
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BusinessDto> create(@Valid @RequestBody CreateBusinessDto dto) {
        BusinessDto createdBusiness = businessService.createBusiness(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBusiness);
    }

    @Override
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginDto> login(@Valid @RequestBody BusinessLoginDto dto) {
        LoginDto loginResponse = authService.businessLogin(dto);
        return ResponseEntity.ok(loginResponse);
    }
}
