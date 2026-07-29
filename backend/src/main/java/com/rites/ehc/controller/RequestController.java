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
    public String requests(
            @RequestParam(value = "page",   defaultValue = "0")  int page,
            @RequestParam(value = "size",   defaultValue = "10") int size,
            @RequestParam(value = "search", defaultValue = "")   String search) {
        return requestService.listRequestsPagedJson(page, size, search);
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

    @PostMapping("/requests/{ehcId}/bill")
    public String uploadBill(
            @PathVariable String ehcId,
            @RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile file,
            @RequestParam("billDetails") String billDetails) {
        return requestService.uploadBill(ehcId, billDetails, file);
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
