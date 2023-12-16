package com.javafx.application;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.DriverManager;


public class SQLConnector {

    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/School";
    private static final String USERNAME = "thuanc177";
    private static final String PASSWORD = "14032005";

    // JDBC variables for opening, closing and managing connection
    private static Connection connection;

    // Static block to load the JDBC driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load the JDBC driver");
        }
    }
    // Execute a SELECT query and return the ResultSet
    public static ResultSet executeSelectQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute SELECT query");
        }
    }

    // Method to establish a connection to the database
    public static Connection connect() {
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database");
        }
    }

    // Method to close the connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to close the database connection");
        }
    }

    // You can add more methods for executing SQL queries, updates, etc.

    public static void main(String[] args) {
        // Example usage:
        Connection dbConnection = SQLConnector.connect();
        System.out.println("Connected to the database!");

        // Example SELECT query
        String selectQuery = "SELECT * FROM sinhvien";
        ResultSet resultSet = SQLConnector.executeSelectQuery(selectQuery);

        try {
            // Process the ResultSet (this is just an example, modify as needed)
            while (resultSet.next()) {
                String MASV = resultSet.getString("MASV");
                String HOTEN = resultSet.getString("HOTEN");
                Date NGAYSINH = resultSet.getDate("NGAYSINH");
                // Process other columns as needed
                System.out.println("MASV: " + MASV + ", HOTEN: " + HOTEN + ", NGAYSINH: " + NGAYSINH);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the ResultSet (and optionally the Statement) when done
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        SQLConnector.closeConnection();
        System.out.println("Connection closed.");
    }
}

