package com.desktopfinance.dao;

import com.desktopfinance.model.IncomeSource;
import com.desktopfinance.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp; // Pastikan Timestamp diimport
import java.util.ArrayList;
import java.util.List;

public class IncomeSourceDAO {

    /**
     * Mengambil semua sumber pemasukan yang aktif (global dan milik pengguna tertentu).
     * Sumber global memiliki user_id IS NULL.
     *
     * @param userId ID pengguna untuk mengambil sumber spesifik miliknya.
     * @return List dari objek IncomeSource.
     * @throws SQLException jika terjadi error database.
     */
    public List<IncomeSource> getAllActiveSourcesForUser(int userId) throws SQLException {
        List<IncomeSource> sources = new ArrayList<>();
        String sql = "SELECT source_id, source_name, user_id, created_at FROM income_sources " +
                     "WHERE user_id IS NULL OR user_id = ? ORDER BY source_name ASC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                IncomeSource source = new IncomeSource();
                source.setSourceId(rs.getInt("source_id"));
                source.setSourceName(rs.getString("source_name"));
                Integer srcUserId = rs.getInt("user_id"); // Ambil sebagai Integer dulu
                if (rs.wasNull()) { // Cek apakah nilai SQL terakhir yang dibaca adalah NULL
                    source.setUserId(null);
                } else {
                    source.setUserId(srcUserId);
                }
                source.setCreatedAt(rs.getTimestamp("created_at"));
                sources.add(source);
            }
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            // Koneksi tidak ditutup di sini, biarkan dikelola oleh pemanggil atau DBConnectionUtil
        }
        return sources;
    }

    /**
     * Menambahkan sumber pemasukan baru yang spesifik untuk pengguna.
     *
     * @param source Objek IncomeSource yang akan ditambahkan (harus memiliki user_id).
     * @return ID dari sumber yang baru ditambahkan, atau -1 jika gagal.
     * @throws SQLException jika terjadi error database atau jika user_id null.
     */
    public int addSource(IncomeSource source) throws SQLException {
        if (source.getUserId() == null) {
            throw new SQLException("User ID tidak boleh null untuk menambah sumber pemasukan baru oleh pengguna.");
        }
        String sql = "INSERT INTO income_sources (source_name, user_id, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int sourceId = -1;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, source.getSourceName());
            pstmt.setInt(2, source.getUserId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    sourceId = generatedKeys.getInt(1);
                    source.setSourceId(sourceId); // Update objek dengan ID yang digenerate
                }
            }
        } finally {
            if (generatedKeys != null) {
                try { generatedKeys.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return sourceId;
    }

    /**
     * Mengupdate nama sumber pemasukan yang spesifik milik pengguna.
     * Hanya sumber milik pengguna (dengan user_id yang cocok) yang bisa diupdate.
     *
     * @param source Objek IncomeSource dengan source_id dan source_name yang baru.
     * @param userId ID pengguna yang melakukan update (untuk verifikasi kepemilikan).
     * @return true jika update berhasil, false jika tidak.
     * @throws SQLException jika terjadi error database.
     */
    public boolean updateSource(IncomeSource source, int userId) throws SQLException {
        // Kategori global (user_id IS NULL) tidak boleh diupdate oleh pengguna.
        String sql = "UPDATE income_sources SET source_name = ? " +
                     "WHERE source_id = ? AND user_id = ?"; // Pastikan user_id cocok
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, source.getSourceName());
            pstmt.setInt(2, source.getSourceId());
            pstmt.setInt(3, userId); // Verifikasi kepemilikan

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Menghapus sumber pemasukan yang spesifik milik pengguna.
     * Hanya sumber milik pengguna (dengan user_id yang cocok) yang bisa dihapus.
     * Transaksi yang menggunakan sumber ini akan memiliki source_id menjadi NULL (karena ON DELETE SET NULL).
     *
     * @param sourceId ID sumber yang akan dihapus.
     * @param userId ID pengguna yang melakukan penghapusan (untuk verifikasi kepemilikan).
     * @return true jika delete berhasil, false jika tidak.
     * @throws SQLException jika terjadi error database.
     */
    public boolean deleteSource(int sourceId, int userId) throws SQLException {
        // Kategori global (user_id IS NULL) tidak boleh dihapus oleh pengguna.
        String sql = "DELETE FROM income_sources WHERE source_id = ? AND user_id = ?"; // Pastikan user_id cocok
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sourceId);
            pstmt.setInt(2, userId); // Verifikasi kepemilikan

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    /**
     * Mengambil satu sumber pemasukan berdasarkan ID-nya.
     * Berguna untuk memverifikasi kepemilikan atau mendapatkan detail.
     *
     * @param sourceId ID sumber.
     * @return Objek IncomeSource jika ditemukan, null jika tidak.
     * @throws SQLException jika terjadi error database.
     */
    public IncomeSource getSourceById(int sourceId) throws SQLException {
        String sql = "SELECT source_id, source_name, user_id, created_at FROM income_sources WHERE source_id = ?";
        IncomeSource source = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sourceId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                source = new IncomeSource();
                source.setSourceId(rs.getInt("source_id"));
                source.setSourceName(rs.getString("source_name"));
                Integer srcUserId = rs.getInt("user_id");
                if (rs.wasNull()) {
                    source.setUserId(null);
                } else {
                    source.setUserId(srcUserId);
                }
                source.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (pstmt != null) {
                try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return source;
    }
}