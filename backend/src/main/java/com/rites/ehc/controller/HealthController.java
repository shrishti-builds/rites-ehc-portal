package com.rites.ehc.controller;

import com.rites.ehc.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {
    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public String root() {
        return "{\"success\":true,\"message\":\"RITES EHC backend is running\",\"data\":{\"health\":\"/api/health\",\"cities\":\"/api/cities\",\"hospitals\":\"/api/hospitals\",\"requests\":\"/api/requests\"}}";
    }

    @GetMapping("/health")
    public String health() {
        return healthService.health();
    }
}
