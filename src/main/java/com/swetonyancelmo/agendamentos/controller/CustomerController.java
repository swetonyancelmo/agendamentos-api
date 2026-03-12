package com.swetonyancelmo.agendamentos.controller;

import com.swetonyancelmo.agendamentos.dtos.request.CreateCustomerDto;
import com.swetonyancelmo.agendamentos.dtos.response.CustomerDto;
import com.swetonyancelmo.agendamentos.services.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customers", description = "Gerenciamento de Clientes")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping(value = "/register")
    public ResponseEntity<CustomerDto> create(@RequestBody @Valid CreateCustomerDto dto){
      return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping(value = "/listAll")
    public ResponseEntity<List<CustomerDto>> findAll(){
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping(value = "/search/{email}")
    public ResponseEntity<CustomerDto> findByEmail(@PathVariable String email){
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @PutMapping(value = "/update/{email}")
    public ResponseEntity<CustomerDto> update(@PathVariable String email, @RequestBody @Valid CreateCustomerDto dto){
        return ResponseEntity.ok(service.update(email, dto));
    }

    @DeleteMapping(value = "/delete/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String email){
        service.delete(email);
    }

}
