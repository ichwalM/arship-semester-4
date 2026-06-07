package com.desktopfinance.dao;

import com.desktopfinance.model.User;
import com.desktopfinance.util.DBConnectionUtil;
import com.desktopfinance.util.PasswordUtil; // Anggap PasswordUtil sudah ada dan bisa di-hash

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class UserDAO {

    /**
     * Menambahkan pengguna baru ke database.
     * Password yang disimpan adalah password yang sudah di-hash.
     *
     * @param user Objek User yang akan ditambahkan.
     * @return true jika user berhasil ditambahkan, false jika gagal.
     * @throws SQLException jika terjadi error database.
     */
    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, created_at, updated_at) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            // Meng-hash password sebelum disimpan ke database
            // Kita akan membuat PasswordUtil nanti. Untuk sekarang, kita asumsikan sudah ada.
            // Jika belum ada PasswordUtil, baris ini bisa menyebabkan error atau perlu di-komen dulu.
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword()); 

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword); // Simpan password yang sudah di-hash
            
            // createdAt dan updatedAt bisa di-set otomatis oleh database jika ada DEFAULT CURRENT_TIMESTAMP
            // Namun, jika ingin di-set dari Java:
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            pstmt.setTimestamp(3, currentTime); // createdAt
            pstmt.setTimestamp(4, currentTime); // updatedAt (saat pembuatan, updatedAt = createdAt)


            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } finally {
            // Selalu tutup PreparedStatement (ResultSet tidak ada di sini)
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace(); // Log error
                }
            }
            // Koneksi akan ditutup oleh pemanggil atau saat aplikasi berhenti,
            // atau bisa juga ditutup di sini jika setiap metode DAO mengelola koneksinya sendiri
            // Untuk saat ini, kita biarkan koneksi dikelola oleh DBConnectionUtil atau pemanggil.
        }
    }

    // Metode berikutnya akan kita buat di sini (getUserByUsername)
     public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password, created_at, updated_at FROM users WHERE username = ?";
        User user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) { // Jika user ditemukan
                user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password")); // Ini adalah password yang sudah di-hash dari DB
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setUpdatedAt(rs.getTimestamp("updated_at"));
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // Sama seperti sebelumnya, penanganan koneksi bisa bervariasi.
        }
        return user;
    }
}