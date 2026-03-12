package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.dtos.request.CreateCustomerDto;
import com.swetonyancelmo.agendamentos.dtos.response.CustomerDto;
import com.swetonyancelmo.agendamentos.exceptions.CustomerRuleException;
import com.swetonyancelmo.agendamentos.mapper.CustomerMapper;
import com.swetonyancelmo.agendamentos.models.Customer;
import com.swetonyancelmo.agendamentos.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Transactional
    public CustomerDto create(CreateCustomerDto dto){
        if(repository.existsByEmail(dto.email())){
            throw new CustomerRuleException("Cliente já cadastrado com esse e-mail");
        }
        Customer customer = mapper.toEntity(dto);
        return mapper.toDto(repository.save(customer));
    }

    public List<CustomerDto> findAll(){
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public CustomerDto findByEmail(String email){
        return repository.findByEmail(email)
                .map(mapper::toDto)
                .orElseThrow(() -> new CustomerRuleException("Cliente não encontrado com o e-mail: " + email));
    }

    @Transactional
    public CustomerDto update(String email, CreateCustomerDto dto){
        Customer customer = repository.findByEmail(email)
                .orElseThrow(() -> new CustomerRuleException("Cliente não encontrado"));

        if(!customer.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())){
            throw new CustomerRuleException("Cliente já cadastrado com esse e-mail");
        }

        customer.setName(dto.name());
        customer.setPhone(dto.phone());
        customer.setEmail(dto.email());

        return mapper.toDto(repository.save(customer));
    }


    public void delete(String email){
        if(!repository.existsByEmail(email)){
            throw new CustomerRuleException("Não é possível deletar: cliente não encontrado.");
        }
        repository.deleteByEmail(email);
    }

}
