package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.config.TokenConfig;
import com.swetonyancelmo.agendamentos.dtos.request.BusinessLoginDto;
import com.swetonyancelmo.agendamentos.dtos.response.LoginDto;
import com.swetonyancelmo.agendamentos.models.Business;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public AuthService(AuthenticationManager authenticationManager, TokenConfig tokenConfig) {
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    public LoginDto businessLogin(BusinessLoginDto dto) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        Business business = (Business) authentication.getPrincipal();
        String token = tokenConfig.generateToken(business);

        return new LoginDto(token);
    }

}
