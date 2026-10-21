package com.example.order_management.service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.issuer:order-management-api}") String issuer,
            @Value("${app.security.jwt.expiration-minutes:15}") long expirationMinutes
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");

        this.jwtEncoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(key)
        );
        this.issuer = issuer;
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(UUID userId, String email) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationMinutes * 60))
                .claim("email", email)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}