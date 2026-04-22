package launch;

import dao.databaseConnection; 
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class launcher {
    public static void main(String[] args) {
        System.out.println("--- Starting Connection Test ---");
        
        try {
            Connection conn = databaseConnection.getInstance().getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ SQL Server is connected!");
                
                // Test query - change 'medicines' to your actual table name
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM medicines");
                
                while (rs.next()) {
                    System.out.println("Medicine: " + rs.getString("name"));
                    // change "name" to your actual column name
                }
                
                rs.close();
                stmt.close();
                
            } else {
                System.out.println("❌ ERROR: Connection is null or closed.");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("--- Done ---");
    }
}
