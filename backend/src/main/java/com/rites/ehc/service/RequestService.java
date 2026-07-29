package com.rites.ehc.service;

import com.rites.ehc.JdbcRepository;
import com.rites.ehc.JsonUtil;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RequestService {
    private final EmailService emailService;

    public RequestService(EmailService emailService) {
        this.emailService = emailService;
    }

    public String listRequestsJson() {
        return "[" + String.join(",", JdbcRepository.listRequests()) + "]";
    }

    /**
     * Returns a paginated + searchable response as JSON.
     * Response shape: { content:[], page, size, totalElements, totalPages, last }
     */
    public String listRequestsPagedJson(int page, int size, String search) {
        // clamp size to sane bounds
        if (size <= 0) size = 10;
        if (size > 100) size = 100;
        if (page < 0) page = 0;

        List<String> rows = JdbcRepository.listRequestsPaged(page, size, search);
        long total = JdbcRepository.countRequests(search);
        int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
        boolean last = page >= (totalPages - 1);

        String content = "[" + String.join(",", rows) + "]";
        return "{\"content\":" + content
                + ",\"page\":" + page
                + ",\"size\":" + size
                + ",\"totalElements\":" + total
                + ",\"totalPages\":" + totalPages
                + ",\"last\":" + last + "}";
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

        // Trigger Email Notification
        // Note: For a real app, we'd fetch the employee email from DB. 
        // Using a mock email based on ehcId for demo.
        emailService.sendStatusUpdateEmail("employee_" + ehcId + "@rites.com", ehcId, status, "Employee");

        return "{\"success\":true,\"message\":\"Request status updated to " + JsonUtil.escape(status) + "\",\"data\":" + JdbcRepository.findRequest(ehcId) + "}";
    }

    public String uploadBill(String ehcId, String body, org.springframework.web.multipart.MultipartFile file) {
        // Step 4: Save the uploaded file to disk
        String filePath = "";
        if (file != null && !file.isEmpty()) {
            try {
                java.io.File uploadsDir = new java.io.File("uploads");
                if (!uploadsDir.exists()) uploadsDir.mkdir();
                
                String originalFilename = file.getOriginalFilename();
                String ext = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
                String newFilename = ehcId + "_" + System.currentTimeMillis() + ext;
                
                java.io.File dest = new java.io.File(uploadsDir, newFilename);
                file.transferTo(dest);
                filePath = dest.getAbsolutePath();
            } catch (Exception e) {
                return "{\"success\":false,\"message\":\"Failed to save file: " + e.getMessage() + "\"}";
            }
        }
        
        // Inject filePath into the body JSON if we saved a file
        if (!filePath.isEmpty()) {
            body = body.replace("}", ",\"uploadedFilePath\":\"" + filePath.replace("\\", "\\\\") + "\"}");
        }

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
