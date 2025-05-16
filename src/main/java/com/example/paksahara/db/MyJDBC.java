package com.example.paksahara.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class MyJDBC{
    public static void main(String[] args) {
        try {
            Connection c = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/paksahara_db",
                    "root",
                    "Azaan2004."
            );

            Statement s = c.createStatement();
            ResultSet resultSet = s.executeQuery("SELECT * FROM temp");

            while (resultSet.next()) {
                System.out.println(resultSet.getString("userid"));
                System.out.println(resultSet.getString("username"));
                System.out.println(resultSet.getString("age"));
            }

            // Close resources
            resultSet.close();
            s.close();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
