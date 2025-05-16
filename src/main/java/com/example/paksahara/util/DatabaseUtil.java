package com.example.paksahara.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/paksahara_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Azaan2004.";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
