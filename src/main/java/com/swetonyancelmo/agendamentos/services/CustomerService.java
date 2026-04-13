package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.dtos.request.CreateCustomerDto;
import com.swetonyancelmo.agendamentos.dtos.response.CustomerDto;
import com.swetonyancelmo.agendamentos.exceptions.CustomerRuleException;
import com.swetonyancelmo.agendamentos.mapper.CustomerMapper;
import com.swetonyancelmo.agendamentos.models.Customer;
import com.swetonyancelmo.agendamentos.models.Role;
import com.swetonyancelmo.agendamentos.repositories.AppointmentRepository;
import com.swetonyancelmo.agendamentos.repositories.CustomerRepository;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AppointmentRepository appointmentRepository;

    public CustomerService(CustomerRepository repository, CustomerMapper mapper, PasswordEncoder passwordEncoder, AppointmentRepository appointmentRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.appointmentRepository = appointmentRepository;
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

    @Transactional(readOnly = true)
    public List<CustomerDto> findAllCustomerByStatusCompleted() {
        return appointmentRepository.findDistinctCustomersWithCompletedAppointments().stream()
                .map(customer -> new CustomerDto(
                        customer.getId(),
                        customer.getName(),
                        customer.getPhone(),
                        customer.getEmail()
                ))
                .toList();
    }

}
