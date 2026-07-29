import java.sql.*;

public class TestInsert {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=ehc_rites;encrypt=true;trustServerCertificate=true";
        try (Connection conn = DriverManager.getConnection(url, "ehc_rites_user", "Ehc@12345")) {
            System.out.println("Connected.");
            String ehcId = "EHC-999999";
            String insert = "INSERT INTO ehc_requests(ehc_id, emp_no, emp_name, designation, division, mobile, landline, pu_head, state_name, city_name, hospital_name, status, remarks, submission_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, ehcId);
                ps.setString(2, "10124");
                ps.setString(3, "TEST USER");
                ps.setString(4, "TEST DESIG");
                ps.setString(5, "TEST DIV");
                ps.setString(6, "1234567890");
                ps.setString(7, null);
                ps.setString(8, "PU HEAD");
                ps.setString(9, "Haryana");
                ps.setString(10, "Gurugram");
                ps.setString(11, "Medanta");
                ps.setString(12, "Pending SBU");
                ps.setString(13, "");
                ps.setDate(14, Date.valueOf(java.time.LocalDate.now()));
                int rows = ps.executeUpdate();
                System.out.println("Inserted request: " + rows);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
