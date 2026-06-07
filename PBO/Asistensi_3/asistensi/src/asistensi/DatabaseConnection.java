package asistensi;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/nama_database";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

// Contoh method untuk mengambil data produk
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class ProductDAO { // Data Access Object
    public static List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id, nama, harga FROM produk");

            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama");
                double harga = rs.getDouble("harga");
                productList.add(new Product(id, nama, harga));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            DatabaseConnection.closeConnection(conn);
        }

        return productList;
    }

    public static void addProduct(String nama, double harga) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO produk (nama, harga) VALUES (?, ?)");
            pstmt.setString(1, nama);
            pstmt.setDouble(2, harga);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            DatabaseConnection.closeConnection(conn);
        }
    }

    // Implement method updateProduct dan deleteProduct serupa
}

// Contoh kelas Product
public class Product {
    private int id;
    private String nama;
    private double harga;

    public Product(int id, String nama, double harga) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
    }

    // Getters dan Setters
}

// Contoh mengisi JTable
private void populateTable() {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    model.setRowCount(0); // Clear existing data
    List<Product> products = ProductDAO.getAllProducts(); // Ambil data dari database
    for (Product product : products) {
        model.addRow(new Object[]{product.getId(), product.getNama(), product.getHarga()});
    }
}