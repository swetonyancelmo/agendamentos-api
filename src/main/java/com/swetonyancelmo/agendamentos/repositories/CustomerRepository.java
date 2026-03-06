package com.swetonyancelmo.agendamentos.repositories;

import com.swetonyancelmo.agendamentos.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Esta sendo colocado no MVP mas não será implementado, talvez em uma V2
    boolean existsByPhone(String phone);
}
