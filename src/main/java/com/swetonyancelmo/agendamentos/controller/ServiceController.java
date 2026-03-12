package com.swetonyancelmo.agendamentos.controller;

import com.swetonyancelmo.agendamentos.dtos.request.CreateServiceDto;
import com.swetonyancelmo.agendamentos.dtos.response.ServiceDto;
import com.swetonyancelmo.agendamentos.models.Business;
import com.swetonyancelmo.agendamentos.services.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
public class ServiceController implements com.swetonyancelmo.agendamentos.controller.docs.ServiceControllerDocs {

    private final ServiceService service;

    public ServiceController(ServiceService service) {
        this.service = service;
    }

    @PostMapping
    @Override
    public ResponseEntity<ServiceDto> createService(@RequestBody @Valid CreateServiceDto dto, @AuthenticationPrincipal Business businessLogged) {
        ServiceDto createdService = service.createService(dto, businessLogged);
        return new ResponseEntity<>(createdService, HttpStatus.CREATED);
    }

}
