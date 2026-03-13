package com.swetonyancelmo.agendamentos.controller;

import com.swetonyancelmo.agendamentos.controller.docs.AvailabilityControllerDocs;
import com.swetonyancelmo.agendamentos.dtos.request.AvailabilityRequestDto;
import com.swetonyancelmo.agendamentos.dtos.response.AvailabilityResponseDto;
import com.swetonyancelmo.agendamentos.models.Business;
import com.swetonyancelmo.agendamentos.services.AvailabilityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/business")
@Tag(name = "Availability", description = "Gestão de horários da empresa")
public class AvailabilityController implements AvailabilityControllerDocs {

    private final AvailabilityService service;

    public AvailabilityController(AvailabilityService service) {
        this.service = service;
    }

    @PostMapping("/availability")
    @PreAuthorize("hasAnyRole('ROLE_BUSINESS')")
    @Override
    public ResponseEntity<AvailabilityResponseDto> create(@RequestBody @Valid AvailabilityRequestDto dto,
                                                          @AuthenticationPrincipal Business businessLogged) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(businessLogged, dto));
    }

    @GetMapping("/availability/{businessId}")
    @PreAuthorize("hasAnyRole('ROLE_BUSINESS', 'ROLE_CUSTOMER')")
    @Override
    public ResponseEntity<List<AvailabilityResponseDto>> findAllByBusiness(@PathVariable UUID businessId) {
        return ResponseEntity.ok(service.findAllByBusiness(businessId));
    }

    @DeleteMapping("/availability/{availabilityId}")
    @PreAuthorize("hasAnyRole('ROLE_BUSINESS')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void delete(@PathVariable UUID availabilityId, @AuthenticationPrincipal Business businessLogged) {
        service.delete(availabilityId, businessLogged);
    }
}
