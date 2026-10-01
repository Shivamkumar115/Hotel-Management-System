import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection conn;

    public static Connection getConnection() {
        try {
            // MySQL JDBC Driver load karna
            Class.forName("com.mysql.cj.jdbc.Driver");

            
            String url = "jdbc:mysql://localhost:3306/hoteldb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            String username = "admin"; 
            String password = "Shivam@2003ps";

    
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, username, password);
                System.out.println("Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found! Add mysql-connector-j.jar to classpath.");
        } catch (SQLException e) {
            System.out.println("Database connection failed! Check username/password and DB name.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
  
    }
    
}
