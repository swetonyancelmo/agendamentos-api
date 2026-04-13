package com.swetonyancelmo.agendamentos.controller;

import com.swetonyancelmo.agendamentos.controller.docs.CustomerControllerDocs;
import com.swetonyancelmo.agendamentos.dtos.response.CustomerDto;
import com.swetonyancelmo.agendamentos.services.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Endpoints para gerenciamento dos Customers")
public class CustomerController implements CustomerControllerDocs {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping()
    @Override
    public ResponseEntity<List<CustomerDto>> getAllCustomerByStatusCompleted() {
        return ResponseEntity.ok(customerService.findAllCustomerByStatusCompleted());
    }

}
