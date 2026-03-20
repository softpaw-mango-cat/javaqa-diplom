package ru.netology.config;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    @Getter
    private static Connection connection;

    public static void connectToMySQL() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/app";
        String user = "app";
        String password = "pass";
        connection = DriverManager.getConnection(url, user, password);
    }

    public static void connectToPostgreSQL() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/app";
        String user = "app";
        String password = "pass";
        connection = DriverManager.getConnection(url, user, password);
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
