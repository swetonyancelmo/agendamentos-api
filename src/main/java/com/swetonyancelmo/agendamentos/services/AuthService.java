package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.config.TokenConfig;
import com.swetonyancelmo.agendamentos.dtos.request.BusinessLoginDto;
import com.swetonyancelmo.agendamentos.dtos.request.CustomerLoginDto;
import com.swetonyancelmo.agendamentos.dtos.response.LoginDto;
import com.swetonyancelmo.agendamentos.models.Business;
import com.swetonyancelmo.agendamentos.models.Customer;
import com.swetonyancelmo.agendamentos.repositories.CustomerRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, TokenConfig tokenConfig,
                       CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginDto businessLogin(BusinessLoginDto dto) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        Business business = (Business) authentication.getPrincipal();
        String token = tokenConfig.generateToken(business);

        return new LoginDto(token);
    }

    public LoginDto customerLogin(CustomerLoginDto dto) {
        Customer customer = customerRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BadCredentialsException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(dto.password(), customer.getPassword())) {
            throw new BadCredentialsException("Email ou senha inválidos");
        }

        String token = tokenConfig.generateToken(customer);
        return new LoginDto(token);
    }

}
