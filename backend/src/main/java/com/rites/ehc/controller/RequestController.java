package com.rites.ehc.controller;

import com.rites.ehc.service.RequestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RequestController {
    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/requests")
    public String requests() {
        return requestService.listRequestsJson();
    }

    @GetMapping("/requests/{ehcId}")
    public String request(@PathVariable String ehcId) {
        return requestService.getRequestJson(ehcId);
    }

    @PostMapping("/requests")
    public String create(@RequestBody String body) {
        return requestService.createRequest(body);
    }

    @PutMapping("/requests/{ehcId}")
    public String updateStatus(@PathVariable String ehcId, @RequestBody String body) {
        return requestService.updateStatus(ehcId, body);
    }

    @PutMapping("/requests/{ehcId}/bill")
    public String uploadBill(@PathVariable String ehcId, @RequestBody String body) {
        return requestService.uploadBill(ehcId, body);
    }

    @PutMapping("/requests/{ehcId}/approve-bill")
    public String approveBill(@PathVariable String ehcId, @RequestBody String body) {
        return requestService.approveBill(ehcId, body);
    }

    @PutMapping("/requests/{ehcId}/reject-bill")
    public String rejectBill(@PathVariable String ehcId, @RequestBody String body) {
        return requestService.rejectBill(ehcId, body);
    }

    @PutMapping("/requests/{ehcId}/disburse")
    public String disburse(@PathVariable String ehcId, @RequestBody String body) {
        return requestService.disburse(ehcId, body);
    }
}
