package com.rites.ehc.service;

import com.rites.ehc.JdbcRepository;
import com.rites.ehc.JsonUtil;
import org.springframework.stereotype.Service;

@Service
public class HospitalService {
    public String listHospitalsJson() {
        return "[" + String.join(",", JdbcRepository.listHospitals()) + "]";
    }

    public String addHospital(String body) {
        String vendorCode = JsonUtil.jsonValue(body, "vendorCode").orElse("");
        String hospitalName = JsonUtil.jsonValue(body, "hospitalName").orElse(JsonUtil.jsonValue(body, "name").orElse(""));
        if (vendorCode.isEmpty() || hospitalName.isEmpty()) {
            return "{\"success\":false,\"message\":\"Vendor code and hospital name are required\"}";
        }
        if (JdbcRepository.hospitalExists(vendorCode)) {
            return "{\"success\":false,\"message\":\"Hospital vendor code already exists\"}";
        }
        JdbcRepository.insertHospital(body);
        String hospital = JsonUtil.replaceOrAddField(body, "name", hospitalName);
        return "{\"success\":true,\"message\":\"Hospital added successfully\",\"data\":" + hospital + "}";
    }

    public String updateRates(String vendorCode, String body) {
        double rateMale = Double.parseDouble(JsonUtil.jsonValue(body, "rateMale").orElse("0"));
        double rateFemale = Double.parseDouble(JsonUtil.jsonValue(body, "rateFemale").orElse("0"));
        if (JdbcRepository.updateHospitalRates(vendorCode, rateMale, rateFemale)) {
            return "{\"success\":true,\"message\":\"Hospital rates updated successfully\"}";
        }
        return "{\"success\":false,\"message\":\"Hospital not found\"}";
    }
}
