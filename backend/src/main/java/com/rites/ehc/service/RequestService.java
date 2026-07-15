package com.rites.ehc.service;

import com.rites.ehc.JdbcRepository;
import com.rites.ehc.JsonUtil;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    public String listRequestsJson() {
        return "[" + String.join(",", JdbcRepository.listRequests()) + "]";
    }

    public String getRequestJson(String ehcId) {
        String json = JdbcRepository.findRequest(ehcId);
        return json != null ? json : "{\"success\":false,\"message\":\"Request not found\"}";
    }

    public String createRequest(String body) {
        if (JsonUtil.jsonValue(body, "empNo").orElse("").isEmpty()
                || JsonUtil.jsonValue(body, "hospitalName").orElse("").isEmpty()
                || !body.contains("\"dependents\"")) {
            return "{\"success\":false,\"message\":\"Employee, hospital and dependents are required\"}";
        }
        String created = JdbcRepository.createRequest(body);
        return "{\"success\":true,\"message\":\"Health checkup request submitted successfully\",\"data\":" + created + "}";
    }

    public String updateStatus(String ehcId, String body) {
        String status = JsonUtil.jsonValue(body, "status").orElse("");
        String remarks = JsonUtil.jsonValue(body, "remarks").orElse("");
        if (!JdbcRepository.updateRequestStatus(ehcId, status, remarks)) {
            return "{\"success\":false,\"message\":\"Request not found\"}";
        }
        return "{\"success\":true,\"message\":\"Request status updated to " + JsonUtil.escape(status) + "\",\"data\":" + JdbcRepository.findRequest(ehcId) + "}";
    }

    public String uploadBill(String ehcId, String body) {
        if (JdbcRepository.updateRequestBill(ehcId, body)) {
            return "{\"success\":true,\"message\":\"Bill uploaded successfully\",\"data\":" + JdbcRepository.findRequest(ehcId) + "}";
        }
        return "{\"success\":false,\"message\":\"Request not found\"}";
    }

    public String approveBill(String ehcId, String body) {
        String financeRemarks = JsonUtil.jsonValue(body, "financeRemarks").orElse("");
        if (JdbcRepository.updateRequestFinanceAction(ehcId, "Bill Approved", financeRemarks)) {
            return "{\"success\":true,\"message\":\"Bill approved by finance\",\"data\":" + JdbcRepository.findRequest(ehcId) + "}";
        }
        return "{\"success\":false,\"message\":\"Request not found\"}";
    }

    public String rejectBill(String ehcId, String body) {
        String financeRemarks = JsonUtil.jsonValue(body, "financeRemarks").orElse("");
        if (JdbcRepository.updateRequestFinanceAction(ehcId, "Bill Rejected", financeRemarks)) {
            return "{\"success\":true,\"message\":\"Bill rejected by finance\",\"data\":" + JdbcRepository.findRequest(ehcId) + "}";
        }
        return "{\"success\":false,\"message\":\"Request not found\"}";
    }

    public String disburse(String ehcId, String body) {
        if (JdbcRepository.updateRequestDisbursement(ehcId, body)) {
            return "{\"success\":true,\"message\":\"Disbursement completed successfully\",\"data\":" + JdbcRepository.findRequest(ehcId) + "}";
        }
        return "{\"success\":false,\"message\":\"Request not found\"}";
    }
}
