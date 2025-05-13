// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;

// public class TestDBConnection {
//     public static void main(String[] args) {
//         String url = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

//         try {
//             // Load the JDBC driver
//             Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

//             // Attempt connection using Windows Authentication
//             Connection conn = DriverManager.getConnection(url);
//             System.out.println("Connected to SQL Server successfully using Windows Authentication!");
//             conn.close();
//         } catch (ClassNotFoundException e) {
//             System.out.println("SQL Server JDBC Driver not found.");
//             e.printStackTrace();
//         } catch (SQLException e) {
//             System.out.println("Connection failed!");
//             e.printStackTrace();
//         }
//     }
// }


public class ReadAdminsWindowsAuth {
     public static void main(String[] args) {
        new SignInPage();
    }
}





