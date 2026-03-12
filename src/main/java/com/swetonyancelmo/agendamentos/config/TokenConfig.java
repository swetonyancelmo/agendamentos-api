package com.swetonyancelmo.agendamentos.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.swetonyancelmo.agendamentos.models.Business;
import com.swetonyancelmo.agendamentos.models.Customer;
import com.swetonyancelmo.agendamentos.models.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class TokenConfig {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(Business business) {
        return generateToken(business.getId(), business.getEmail(), business.getRole());
    }

    public String generateToken(Customer customer) {
        return generateToken(customer.getId(), customer.getEmail(), customer.getRole());
    }

    private String generateToken(UUID userId, String email, Role role) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withClaim("userId", userId.toString())
                .withClaim("role", role.name())
                .withSubject(email)
                .withExpiresAt(Instant.now().plusSeconds(86400))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decode = JWT.require(algorithm)
                    .build().verify(token);

            String userIdStr = decode.getClaim("userId").asString();
            String role = decode.getClaim("role").asString();

            return Optional.of(JWTUserData.builder()
                    .userId(userIdStr != null ? UUID.fromString(userIdStr) : null)
                    .email(decode.getSubject())
                    .role(role)
                    .build());

        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }

}
