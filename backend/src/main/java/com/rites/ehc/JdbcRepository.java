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
            String ehcId = "EHC-" + String.valueOf(System.currentTimeMillis()).substring(7);
            String insert = "INSERT INTO ehc_requests(ehc_id, emp_no, emp_name, designation, division, mobile, landline, pu_head, state_name, city_name, hospital_name, status, remarks, submission_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, ehcId);
                ps.setString(2, JsonUtil.jsonValue(jsonBody, "empNo").orElse(""));
                ps.setString(3, JsonUtil.jsonValue(jsonBody, "empName").orElse(""));
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
            String selectIdSql = "SELECT request_id FROM ehc_requests WHERE ehc_id = ?";
            long requestId;
            try (PreparedStatement ps = conn.prepareStatement(selectIdSql)) {
                ps.setString(1, ehcId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    requestId = rs.getLong(1);
                }
            }
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
            conn.commit();
            return findRequest(ehcId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateRequestStatus(String ehcId, String status, String remarks) {
        String sql = "UPDATE ehc_requests SET status = ?, remarks = ? WHERE ehc_id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, remarks);
            ps.setString(3, ehcId);
            return ps.executeUpdate() > 0;
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
        String sql = "UPDATE ehc_requests SET status = 'Bill Uploaded', bill_details = ? WHERE ehc_id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, billDetails);
            ps.setString(2, ehcId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateRequestFinanceAction(String ehcId, String status, String financeRemarks) {
        String sql = "UPDATE ehc_requests SET status = ?, finance_remarks = ? WHERE ehc_id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, financeRemarks);
            ps.setString(3, ehcId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateRequestDisbursement(String ehcId, String disbursementDetails) {
        String sql = "UPDATE ehc_requests SET status = 'Disbursed', disbursement_details = ? WHERE ehc_id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, disbursementDetails);
            ps.setString(2, ehcId);
            return ps.executeUpdate() > 0;
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
