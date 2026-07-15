package com.rites.ehc;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public final class Db {
    private Db() {
    }

    public static Connection getConnection() throws SQLException {
        DataSource dataSource = SpringContext.getBean(DataSource.class);
        return dataSource.getConnection();
    }
}
