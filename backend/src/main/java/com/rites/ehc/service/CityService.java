package com.rites.ehc.service;

import com.rites.ehc.JdbcRepository;
import com.rites.ehc.JsonUtil;
import org.springframework.stereotype.Service;

@Service
public class CityService {
    public String listCitiesJson() {
        return "[" + String.join(",", JdbcRepository.listCities()) + "]";
    }

    public String addCity(String body) {
        String state = JsonUtil.jsonValue(body, "state").orElse(JsonUtil.jsonValue(body, "stateName").orElse(""));
        String city = JsonUtil.jsonValue(body, "city").orElse(JsonUtil.jsonValue(body, "cityName").orElse(""));
        if (state.isBlank() || city.isBlank()) {
            return "{\"success\":false,\"message\":\"State and city are required\"}";
        }
        JdbcRepository.addCity(state, city);
        return "{\"success\":true,\"message\":\"City added successfully\"}";
    }

}
