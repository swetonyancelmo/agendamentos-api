package com.swetonyancelmo.agendamentos.controller;

import com.swetonyancelmo.agendamentos.dtos.request.CreateAppointmentDto;
import com.swetonyancelmo.agendamentos.dtos.request.UpdateAppointmentStatusDto;
import com.swetonyancelmo.agendamentos.dtos.response.AppointmentDto;
import com.swetonyancelmo.agendamentos.models.enums.AppointmentStatus;
import com.swetonyancelmo.agendamentos.services.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Agendamentos", description = "Operações relacionadas a agendamentos de serviços")
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Criar agendamento", description = "Cria um novo agendamento de serviço.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public AppointmentDto createAppointment(@RequestBody CreateAppointmentDto dto) {
        return appointmentService.createAppointment(dto);
    }

    @Operation(summary = "Listar agendamentos do cliente autenticado",
            description = "Retorna todos os agendamentos associados ao cliente atualmente autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    })
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<AppointmentDto> getCustomerAppointments() {
        return appointmentService.getCustomerAppointments();
    }

    @Operation(summary = "Listar meus agendamentos (cliente)",
            description = "Alias de GET /api/appointments/customer para alinhar com a documentação do MVP.")
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<AppointmentDto> getMyAppointments() {
        return appointmentService.getCustomerAppointments();
    }

    @Operation(summary = "Listar agendamentos por estabelecimento",
            description = "Retorna todos os agendamentos de um estabelecimento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Estabelecimento não encontrado", content = @Content)
    })
    @GetMapping("/business/{businessId}")
    @PreAuthorize("hasRole('BUSINESS')")
    public List<AppointmentDto> getBusinessAppointments(
            @PathVariable UUID businessId,
            @RequestParam(required = false) AppointmentStatus status
    ) {
        return appointmentService.getBusinessAppointments(businessId, status);
    }

    @Operation(summary = "Aceitar ou rejeitar agendamento (prestador)",
            description = "Atualiza o status de um agendamento PENDING para CONFIRMED ou REJECTED.")
    @PatchMapping("/{appointmentId}/status")
    @PreAuthorize("hasRole('BUSINESS')")
    public AppointmentDto updateAppointmentStatus(
            @PathVariable UUID appointmentId,
            @Valid @RequestBody UpdateAppointmentStatusDto dto
    ) {
        return appointmentService.updateStatusByBusiness(appointmentId, dto);
    }

    @Operation(summary = "Cancelar agendamento (cliente)", description = "Marca o agendamento como CANCELLED.")
    @PatchMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public AppointmentDto cancelAppointment(@PathVariable UUID appointmentId) {
        return appointmentService.cancelByCustomer(appointmentId);
    }
}