package com.swetonyancelmo.agendamentos.controller.docs;

import com.swetonyancelmo.agendamentos.dtos.request.AvailabilityRequestDto;
import com.swetonyancelmo.agendamentos.dtos.response.AvailabilityResponseDto;
import com.swetonyancelmo.agendamentos.dtos.response.ErrorResponseDto;
import com.swetonyancelmo.agendamentos.models.Business;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.UUID;

public interface AvailabilityControllerDocs {

    @Operation(summary = "Cadastra Disponibilidade", description = "Empresa cadastra um horário disponível para atendimento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Disponibilidade cadastrada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AvailabilityResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<AvailabilityResponseDto> create(@RequestBody @Valid AvailabilityRequestDto dto,
                                                   @Parameter(hidden = true) @AuthenticationPrincipal Business businessLogged);

    @Operation(summary = "Lista Disponibilidades", description = "Retorna todos os horários disponíveis de uma empresa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidades retornadas com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AvailabilityResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<List<AvailabilityResponseDto>> findAllByBusiness(@PathVariable UUID businessId);

    @Operation(summary = "Deleta Disponibilidade", description = "Empresa remove um horário disponível")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Disponibilidade removida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    void delete(@PathVariable UUID availabilityId,
                @Parameter(hidden = true) @AuthenticationPrincipal Business businessLogged);
}
