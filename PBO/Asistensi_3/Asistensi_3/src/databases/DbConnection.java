package databases;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/product_asistensi3";
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = ""; 

    private static Connection connection = null;
    private DbConnection() {
        
    }
    public static Connection getConnection() throws SQLException {
        try {
           

            if (connection == null || connection.isClosed()) {
                
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Koneksi ke database berhasil dibuat!"); 
            }
        } catch (SQLException e) {
            System.err.println("Koneksi ke database gagal! Error: " + e.getMessage());
           
            throw e; 
        }
        
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Koneksi ditutup."); 
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi " + e.getMessage());
            
            } finally {
                connection = null; 
            }
        }
    }

    
    public static void main(String[] args) {
        try {
            Connection conn = DbConnection.getConnection();
            if (conn != null) {
                System.out.println("Tes koneksi berhasil");
                DbConnection.closeConnection();
            } else {
                System.out.println("Tes koneksi gagal.");
            }
        } catch (SQLException e) {
            System.err.println("Error saat testing koneksi: " + e.getMessage());
        }
    }
}
