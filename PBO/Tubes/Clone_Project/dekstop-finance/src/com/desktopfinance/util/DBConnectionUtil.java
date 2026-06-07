package com.desktopfinance.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionUtil {

    // --- Informasi Koneksi Database ---
    // Ganti dengan detail koneksi database MySQL Anda
    private static final String DB_URL = "jdbc:mysql://localhost:3306/finance_dekstop"; // Sesuaikan nama database
    private static final String DB_USER = "root"; // Ganti dengan username database Anda
    private static final String DB_PASSWORD = ""; // Ganti dengan password database Anda

    private static Connection connection = null;

    // Private constructor untuk mencegah instansiasi langsung (Singleton Pattern)
    private DBConnectionUtil() {
        // Tidak perlu melakukan apa-apa di sini
    }

    /**
     * Mendapatkan instance koneksi database.
     * Jika koneksi belum ada atau sudah ditutup, maka akan membuat koneksi baru.
     * * @return Objek Connection ke database
     * @throws SQLException Jika terjadi error saat koneksi ke database
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Cek apakah driver JDBC sudah ter-load (opsional untuk JDBC 4.0+)
            // Class.forName("com.mysql.cj.jdbc.Driver"); // Untuk MySQL Connector/J 8.0+

            if (connection == null || connection.isClosed()) {
                // Membuat koneksi baru
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Koneksi ke database berhasil dibuat!"); // Pesan untuk debugging
            }
        } catch (SQLException e) {
            System.err.println("Koneksi ke database gagal! Error: " + e.getMessage());
            // e.printStackTrace(); // Tampilkan stack trace untuk debugging lebih detail
            throw e; // Lemparkan kembali exception agar bisa ditangani oleh pemanggil
        }
        // Jika menggunakan Class.forName dan ingin menanganinya secara spesifik:
        // catch (ClassNotFoundException e) {
        //     System.err.println("Driver JDBC MySQL tidak ditemukan! Error: " + e.getMessage());
        //     throw new SQLException("Driver JDBC tidak ditemukan", e);
        // }
        return connection;
    }

    /**
     * Menutup koneksi database jika sedang terbuka.
     * Sebaiknya dipanggil saat aplikasi ditutup.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Koneksi ke database berhasil ditutup."); // Pesan untuk debugging
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi database! Error: " + e.getMessage());
                // e.printStackTrace();
            } finally {
                connection = null; // Set ke null setelah ditutup
            }
        }
    }

    // --- Main method untuk testing koneksi (opsional) ---
    public static void main(String[] args) {
        try {
            Connection conn = DBConnectionUtil.getConnection();
            if (conn != null) {
                System.out.println("Tes koneksi berhasil. Koneksi aktif.");
                DBConnectionUtil.closeConnection();
            } else {
                System.out.println("Tes koneksi gagal.");
            }
        } catch (SQLException e) {
            System.err.println("Error saat testing koneksi: " + e.getMessage());
        }
    }
}