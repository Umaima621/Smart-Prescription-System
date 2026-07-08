package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
        "jdbc:sqlserver://localhost:1433;" +
        "databaseName=smart_prescription_db;" +
        "user=medicine_user;" +
        "password=Pass1234;" +
        "trustServerCertificate=true;" +
        "encrypt=false;";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(URL);
            System.out.println("[DB] Connected to smart_prescription_db successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] Driver not found: " + e.getMessage());
            throw new RuntimeException("JDBC Driver missing.", e);
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection failed: " + e.getMessage(), e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Re-connection failed: " + e.getMessage(), e);
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing: " + e.getMessage());
        }
    }
}