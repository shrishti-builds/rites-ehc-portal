package com.rites.ehc;

public final class SqlServerConfig {
    private SqlServerConfig() {
    }

    public static String url() {
        String value = System.getenv("EHC_DB_URL");
        if (value != null && !value.isBlank()) {
            return value;
        }
        return "jdbc:sqlserver://localhost:1433;databaseName=ehc_rites;encrypt=true;trustServerCertificate=true";
    }

    public static String username() {
        String value = System.getenv("EHC_DB_USER");
        return value != null ? value : "sa";
    }

    public static String password() {
        String value = System.getenv("EHC_DB_PASSWORD");
        return value != null ? value : "";
    }
}
