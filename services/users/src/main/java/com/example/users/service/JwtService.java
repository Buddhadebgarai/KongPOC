package com.example.users.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    // Must match Kong's configuration: my-secret-key
    private static final String SECRET = "my-super-secret-key-1234567890123456";
    private static final long EXPIRATION_TIME_MS = 24 * 60 * 60 * 1000; // 24 hours

    /**
     * Generate a JWT token with Kong-compatible claims
     * 
     * @return JWT token string
     */
    public String generateToken() {
        long now = System.currentTimeMillis();
        long expiryTime = now + EXPIRATION_TIME_MS;

        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claim("kid", "poc-key")
                .claim("iss", "kong")                    // Issuer
                .claim("sub", "poc-user")                // Subject (consumer name in Kong)
                .claim("aud", "kong")                    // Audience
                .claim("iat", now / 1000)                // Issued at (seconds)
                .claim("exp", expiryTime / 1000)         // Expiration (seconds)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Generate a JWT token with custom expiration time
     * 
     * @param expirationHours Expiration time in hours
     * @return JWT token string
     */
    public String generateTokenWithExpiration(int expirationHours) {
        long now = System.currentTimeMillis();
        long expiryTime = now + (expirationHours * 60 * 60 * 1000L);

        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claim("iss", "kong")
                .claim("sub", "poc-user")
                .claim("aud", "kong")
                .claim("iat", now / 1000)
                .claim("exp", expiryTime / 1000)
                .signWith(secretKey)
                .compact();
    }
}
