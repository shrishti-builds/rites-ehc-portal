package com.rites.ehc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JdbcRepository {
    private JdbcRepository() {
    }

    public static List<String> listCities() {
        List<String> rows = new ArrayList<>();
        String sql = "SELECT state_name, city_name FROM ehc_cities WHERE ISNULL(active,1)=1 ORDER BY state_name, city_name";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            String currentState = null;
            List<String> currentCities = new ArrayList<>();
            while (rs.next()) {
                String state = rs.getString("state_name");
                String city = rs.getString("city_name");
                if (currentState == null) {
                    currentState = state;
                }
                if (!state.equals(currentState)) {
                    rows.add("{\"state\":\"" + JsonUtil.escape(currentState) + "\",\"cities\":[" + toJsonArray(currentCities) + "]}");
                    currentState = state;
                    currentCities = new ArrayList<>();
                }
                currentCities.add(city);
            }
            if (currentState != null) {
                rows.add("{\"state\":\"" + JsonUtil.escape(currentState) + "\",\"cities\":[" + toJsonArray(currentCities) + "]}");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    public static List<String> listHospitals() {
        List<String> rows = new ArrayList<>();
        String sql = "SELECT * FROM ehc_hospitals WHERE ISNULL(active,1)=1 ORDER BY hospital_name";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(toHospitalJson(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    public static boolean hospitalExists(String vendorCode) {
        String sql = "SELECT 1 FROM ehc_hospitals WHERE vendor_code = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vendorCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertHospital(String jsonBody) {
        String sql = "INSERT INTO ehc_hospitals(vendor_code, hospital_name, address1, address2, state_name, city_name, pincode, phone_l, contact_person, contact_designation, contact_email, contact_m, alt_contact_person, alt_contact_designation, alt_contact_email, alt_contact_m, rate_male, rate_female, valid_upto, concession_info, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, JsonUtil.jsonValue(jsonBody, "vendorCode").orElse(""));
            ps.setString(2, JsonUtil.jsonValue(jsonBody, "name").orElse(JsonUtil.jsonValue(jsonBody, "hospitalName").orElse("")));
            ps.setString(3, JsonUtil.jsonValue(jsonBody, "address1").orElse(""));
            ps.setString(4, JsonUtil.jsonValue(jsonBody, "address2").orElse(null));
            ps.setString(5, JsonUtil.jsonValue(jsonBody, "state").orElse(""));
            ps.setString(6, JsonUtil.jsonValue(jsonBody, "city").orElse(""));
            ps.setString(7, JsonUtil.jsonValue(jsonBody, "pincode").orElse(""));
            ps.setString(8, JsonUtil.jsonValue(jsonBody, "phoneL").orElse(null));
            ps.setString(9, JsonUtil.jsonValue(jsonBody, "contactPerson").orElse(""));
            ps.setString(10, JsonUtil.jsonValue(jsonBody, "contactDesignation").orElse(""));
            ps.setString(11, JsonUtil.jsonValue(jsonBody, "contactEmail").orElse(""));
            ps.setString(12, JsonUtil.jsonValue(jsonBody, "contactM").orElse(""));
            ps.setString(13, JsonUtil.jsonValue(jsonBody, "altContactPerson").orElse(null));
            ps.setString(14, JsonUtil.jsonValue(jsonBody, "altContactDesignation").orElse(null));
            ps.setString(15, JsonUtil.jsonValue(jsonBody, "altContactEmail").orElse(null));
            ps.setString(16, JsonUtil.jsonValue(jsonBody, "altContactM").orElse(null));
            ps.setBigDecimal(17, new java.math.BigDecimal(JsonUtil.jsonValue(jsonBody, "rateMale").orElse("0")));
            ps.setBigDecimal(18, new java.math.BigDecimal(JsonUtil.jsonValue(jsonBody, "rateFemale").orElse("0")));
            ps.setDate(19, Date.valueOf(JsonUtil.jsonValue(jsonBody, "validUpto").orElse("1970-01-01")));
            ps.setString(20, JsonUtil.jsonValue(jsonBody, "concessionInfo").orElse(null));
            ps.setString(21, JsonUtil.jsonValue(jsonBody, "remarks").orElse(null));
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String findEmployee(String empNo) {
        String sql = "SELECT * FROM ehc_employees WHERE emp_no = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return toEmployeeJson(conn, rs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> listRequests() {
        List<String> rows = new ArrayList<>();
        String sql = "SELECT * FROM ehc_requests ORDER BY created_at DESC, request_id DESC";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(toRequestJson(conn, rs));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    /**
     * Returns one page of requests, optionally filtered by a search keyword.
     * Search matches ehc_id, emp_name, hospital_name, or status (case-insensitive).
     */
    public static List<String> listRequestsPaged(int page, int size, String search) {
        List<String> rows = new ArrayList<>();
        boolean hasSearch = search != null && !search.trim().isEmpty();
        String where = hasSearch
                ? "WHERE LOWER(ehc_id) LIKE ? OR LOWER(emp_name) LIKE ? OR LOWER(hospital_name) LIKE ? OR LOWER(status) LIKE ?"
                : "";
        String sql = "SELECT * FROM ehc_requests " + where
                + " ORDER BY created_at DESC, request_id DESC"
                + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIdx = 1;
            if (hasSearch) {
                String like = "%" + search.trim().toLowerCase() + "%";
                ps.setString(paramIdx++, like);
                ps.setString(paramIdx++, like);
                ps.setString(paramIdx++, like);
                ps.setString(paramIdx++, like);
            }
            ps.setInt(paramIdx++, page * size);   // OFFSET
            ps.setInt(paramIdx,   size);           // FETCH NEXT
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(toRequestJson(conn, rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rows;
    }

    /**
     * Counts total requests matching the optional search keyword.
     */
    public static long countRequests(String search) {
        boolean hasSearch = search != null && !search.trim().isEmpty();
        String where = hasSearch
                ? "WHERE LOWER(ehc_id) LIKE ? OR LOWER(emp_name) LIKE ? OR LOWER(hospital_name) LIKE ? OR LOWER(status) LIKE ?"
                : "";
        String sql = "SELECT COUNT(*) FROM ehc_requests " + where;
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hasSearch) {
                String like = "%" + search.trim().toLowerCase() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
                ps.setString(4, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String findRequest(String ehcId) {
        String sql = "SELECT * FROM ehc_requests WHERE ehc_id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ehcId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return toRequestJson(conn, rs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Long findRequestId(String ehcId) {
        String sql = "SELECT request_id FROM ehc_requests WHERE ehc_id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ehcId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getLong("request_id");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String createRequest(String jsonBody) {
        try (Connection conn = Db.getConnection()) {
            conn.setAutoCommit(false);

            // --- Auto-register employee if not present ---
            String empNo = JsonUtil.jsonValue(jsonBody, "empNo").orElse("");
            String empName = JsonUtil.jsonValue(jsonBody, "empName").orElse("");
            boolean empExists = false;
            try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM ehc_employees WHERE emp_no = ?")) {
                ps.setString(1, empNo);
                try (ResultSet rs = ps.executeQuery()) {
                    empExists = rs.next();
                }
            }
            if (!empExists && !empNo.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO ehc_employees(emp_no, emp_name, designation, division, mobile, landline, dob, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, empNo);
                    ps.setString(2, empName);
                    ps.setString(3, JsonUtil.jsonValue(jsonBody, "designation").orElse(null));
                    ps.setString(4, JsonUtil.jsonValue(jsonBody, "division").orElse(null));
                    ps.setString(5, JsonUtil.jsonValue(jsonBody, "mobile").orElse(null));
                    ps.setString(6, JsonUtil.jsonValue(jsonBody, "landline").orElse(null));
                    ps.setDate(7, Date.valueOf("1985-01-01")); // default DOB
                    ps.setString(8, null);
                    ps.executeUpdate();
                }
                // Insert Self dependent for auto-registered employee
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO ehc_employee_dependents(emp_no, dependent_name, relation, dob, gender) VALUES (?, ?, ?, ?, ?)")) {
                    ps.setString(1, empNo);
                    ps.setString(2, empName.isEmpty() ? "Self" : empName);
                    ps.setString(3, "Self");
                    ps.setDate(4, Date.valueOf("1985-01-01"));
                    ps.setString(5, null);
                    ps.executeUpdate();
                }
            }

            // --- Insert the request ---
            String ehcId = "EHC-" + String.valueOf(System.currentTimeMillis()).substring(7);
            String insert = "INSERT INTO ehc_requests(ehc_id, emp_no, emp_name, designation, division, mobile, landline, pu_head, state_name, city_name, hospital_name, status, remarks, submission_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, ehcId);
                ps.setString(2, empNo);
                ps.setString(3, empName);
                ps.setString(4, JsonUtil.jsonValue(jsonBody, "designation").orElse(null));
                ps.setString(5, JsonUtil.jsonValue(jsonBody, "division").orElse(null));
                ps.setString(6, JsonUtil.jsonValue(jsonBody, "mobile").orElse(""));
                ps.setString(7, JsonUtil.jsonValue(jsonBody, "landline").orElse(null));
                ps.setString(8, JsonUtil.jsonValue(jsonBody, "puHead").orElse(""));
                ps.setString(9, JsonUtil.jsonValue(jsonBody, "state").orElse(null));
                ps.setString(10, JsonUtil.jsonValue(jsonBody, "city").orElse(null));
                ps.setString(11, JsonUtil.jsonValue(jsonBody, "hospitalName").orElse(""));
                ps.setString(12, "Pending SBU");
                ps.setString(13, "");
                ps.setDate(14, Date.valueOf(java.time.LocalDate.now()));
                ps.executeUpdate();
            }

            // --- Fetch generated request_id ---
            long requestId;
            try (PreparedStatement ps = conn.prepareStatement("SELECT request_id FROM ehc_requests WHERE ehc_id = ?")) {
                ps.setString(1, ehcId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    requestId = rs.getLong(1);
                }
            }

            // --- Insert dependents ---
            String depsJson = jsonArrayValue(jsonBody, "dependents").orElse("[]");
            for (String depJson : splitObjects(depsJson)) {
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ehc_request_dependents(request_id, dependent_name, relation, dob, gender) VALUES (?, ?, ?, ?, ?)")) {
                    ps.setLong(1, requestId);
                    ps.setString(2, JsonUtil.jsonValue(depJson, "name").orElse(""));
                    ps.setString(3, JsonUtil.jsonValue(depJson, "relation").orElse(""));
                    ps.setDate(4, Date.valueOf(JsonUtil.jsonValue(depJson, "dob").orElse("1970-01-01")));
                    ps.setString(5, JsonUtil.jsonValue(depJson, "gender").orElse(null));
                    ps.executeUpdate();
                }
            }

            // --- Record initial status history ---
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ehc_status_history(request_id, old_status, new_status, remarks) VALUES (?, ?, ?, ?)")) {
                ps.setLong(1, requestId);
                ps.setNull(2, java.sql.Types.VARCHAR);
                ps.setString(3, "Pending SBU");
                ps.setString(4, "Request submitted");
                ps.executeUpdate();
            }

            conn.commit();
            return findRequest(ehcId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateRequestStatus(String ehcId, String status, String remarks) {
        try (Connection conn = Db.getConnection()) {
            conn.setAutoCommit(false);
            // Fetch old status
            String oldStatus = null;
            Long requestId = null;
            try (PreparedStatement ps = conn.prepareStatement("SELECT request_id, status FROM ehc_requests WHERE ehc_id = ?")) {
                ps.setString(1, ehcId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        requestId = rs.getLong("request_id");
                        oldStatus = rs.getString("status");
                    }
                }
            }
            if (requestId == null) { conn.rollback(); return false; }
            // Update request
            try (PreparedStatement ps = conn.prepareStatement("UPDATE ehc_requests SET status = ?, remarks = ? WHERE ehc_id = ?")) {
                ps.setString(1, status);
                ps.setString(2, remarks);
                ps.setString(3, ehcId);
                ps.executeUpdate();
            }
            // Write status history
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ehc_status_history(request_id, old_status, new_status, remarks) VALUES (?, ?, ?, ?)")) {
                ps.setLong(1, requestId);
                ps.setString(2, oldStatus);
                ps.setString(3, status);
                ps.setString(4, remarks);
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean addCity(String state, String city) {
        String sql = "INSERT INTO ehc_cities(state_name, city_name) VALUES (?, ?)";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, state);
            ps.setString(2, city);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteCity(String city) {
        String sql = "DELETE FROM ehc_cities WHERE city_name = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, city);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateHospitalRates(String vendorCode, double rateMale, double rateFemale) {
        String sql = "UPDATE ehc_hospitals SET rate_male = ?, rate_female = ? WHERE vendor_code = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, rateMale);
            ps.setDouble(2, rateFemale);
            ps.setString(3, vendorCode);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateRequestBill(String ehcId, String billDetails) {
        try (Connection conn = Db.getConnection()) {
            conn.setAutoCommit(false);
            String oldStatus = null;
            Long requestId = null;
            try (PreparedStatement ps = conn.prepareStatement("SELECT request_id, status FROM ehc_requests WHERE ehc_id = ?")) {
                ps.setString(1, ehcId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        requestId = rs.getLong("request_id");
                        oldStatus = rs.getString("status");
                    }
                }
            }
            if (requestId == null) { conn.rollback(); return false; }
            // Update request
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE ehc_requests SET status = 'Bill Uploaded', bill_details = ? WHERE ehc_id = ?")) {
                ps.setString(1, billDetails);
                ps.setString(2, ehcId);
                ps.executeUpdate();
            }
            // Insert document record — parse filename from JSON if available
            String fileName = "bill_upload";
            String contentType = "application/octet-stream";
            try {
                Optional<String> fn = JsonUtil.jsonValue(billDetails, "fileName");
                if (fn.isPresent()) fileName = fn.get();
                Optional<String> ct = JsonUtil.jsonValue(billDetails, "contentType");
                if (ct.isPresent()) contentType = ct.get();
            } catch (Exception ignored) {}
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ehc_documents(request_id, document_type, file_name, file_path, content_type, uploaded_by) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setLong(1, requestId);
                ps.setString(2, "Bill");
                ps.setString(3, fileName);
                ps.setString(4, "uploads/" + ehcId + "/" + fileName);
                ps.setString(5, contentType);
                ps.setString(6, "hospital");
                ps.executeUpdate();
            }
            // Status history
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ehc_status_history(request_id, old_status, new_status, remarks) VALUES (?, ?, ?, ?)")) {
                ps.setLong(1, requestId);
                ps.setString(2, oldStatus);
                ps.setString(3, "Bill Uploaded");
                ps.setString(4, "Bill submitted by hospital");
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateRequestFinanceAction(String ehcId, String status, String financeRemarks) {
        try (Connection conn = Db.getConnection()) {
            conn.setAutoCommit(false);
            String oldStatus = null;
            Long requestId = null;
            try (PreparedStatement ps = conn.prepareStatement("SELECT request_id, status FROM ehc_requests WHERE ehc_id = ?")) {
                ps.setString(1, ehcId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        requestId = rs.getLong("request_id");
                        oldStatus = rs.getString("status");
                    }
                }
            }
            if (requestId == null) { conn.rollback(); return false; }
            // Update request
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE ehc_requests SET status = ?, finance_remarks = ? WHERE ehc_id = ?")) {
                ps.setString(1, status);
                ps.setString(2, financeRemarks);
                ps.setString(3, ehcId);
                ps.executeUpdate();
            }
            // On approval, insert payment recommendation
            if ("Bill Approved".equalsIgnoreCase(status)) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO ehc_payment_recommendations(request_id, recommended_by, total_bill_amount, company_payable_amount, employee_payable_amount, payment_mode, comments) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setLong(1, requestId);
                    ps.setString(2, "finance");
                    ps.setBigDecimal(3, java.math.BigDecimal.ZERO);
                    ps.setBigDecimal(4, java.math.BigDecimal.ZERO);
                    ps.setBigDecimal(5, java.math.BigDecimal.ZERO);
                    ps.setString(6, "NEFT");
                    ps.setString(7, financeRemarks);
                    ps.executeUpdate();
                }
            }
            // Status history
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ehc_status_history(request_id, old_status, new_status, remarks) VALUES (?, ?, ?, ?)")) {
                ps.setLong(1, requestId);
                ps.setString(2, oldStatus);
                ps.setString(3, status);
                ps.setString(4, financeRemarks);
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateRequestDisbursement(String ehcId, String disbursementDetails) {
        try (Connection conn = Db.getConnection()) {
            conn.setAutoCommit(false);
            String oldStatus = null;
            Long requestId = null;
            try (PreparedStatement ps = conn.prepareStatement("SELECT request_id, status FROM ehc_requests WHERE ehc_id = ?")) {
                ps.setString(1, ehcId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        requestId = rs.getLong("request_id");
                        oldStatus = rs.getString("status");
                    }
                }
            }
            if (requestId == null) { conn.rollback(); return false; }
            // Update request
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE ehc_requests SET status = 'Disbursed', disbursement_details = ? WHERE ehc_id = ?")) {
                ps.setString(1, disbursementDetails);
                ps.setString(2, ehcId);
                ps.executeUpdate();
            }
            // Fetch recommendation_id (latest for this request)
            long recommendationId = -1;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT TOP 1 recommendation_id FROM ehc_payment_recommendations WHERE request_id = ? ORDER BY recommendation_id DESC")) {
                ps.setLong(1, requestId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) recommendationId = rs.getLong(1);
                }
            }
            // If no recommendation exists, create a placeholder one first
            if (recommendationId == -1) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO ehc_payment_recommendations(request_id, recommended_by, total_bill_amount, company_payable_amount, employee_payable_amount, payment_mode, comments) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, requestId);
                    ps.setString(2, "finance");
                    ps.setBigDecimal(3, java.math.BigDecimal.ZERO);
                    ps.setBigDecimal(4, java.math.BigDecimal.ZERO);
                    ps.setBigDecimal(5, java.math.BigDecimal.ZERO);
                    ps.setString(6, "NEFT");
                    ps.setString(7, "Auto-created on disbursement");
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) recommendationId = keys.getLong(1);
                    }
                }
            }
            // Parse paid amount from disbursementDetails JSON
            java.math.BigDecimal paidAmount = java.math.BigDecimal.ZERO;
            String payRef = "";
            try {
                Optional<String> pa = JsonUtil.jsonValue(disbursementDetails, "paidAmount");
                if (pa.isPresent()) paidAmount = new java.math.BigDecimal(pa.get());
                Optional<String> pr = JsonUtil.jsonValue(disbursementDetails, "referenceNo");
                if (pr.isPresent()) payRef = pr.get();
            } catch (Exception ignored) {}
            // Insert payment
            if (recommendationId != -1) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO ehc_payments(request_id, recommendation_id, processed_by, payment_status, paid_amount, payment_reference_no, payment_date, finance_comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setLong(1, requestId);
                    ps.setLong(2, recommendationId);
                    ps.setString(3, "finance");
                    ps.setString(4, "Processed");
                    ps.setBigDecimal(5, paidAmount);
                    ps.setString(6, payRef.isEmpty() ? null : payRef);
                    ps.setDate(7, Date.valueOf(java.time.LocalDate.now()));
                    ps.setString(8, disbursementDetails);
                    ps.executeUpdate();
                }
            }
            // Status history
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ehc_status_history(request_id, old_status, new_status, remarks) VALUES (?, ?, ?, ?)")) {
                ps.setLong(1, requestId);
                ps.setString(2, oldStatus);
                ps.setString(3, "Disbursed");
                ps.setString(4, "Payment disbursed");
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHospitalJson(ResultSet rs) throws Exception {
        return "{" +
                "\"vendorCode\":\"" + JsonUtil.escape(rs.getString("vendor_code")) + "\"," +
                "\"name\":\"" + JsonUtil.escape(rs.getString("hospital_name")) + "\"," +
                "\"hospitalName\":\"" + JsonUtil.escape(rs.getString("hospital_name")) + "\"," +
                "\"address1\":\"" + JsonUtil.escape(rs.getString("address1")) + "\"," +
                "\"address2\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("address2")).orElse("")) + "\"," +
                "\"state\":\"" + JsonUtil.escape(rs.getString("state_name")) + "\"," +
                "\"city\":\"" + JsonUtil.escape(rs.getString("city_name")) + "\"," +
                "\"pincode\":\"" + JsonUtil.escape(rs.getString("pincode")) + "\"," +
                "\"phoneL\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("phone_l")).orElse("")) + "\"," +
                "\"contactPerson\":\"" + JsonUtil.escape(rs.getString("contact_person")) + "\"," +
                "\"contactDesignation\":\"" + JsonUtil.escape(rs.getString("contact_designation")) + "\"," +
                "\"contactEmail\":\"" + JsonUtil.escape(rs.getString("contact_email")) + "\"," +
                "\"contactM\":\"" + JsonUtil.escape(rs.getString("contact_m")) + "\"," +
                "\"altContactPerson\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("alt_contact_person")).orElse("")) + "\"," +
                "\"altContactDesignation\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("alt_contact_designation")).orElse("")) + "\"," +
                "\"altContactEmail\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("alt_contact_email")).orElse("")) + "\"," +
                "\"altContactM\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("alt_contact_m")).orElse("")) + "\"," +
                "\"rateMale\":\"" + JsonUtil.escape(rs.getBigDecimal("rate_male").toPlainString()) + "\"," +
                "\"rateFemale\":\"" + JsonUtil.escape(rs.getBigDecimal("rate_female").toPlainString()) + "\"," +
                "\"validUpto\":\"" + JsonUtil.escape(rs.getDate("valid_upto").toString()) + "\"," +
                "\"concessionInfo\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("concession_info")).orElse("")) + "\"," +
                "\"remarks\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("remarks")).orElse("")) + "\"}";
    }

    private static String toEmployeeJson(Connection conn, ResultSet rs) throws Exception {
        String empNo = rs.getString("emp_no");
        List<String> deps = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM ehc_employee_dependents WHERE emp_no = ?")) {
            ps.setString(1, empNo);
            try (ResultSet drs = ps.executeQuery()) {
                while (drs.next()) {
                    deps.add("{\"name\":\"" + JsonUtil.escape(drs.getString("dependent_name")) + "\"," +
                            "\"relation\":\"" + JsonUtil.escape(drs.getString("relation")) + "\"," +
                            "\"dob\":\"" + JsonUtil.escape(drs.getDate("dob").toString()) + "\"," +
                            "\"gender\":\"" + JsonUtil.escape(Optional.ofNullable(drs.getString("gender")).orElse("")) + "\"}");
                }
            }
        }
        return "{" +
                "\"empNo\":\"" + JsonUtil.escape(empNo) + "\"," +
                "\"name\":\"" + JsonUtil.escape(rs.getString("emp_name")) + "\"," +
                "\"designation\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("designation")).orElse("")) + "\"," +
                "\"division\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("division")).orElse("")) + "\"," +
                "\"mobile\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("mobile")).orElse("")) + "\"," +
                "\"landline\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("landline")).orElse("")) + "\"," +
                "\"dob\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getDate("dob")).map(Date::toString).orElse("")) + "\"," +
                "\"gender\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("gender")).orElse("")) + "\"," +
                "\"dependents\":[" + String.join(",", deps) + "]}";
    }

    private static String toRequestJson(Connection conn, ResultSet rs) throws Exception {
        long requestId = rs.getLong("request_id");
        List<String> deps = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM ehc_request_dependents WHERE request_id = ? ORDER BY dependent_id")) {
            ps.setLong(1, requestId);
            try (ResultSet drs = ps.executeQuery()) {
                while (drs.next()) {
                    deps.add("{\"name\":\"" + JsonUtil.escape(drs.getString("dependent_name")) + "\"," +
                            "\"relation\":\"" + JsonUtil.escape(drs.getString("relation")) + "\"," +
                            "\"dob\":\"" + JsonUtil.escape(drs.getDate("dob").toString()) + "\"," +
                            "\"gender\":\"" + JsonUtil.escape(Optional.ofNullable(drs.getString("gender")).orElse("")) + "\"}");
                }
            }
        }
        return "{" +
                "\"empNo\":\"" + JsonUtil.escape(rs.getString("emp_no")) + "\"," +
                "\"empName\":\"" + JsonUtil.escape(rs.getString("emp_name")) + "\"," +
                "\"designation\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("designation")).orElse("")) + "\"," +
                "\"division\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("division")).orElse("")) + "\"," +
                "\"mobile\":\"" + JsonUtil.escape(rs.getString("mobile")) + "\"," +
                "\"landline\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("landline")).orElse("")) + "\"," +
                "\"puHead\":\"" + JsonUtil.escape(rs.getString("pu_head")) + "\"," +
                "\"state\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("state_name")).orElse("")) + "\"," +
                "\"city\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("city_name")).orElse("")) + "\"," +
                "\"hospitalName\":\"" + JsonUtil.escape(rs.getString("hospital_name")) + "\"," +
                "\"ehcId\":\"" + JsonUtil.escape(rs.getString("ehc_id")) + "\"," +
                "\"status\":\"" + JsonUtil.escape(rs.getString("status")) + "\"," +
                "\"remarks\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("remarks")).orElse("")) + "\"," +
                "\"submissionDate\":\"" + JsonUtil.escape(rs.getDate("submission_date").toString()) + "\"," +
                "\"billDetails\":" + Optional.ofNullable(rs.getString("bill_details")).orElse("null") + "," +
                "\"financeRemarks\":\"" + JsonUtil.escape(Optional.ofNullable(rs.getString("finance_remarks")).orElse("")) + "\"," +
                "\"disbursementDetails\":" + Optional.ofNullable(rs.getString("disbursement_details")).orElse("null") + "," +
                "\"dependents\":[" + String.join(",", deps) + "]}";
    }

    private static String toJsonArray(List<String> values) {
        List<String> escaped = new ArrayList<>();
        for (String value : values) {
            escaped.add("\"" + JsonUtil.escape(value) + "\"");
        }
        return String.join(",", escaped);
    }

    private static java.util.Optional<String> jsonArrayValue(String json, String field) {
        int idx = json.indexOf("\"" + field + "\"");
        if (idx < 0) return Optional.empty();
        int start = json.indexOf('[', idx);
        if (start < 0) return Optional.empty();
        int depth = 0;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) {
                    return Optional.of(json.substring(start, i + 1));
                }
            }
        }
        return Optional.empty();
    }

    private static List<String> splitObjects(String jsonArray) {
        List<String> out = new ArrayList<>();
        int depth = 0;
        int start = -1;
        for (int i = 0; i < jsonArray.length(); i++) {
            char c = jsonArray.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    out.add(jsonArray.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return out;
    }
}
