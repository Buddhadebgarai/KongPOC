package com.example.users.controller;

import com.example.users.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtService jwtService;

    /**
     * Generate a JWT token for testing Kong Gateway
     * This endpoint is NOT protected by Kong JWT plugin
     * Use this to get a token, then use it to access protected endpoints
     * 
     * Example:
     *   GET http://localhost:8080/api/v1/auth/token
     *   Response: { "token": "eyJhbGci..." }
     * 
     *   Then use in requests:
     *   GET http://localhost:8000/users
     *   Authorization: Bearer eyJhbGci...
     */
    @GetMapping(path = "/token")
    public ResponseEntity<Map<String, Object>> generateToken() {
        log.info("Generating JWT token");
        
        String token = jwtService.generateToken();
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("expires_in", 24 * 60 * 60); // 24 hours in seconds
        response.put("message", "Use this token in Kong Gateway requests: Authorization: Bearer <token>");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Generate a JWT token with custom expiration
     * 
     * Example:
     *   GET http://localhost:8080/api/v1/auth/token?expirationHours=48
     */
    @GetMapping(path = "/token/{expirationHours}")
    public ResponseEntity<Map<String, Object>> generateTokenWithExpiration(
            @PathVariable int expirationHours) {
        
        log.info("Generating JWT token with expiration: {} hours", expirationHours);
        
        if (expirationHours <= 0 || expirationHours > 720) { // Max 30 days
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Expiration hours must be between 1 and 720");
            return ResponseEntity.badRequest().body(error);
        }
        
        String token = jwtService.generateTokenWithExpiration(expirationHours);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("expires_in", expirationHours * 60 * 60);
        response.put("message", "Use this token in Kong Gateway requests: Authorization: Bearer <token>");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check for auth service
     */
    @GetMapping(path = "/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "auth");
        return ResponseEntity.ok(response);
    }
}
