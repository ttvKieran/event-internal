package com.example.iam_service.infrastructure.security;

import com.example.iam_service.domain.model.Employee;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;

    private final long ACCESS_TOKEN_EXPIRATION = 3600000;
    private final long REFRESH_TOKEN_EXPIRATION = 604800000;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Employee employee) {
        return Jwts.builder()
            .subject(employee.getId())
            .claim("role", employee.getRole() != null ? employee.getRole().getName() : "EMPLOYEE")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
            .signWith(getSigningKey())
            .compact();
    }

    public String generateRefreshToken(Employee employee) {
        return Jwts.builder()
            .subject(employee.getId())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
            .signWith(getSigningKey())
            .compact();
    }
}
