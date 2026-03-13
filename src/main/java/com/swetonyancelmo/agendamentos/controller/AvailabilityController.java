package com.swetonyancelmo.agendamentos.controller;

import com.swetonyancelmo.agendamentos.dtos.request.AvailabilityRequestDto;
import com.swetonyancelmo.agendamentos.dtos.response.AvailabilityResponseDto;
import com.swetonyancelmo.agendamentos.services.AvailabilityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/business/{business}/availability")
@Tag(name = "Availability", description = "Gestão de horários da empresa")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService service;

    @PostMapping(value = "/create")
    public ResponseEntity<AvailabilityResponseDto> create(@PathVariable UUID businessId,
                                                          @RequestBody @Valid AvailabilityRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(businessId, dto));
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<AvailabilityResponseDto>> findAll(@PathVariable UUID businessId) {
        return ResponseEntity.ok(service.findAllByBusiness(businessId));
    }

    @DeleteMapping("/delete/{availabilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID availabilityId) {
        service.delete(availabilityId);
    }
}
