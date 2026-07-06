package com.placement.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("db.properties not found! Copy db.properties.example to db.properties and fill in your credentials.");
            } else {
                props.load(input);
            }
        } catch (IOException e) {
            System.out.println("Error loading db.properties: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }
}