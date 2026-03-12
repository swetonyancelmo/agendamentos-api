package com.swetonyancelmo.agendamentos.repositories;

import com.swetonyancelmo.agendamentos.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByEmail(String email);
    Boolean existsByEmail(String email);
    void deleteByEmail(String email);

}
