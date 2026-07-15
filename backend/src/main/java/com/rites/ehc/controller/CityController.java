package com.rites.ehc.controller;

import com.rites.ehc.service.CityService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/cities")
    public String cities() {
        return cityService.listCitiesJson();
    }

    @PostMapping("/cities")
    public String addCity(@RequestBody String body) {
        return cityService.addCity(body);
    }

}
