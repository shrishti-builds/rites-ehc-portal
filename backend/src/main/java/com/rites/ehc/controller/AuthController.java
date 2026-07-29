package com.rites.ehc.controller;

import com.rites.ehc.security.JwtTokenProvider;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenProvider tokenProvider;

    public AuthController(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Presentation-friendly endpoint to get a valid JWT for a specific role without real credentials.
     * Roles: EMPLOYEE, SBU, HR, FINANCE
     */
    @GetMapping("/demo-login")
    public Map<String, String> demoLogin(@RequestParam(defaultValue = "EMPLOYEE") String role) {
        // We'll use a generic username based on the role for demo purposes
        String username = "demo_" + role.toLowerCase();
        String token = tokenProvider.generateToken(username, "ROLE_" + role.toUpperCase());
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", role.toUpperCase());
        return response;
    }
}
