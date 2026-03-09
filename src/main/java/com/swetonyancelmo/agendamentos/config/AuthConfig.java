package com.swetonyancelmo.agendamentos.config;

import com.swetonyancelmo.agendamentos.repositories.BusinessRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthConfig implements UserDetailsService {

    private final BusinessRepository businessRepository;

    public AuthConfig(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return businessRepository.findBusinessByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Empresa não encontrada com email: " + username));
    }
}
