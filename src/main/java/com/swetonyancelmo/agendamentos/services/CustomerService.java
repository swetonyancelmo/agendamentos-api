package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.dtos.request.CreateCustomerDto;
import com.swetonyancelmo.agendamentos.dtos.response.CustomerDto;
import com.swetonyancelmo.agendamentos.exceptions.CustomerRuleException;
import com.swetonyancelmo.agendamentos.mapper.CustomerMapper;
import com.swetonyancelmo.agendamentos.models.Customer;
import com.swetonyancelmo.agendamentos.models.Role;
import com.swetonyancelmo.agendamentos.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository repository, CustomerMapper mapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CustomerDto create(CreateCustomerDto dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new CustomerRuleException("Cliente já cadastrado com esse e-mail");
        }

        Customer customer = new Customer();
        customer.setName(dto.name());
        customer.setPhone(dto.phone());
        customer.setEmail(dto.email());
        customer.setPassword(passwordEncoder.encode(dto.password()));
        customer.setRole(Role.ROLE_CUSTOMER);

        return mapper.toDto(repository.save(customer));
    }

}
