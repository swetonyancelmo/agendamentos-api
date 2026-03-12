package com.swetonyancelmo.agendamentos.config;

import com.swetonyancelmo.agendamentos.models.Business;
import com.swetonyancelmo.agendamentos.repositories.BusinessRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenConfig tokenConfig;
    private final BusinessRepository businessRepository;

    public SecurityFilter(TokenConfig tokenConfig, BusinessRepository businessRepository) {
        this.tokenConfig = tokenConfig;
        this.businessRepository = businessRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && !authorizationHeader.isBlank() && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            Optional<JWTUserData> optUserData = tokenConfig.validateToken(token);

            if (optUserData.isPresent()) {
                JWTUserData userData = optUserData.get();
                Optional<Business> optBusiness = userData.businessId() != null
                        ? businessRepository.findById(userData.businessId())
                        : Optional.empty();
                if (optBusiness.isPresent()) {
                    Business business = optBusiness.get();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(business, null, business.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
