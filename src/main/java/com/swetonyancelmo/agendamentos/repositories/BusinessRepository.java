package com.swetonyancelmo.agendamentos.repositories;

import com.swetonyancelmo.agendamentos.models.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessRepository extends JpaRepository<Business, UUID> {

    Optional<UserDetails> findBusinessByEmail(String email);

    boolean existsByEmail(String email);
}
