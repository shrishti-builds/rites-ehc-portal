package com.rites.ehc;

import java.sql.Connection;
import java.sql.Statement;

public final class SqlServerBootstrap {
    private SqlServerBootstrap() {
    }

    public static void ensureSchema() {
        try (Connection conn = Db.getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate("IF OBJECT_ID('ehc_cities', 'U') IS NULL CREATE TABLE ehc_cities (" +
                    "city_id BIGINT IDENTITY(1,1) PRIMARY KEY," +
                    "state_name VARCHAR(100) NOT NULL," +
                    "city_name VARCHAR(100) NOT NULL UNIQUE," +
                    "active BIT NOT NULL DEFAULT 1" +
                    ")");
            st.executeUpdate("IF OBJECT_ID('ehc_employees', 'U') IS NULL CREATE TABLE ehc_employees (" +
                    "emp_no VARCHAR(50) PRIMARY KEY," +
                    "emp_name VARCHAR(150) NOT NULL," +
                    "designation VARCHAR(150) NULL," +
                    "division VARCHAR(150) NULL," +
                    "mobile VARCHAR(20) NULL," +
                    "landline VARCHAR(30) NULL," +
                    "dob DATE NULL," +
                    "gender VARCHAR(20) NULL," +
                    "active BIT NOT NULL DEFAULT 1" +
                    ")");
            st.executeUpdate("IF OBJECT_ID('ehc_employee_dependents', 'U') IS NULL CREATE TABLE ehc_employee_dependents (" +
                    "dependent_id BIGINT IDENTITY(1,1) PRIMARY KEY," +
                    "emp_no VARCHAR(50) NOT NULL FOREIGN KEY REFERENCES ehc_employees(emp_no)," +
                    "dependent_name VARCHAR(150) NOT NULL," +
                    "relation VARCHAR(50) NOT NULL," +
                    "dob DATE NOT NULL," +
                    "gender VARCHAR(20) NULL" +
                    ")");
            st.executeUpdate("IF OBJECT_ID('ehc_hospitals', 'U') IS NULL CREATE TABLE ehc_hospitals (" +
                    "hospital_id BIGINT IDENTITY(1,1) PRIMARY KEY," +
                    "vendor_code VARCHAR(50) NOT NULL UNIQUE," +
                    "hospital_name VARCHAR(200) NOT NULL," +
                    "address1 VARCHAR(300) NOT NULL," +
                    "address2 VARCHAR(300) NULL," +
                    "state_name VARCHAR(100) NOT NULL," +
                    "city_name VARCHAR(100) NOT NULL," +
                    "pincode VARCHAR(6) NOT NULL," +
                    "phone_l VARCHAR(30) NULL," +
                    "contact_person VARCHAR(150) NOT NULL," +
                    "contact_designation VARCHAR(150) NOT NULL," +
                    "contact_email VARCHAR(150) NOT NULL," +
                    "contact_m VARCHAR(10) NOT NULL," +
                    "alt_contact_person VARCHAR(150) NULL," +
                    "alt_contact_designation VARCHAR(150) NULL," +
                    "alt_contact_email VARCHAR(150) NULL," +
                    "alt_contact_m VARCHAR(10) NULL," +
                    "rate_male DECIMAL(12,2) NOT NULL," +
                    "rate_female DECIMAL(12,2) NOT NULL," +
                    "valid_upto DATE NOT NULL," +
                    "concession_info VARCHAR(500) NULL," +
                    "remarks VARCHAR(500) NULL," +
                    "active BIT NOT NULL DEFAULT 1," +
                    "created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()" +
                    ")");
            st.executeUpdate("IF OBJECT_ID('ehc_requests', 'U') IS NULL CREATE TABLE ehc_requests (" +
                    "request_id BIGINT IDENTITY(1,1) PRIMARY KEY," +
                    "ehc_id VARCHAR(30) NOT NULL UNIQUE," +
                    "emp_no VARCHAR(50) NOT NULL," +
                    "emp_name VARCHAR(150) NOT NULL," +
                    "designation VARCHAR(150) NULL," +
                    "division VARCHAR(150) NULL," +
                    "mobile VARCHAR(20) NOT NULL," +
                    "landline VARCHAR(30) NULL," +
                    "pu_head VARCHAR(50) NOT NULL," +
                    "state_name VARCHAR(100) NULL," +
                    "city_name VARCHAR(100) NULL," +
                    "hospital_name VARCHAR(200) NOT NULL," +
                    "status VARCHAR(50) NOT NULL," +
                    "remarks VARCHAR(1000) NULL," +
                    "bill_details NVARCHAR(MAX) NULL," +
                    "finance_remarks VARCHAR(1000) NULL," +
                    "disbursement_details NVARCHAR(MAX) NULL," +
                    "submission_date DATE NOT NULL," +
                    "created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()" +
                    ")");
            st.executeUpdate("IF COL_LENGTH('ehc_requests', 'bill_details') IS NULL ALTER TABLE ehc_requests ADD bill_details NVARCHAR(MAX) NULL");
            st.executeUpdate("IF COL_LENGTH('ehc_requests', 'finance_remarks') IS NULL ALTER TABLE ehc_requests ADD finance_remarks VARCHAR(1000) NULL");
            st.executeUpdate("IF COL_LENGTH('ehc_requests', 'disbursement_details') IS NULL ALTER TABLE ehc_requests ADD disbursement_details NVARCHAR(MAX) NULL");
            st.executeUpdate("IF OBJECT_ID('ehc_request_dependents', 'U') IS NULL CREATE TABLE ehc_request_dependents (" +
                    "dependent_id BIGINT IDENTITY(1,1) PRIMARY KEY," +
                    "request_id BIGINT NOT NULL FOREIGN KEY REFERENCES ehc_requests(request_id)," +
                    "dependent_name VARCHAR(150) NOT NULL," +
                    "relation VARCHAR(50) NOT NULL," +
                    "dob DATE NOT NULL," +
                    "gender VARCHAR(20) NULL" +
                    ")");
            st.executeUpdate("IF OBJECT_ID('ehc_status_history', 'U') IS NULL CREATE TABLE ehc_status_history (" +
                    "history_id BIGINT IDENTITY(1,1) PRIMARY KEY," +
                    "request_id BIGINT NOT NULL FOREIGN KEY REFERENCES ehc_requests(request_id)," +
                    "old_status VARCHAR(50) NULL," +
                    "new_status VARCHAR(50) NOT NULL," +
                    "remarks VARCHAR(1000) NULL," +
                    "changed_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()" +
                    ")");
            st.executeUpdate("IF OBJECT_ID('ehc_documents', 'U') IS NULL CREATE TABLE ehc_documents (" +
                    "document_id BIGINT IDENTITY(1,1) PRIMARY KEY," +
                    "request_id BIGINT NOT NULL FOREIGN KEY REFERENCES ehc_requests(request_id)," +
                    "request_member_id BIGINT NULL," +
                    "document_type VARCHAR(50) NOT NULL," +
                    "file_name VARCHAR(255) NOT NULL," +
                    "file_path VARCHAR(500) NOT NULL," +
                    "content_type VARCHAR(100) NULL," +
                    "uploaded_by VARCHAR(50) NOT NULL," +
                    "uploaded_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()," +
                    "is_verified BIT NULL," +
                    "verified_by VARCHAR(50) NULL," +
                    "verified_at DATETIME2 NULL," +
                    "verification_comments VARCHAR(1000) NULL" +
                    ")");
            st.executeUpdate("IF OBJECT_ID('ehc_payment_recommendations', 'U') IS NULL CREATE TABLE ehc_payment_recommendations (" +
                    "recommendation_id BIGINT IDENTITY(1,1) PRIMARY KEY," +
                    "request_id BIGINT NOT NULL FOREIGN KEY REFERENCES ehc_requests(request_id)," +
                    "recommended_by VARCHAR(50) NOT NULL," +
                    "recommended_date DATETIME2 NOT NULL DEFAULT SYSDATETIME()," +
                    "total_bill_amount DECIMAL(12,2) NOT NULL," +
                    "company_payable_amount DECIMAL(12,2) NOT NULL," +
                    "employee_payable_amount DECIMAL(12,2) NOT NULL," +
                    "payment_mode VARCHAR(30) NOT NULL," +
                    "comments VARCHAR(1000) NULL" +
                    ")");
            st.executeUpdate("IF OBJECT_ID('ehc_payments', 'U') IS NULL CREATE TABLE ehc_payments (" +
                    "payment_id BIGINT IDENTITY(1,1) PRIMARY KEY," +
                    "request_id BIGINT NOT NULL FOREIGN KEY REFERENCES ehc_requests(request_id)," +
                    "recommendation_id BIGINT NOT NULL FOREIGN KEY REFERENCES ehc_payment_recommendations(recommendation_id)," +
                    "processed_by VARCHAR(50) NOT NULL," +
                    "processed_date DATETIME2 NOT NULL DEFAULT SYSDATETIME()," +
                    "payment_status VARCHAR(30) NOT NULL," +
                    "paid_amount DECIMAL(12,2) NOT NULL," +
                    "payment_reference_no VARCHAR(100) NULL," +
                    "payment_date DATE NULL," +
                    "finance_comments VARCHAR(1000) NULL" +
                    ")");
            st.executeUpdate("IF NOT EXISTS (SELECT 1 FROM ehc_employee_dependents) " +
                    "INSERT INTO ehc_employee_dependents(emp_no, dependent_name, relation, dob, gender) VALUES " +
                    "('10124','ANJANI UPADHYAY','Self','1975-01-26','Male')," +
                    "('10124','SWETA SHARMA','Spouse','1976-08-28','Female')," +
                    "('10245','RAHUL KUMAR','Self','1982-05-15','Male')," +
                    "('10245','PRIYA KUMARI','Spouse','1985-09-12','Female')," +
                    "('10245','RAMESH KUMAR','Father','1953-04-10','Male')");
        } catch (Exception e) {
            throw new RuntimeException("Failed to bootstrap schema", e);
        }
    }
}
