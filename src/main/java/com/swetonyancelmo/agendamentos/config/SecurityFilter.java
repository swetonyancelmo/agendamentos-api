package com.swetonyancelmo.agendamentos.config;

import com.swetonyancelmo.agendamentos.repositories.BusinessRepository;
import com.swetonyancelmo.agendamentos.repositories.CustomerRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenConfig tokenConfig;
    private final BusinessRepository businessRepository;
    private final CustomerRepository customerRepository;

    public SecurityFilter(TokenConfig tokenConfig, BusinessRepository businessRepository, CustomerRepository customerRepository) {
        this.tokenConfig = tokenConfig;
        this.businessRepository = businessRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && !authorizationHeader.isBlank() && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            Optional<JWTUserData> optUserData = tokenConfig.validateToken(token);

            if (optUserData.isPresent()) {
                JWTUserData userData = optUserData.get();
                String role = userData.role();
                UserDetails userDetails = null;

                if ("ROLE_BUSINESS".equals(role) && userData.userId() != null) {
                    userDetails = businessRepository.findById(userData.userId()).orElse(null);
                } else if ("ROLE_CUSTOMER".equals(role) && userData.userId() != null) {
                    userDetails = customerRepository.findById(userData.userId()).orElse(null);
                }

                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
