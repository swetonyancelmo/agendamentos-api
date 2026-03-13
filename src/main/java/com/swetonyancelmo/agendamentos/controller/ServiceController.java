package com.swetonyancelmo.agendamentos.controller;

import com.swetonyancelmo.agendamentos.controller.docs.ServiceControllerDocs;
import com.swetonyancelmo.agendamentos.dtos.request.CreateServiceDto;
import com.swetonyancelmo.agendamentos.dtos.response.ServiceDto;
import com.swetonyancelmo.agendamentos.models.Business;
import com.swetonyancelmo.agendamentos.services.ServiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/business/services")
@Tag(name = "Services", description = "Endpoint para criação de serviços oferecidos por uma empresa")
public class ServiceController implements ServiceControllerDocs {

    private final ServiceService service;

    public ServiceController(ServiceService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_BUSINESS')")
    @Override
    public ResponseEntity<ServiceDto> createService(@RequestBody @Valid CreateServiceDto dto, @AuthenticationPrincipal Business businessLogged) {
        ServiceDto createdService = service.createService(dto, businessLogged);
        return new ResponseEntity<>(createdService, HttpStatus.CREATED);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_BUSINESS')")
    @Override
    public ResponseEntity<List<ServiceDto>> getAllServices(){
        return ResponseEntity.ok(service.listAllServices());
    }

    @GetMapping("/{businessId}")
    @PreAuthorize("hasAnyRole('ROLE_BUSINESS', 'ROLE_CUSTOMER')")
    @Override
    public ResponseEntity<List<ServiceDto>> getServicesByBusinessId(@PathVariable UUID businessId){
        return ResponseEntity.ok(service.findServiceByBusiness(businessId));
    }

}
