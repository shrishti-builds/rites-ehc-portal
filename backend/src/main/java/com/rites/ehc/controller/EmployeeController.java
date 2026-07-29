package com.rites.ehc.controller;

import com.rites.ehc.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees/{empNo}")
    public ResponseEntity<String> getEmployee(@PathVariable String empNo) {
        String json = employeeService.getEmployeeJson(empNo);
        if (json == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(json);
    }
}
