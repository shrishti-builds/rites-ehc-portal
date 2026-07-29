package com.rites.ehc.service;

import com.rites.ehc.JdbcRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    public String getEmployeeJson(String empNo) {
        return JdbcRepository.findEmployee(empNo);
    }
}
