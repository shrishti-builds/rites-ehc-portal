package com.rites.ehc.service;

import com.rites.ehc.JdbcRepository;
import org.springframework.stereotype.Service;

@Service
public class HealthService {
    public String health() {
        return "{\"success\":true,\"message\":\"Backend is healthy\",\"data\":{\"requests\":" +
                JdbcRepository.listRequests().size() + ",\"hospitals\":" + JdbcRepository.listHospitals().size() + "}}";
    }
}
