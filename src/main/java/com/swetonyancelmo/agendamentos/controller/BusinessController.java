package com.swetonyancelmo.agendamentos.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swetonyancelmo.agendamentos.controller.docs.BusinessControllerDocs;
import com.swetonyancelmo.agendamentos.dtos.response.BusinessDto;
import com.swetonyancelmo.agendamentos.services.BusinessService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/business")
@Tag(name = "Business", description = "Endpoint para gerenciamento de empresas")
public class BusinessController implements BusinessControllerDocs {
    
    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @GetMapping
    @Override
    @PreAuthorize("hasAnyRole('ROLE_BUSINESS', 'ROLE_CUSTOMER')")
    public ResponseEntity<List<BusinessDto>> listAllBusinesses() {
        return ResponseEntity.ok(businessService.listAllBusinesses());
    }

}
