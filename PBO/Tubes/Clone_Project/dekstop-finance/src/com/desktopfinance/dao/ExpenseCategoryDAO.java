package com.desktopfinance.dao;

import com.desktopfinance.model.ExpenseCategory;
import com.desktopfinance.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExpenseCategoryDAO {

    /**
     * Mengambil semua kategori pengeluaran yang aktif (global dan milik pengguna tertentu).
     * Kategori global memiliki user_id IS NULL.
     *
     * @param userId ID pengguna untuk mengambil kategori spesifik miliknya.
     * @return List dari objek ExpenseCategory.
     * @throws SQLException jika terjadi error database.
     */
    public List<ExpenseCategory> getAllActiveCategoriesForUser(int userId) throws SQLException {
        List<ExpenseCategory> categories = new ArrayList<>();
        String sql = "SELECT category_id, category_name, user_id, created_at FROM expense_categories " +
                     "WHERE user_id IS NULL OR user_id = ? ORDER BY category_name ASC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ExpenseCategory category = new ExpenseCategory();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCategoryName(rs.getString("category_name"));
                Integer catUserId = rs.getInt("user_id");
                category.setUserId(rs.wasNull() ? null : catUserId);
                category.setCreatedAt(rs.getTimestamp("created_at"));
                categories.add(category);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return categories;
    }

    /**
     * Menambahkan kategori pengeluaran baru yang spesifik untuk pengguna.
     *
     * @param category Objek ExpenseCategory yang akan ditambahkan (harus memiliki user_id).
     * @return ID dari kategori yang baru ditambahkan, atau -1 jika gagal.
     * @throws SQLException jika terjadi error database.
     */
    public int addCategory(ExpenseCategory category) throws SQLException {
        // Pastikan category memiliki user_id (tidak boleh menambah kategori global dari sini)
        if (category.getUserId() == null) {
            throw new SQLException("User ID tidak boleh null untuk menambah kategori baru oleh pengguna.");
        }
        String sql = "INSERT INTO expense_categories (category_name, user_id, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int categoryId = -1;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, category.getCategoryName());
            pstmt.setInt(2, category.getUserId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    categoryId = generatedKeys.getInt(1);
                    category.setCategoryId(categoryId); // Update objek dengan ID yang digenerate
                }
            }
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (pstmt != null) pstmt.close();
        }
        return categoryId;
    }

    /**
     * Mengupdate nama kategori pengeluaran yang spesifik milik pengguna.
     * Hanya kategori milik pengguna (dengan user_id yang cocok) yang bisa diupdate.
     *
     * @param category Objek ExpenseCategory dengan category_id dan category_name yang baru.
     * @param userId ID pengguna yang melakukan update (untuk verifikasi kepemilikan).
     * @return true jika update berhasil, false jika tidak (misalnya kategori tidak ditemukan atau bukan milik user).
     * @throws SQLException jika terjadi error database.
     */
    public boolean updateCategory(ExpenseCategory category, int userId) throws SQLException {
        // Kategori global (user_id IS NULL) tidak boleh diupdate oleh pengguna.
        String sql = "UPDATE expense_categories SET category_name = ? " +
                     "WHERE category_id = ? AND user_id = ?"; // Pastikan user_id cocok
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category.getCategoryName());
            pstmt.setInt(2, category.getCategoryId());
            pstmt.setInt(3, userId); // Verifikasi kepemilikan

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    /**
     * Menghapus kategori pengeluaran yang spesifik milik pengguna.
     * Hanya kategori milik pengguna (dengan user_id yang cocok) yang bisa dihapus.
     * Kategori global tidak bisa dihapus oleh pengguna.
     * Transaksi yang menggunakan kategori ini akan memiliki category_id menjadi NULL (karena ON DELETE SET NULL).
     *
     * @param categoryId ID kategori yang akan dihapus.
     * @param userId ID pengguna yang melakukan penghapusan (untuk verifikasi kepemilikan).
     * @return true jika delete berhasil, false jika tidak.
     * @throws SQLException jika terjadi error database.
     */
    public boolean deleteCategory(int categoryId, int userId) throws SQLException {
        // Kategori global (user_id IS NULL) tidak boleh dihapus oleh pengguna.
        String sql = "DELETE FROM expense_categories WHERE category_id = ? AND user_id = ?"; // Pastikan user_id cocok
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, categoryId);
            pstmt.setInt(2, userId); // Verifikasi kepemilikan

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * Mengambil satu kategori berdasarkan ID-nya.
     * Berguna untuk memverifikasi kepemilikan atau mendapatkan detail.
     *
     * @param categoryId ID kategori.
     * @return Objek ExpenseCategory jika ditemukan, null jika tidak.
     * @throws SQLException jika terjadi error database.
     */
    public ExpenseCategory getCategoryById(int categoryId) throws SQLException {
        String sql = "SELECT category_id, category_name, user_id, created_at FROM expense_categories WHERE category_id = ?";
        ExpenseCategory category = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, categoryId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                category = new ExpenseCategory();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCategoryName(rs.getString("category_name"));
                Integer catUserId = rs.getInt("user_id");
                category.setUserId(rs.wasNull() ? null : catUserId);
                category.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return category;
    }
}