package com.rites.ehc.controller;

import com.rites.ehc.service.HospitalService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HospitalController {
    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/hospitals")
    public String hospitals() {
        return hospitalService.listHospitalsJson();
    }

    @PostMapping("/hospitals")
    public String addHospital(@RequestBody String body) {
        return hospitalService.addHospital(body);
    }

    @PutMapping("/hospitals/{vendorCode}/rates")
    public String updateRates(@PathVariable String vendorCode, @RequestBody String body) {
        return hospitalService.updateRates(vendorCode, body);
    }
}
