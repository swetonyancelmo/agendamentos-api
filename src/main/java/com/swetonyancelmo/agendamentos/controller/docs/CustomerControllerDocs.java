package com.swetonyancelmo.agendamentos.controller.docs;

import com.swetonyancelmo.agendamentos.dtos.response.CustomerDto;
import com.swetonyancelmo.agendamentos.dtos.response.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public interface CustomerControllerDocs {

    @Operation(summary = "Retorna Customers", description = "Retorna todos os customers com status COMPLETED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers retornados com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerDto.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<List<CustomerDto>> getAllCustomerByStatusCompleted();
}
