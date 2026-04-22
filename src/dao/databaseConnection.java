package dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseConnection {
    private static databaseConnection instance;
    private Connection connection;

    private static final String URL =
    	    "jdbc:sqlserver://127.0.0.1:1433;" +
    	        "instanceName=SQLEXPRESS;" +
    	        "databaseName=medicinedb;" +
    	        "user=medicine_user;" +
    	        "password=Pass1234;" +
    	        "encrypt=false;" +
    	        "trustServerCertificate=true;";

    private databaseConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(URL);
            System.out.println("✅ Connected to medicinedb!");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
            connection = null;
        }
    }

    public static databaseConnection getInstance() {
        if (instance == null) {
            instance = new databaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
