package com.swetonyancelmo.agendamentos.controller.docs;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.swetonyancelmo.agendamentos.dtos.response.BusinessDto;
import com.swetonyancelmo.agendamentos.dtos.response.ErrorResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

public interface BusinessControllerDocs {

    @Operation(summary = "Listar empresas", description = "Lista todas as empresas cadastradas no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresas listadas com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<List<BusinessDto>> listAllBusinesses();

}