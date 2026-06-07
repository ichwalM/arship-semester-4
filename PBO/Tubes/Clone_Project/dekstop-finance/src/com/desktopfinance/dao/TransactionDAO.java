package com.desktopfinance.dao;

import com.desktopfinance.model.Transaction;
import com.desktopfinance.model.TransactionType;
import com.desktopfinance.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types; // Untuk setNull jika categoryId atau sourceId null
import java.util.ArrayList;
import java.util.List;
import java.sql.Date; // Untuk transaction_date
import java.math.BigDecimal; // Untuk amount
import com.desktopfinance.model.TransactionType; // Pastikan sudah diimport
import java.sql.Date; // Untuk parameter tanggal

public class TransactionDAO {

    /**
     * Menambahkan transaksi baru ke database.
     *
     * @param transaction Objek Transaction yang akan ditambahkan.
     * @return true jika transaksi berhasil ditambahkan, false jika gagal.
     * @throws SQLException jika terjadi error database.
     */
    public boolean addTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, type, amount, transaction_date, description, category_id, source_id, is_deleted, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, transaction.getUserId());
            pstmt.setString(2, transaction.getType().getDatabaseValue()); // "income" atau "expense"
            pstmt.setBigDecimal(3, transaction.getAmount());
            pstmt.setDate(4, transaction.getTransactionDate());
            pstmt.setString(5, transaction.getDescription());

            if (transaction.getCategoryId() != null) {
                pstmt.setInt(6, transaction.getCategoryId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }

            if (transaction.getSourceId() != null) {
                pstmt.setInt(7, transaction.getSourceId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            
            pstmt.setBoolean(8, transaction.isDeleted()); // is_deleted

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Ambil ID yang digenerate (opsional, jika dibutuhkan)
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setTransactionId(generatedKeys.getInt(1));
                    }
                }
                return true;
            } else {
                return false;
            }

        } finally {
            if (pstmt != null) pstmt.close();
            // Koneksi tidak ditutup di sini
        }
    }

    /**
     * Mengupdate data transaksi yang sudah ada.
     *
     * @param transaction Objek Transaction dengan data yang sudah diupdate.
     * @return true jika update berhasil, false jika gagal.
     * @throws SQLException jika terjadi error database.
     */
    public boolean updateTransaction(Transaction transaction) throws SQLException {
        String sql = "UPDATE transactions SET type = ?, amount = ?, transaction_date = ?, description = ?, " +
                     "category_id = ?, source_id = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE transaction_id = ? AND user_id = ? AND is_deleted = FALSE";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, transaction.getType().getDatabaseValue());
            pstmt.setBigDecimal(2, transaction.getAmount());
            pstmt.setDate(3, transaction.getTransactionDate());
            pstmt.setString(4, transaction.getDescription());

            if (transaction.getCategoryId() != null) {
                pstmt.setInt(5, transaction.getCategoryId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            if (transaction.getSourceId() != null) {
                pstmt.setInt(6, transaction.getSourceId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            
            pstmt.setInt(7, transaction.getTransactionId());
            pstmt.setInt(8, transaction.getUserId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    /**
     * Melakukan soft delete pada transaksi.
     *
     * @param transactionId ID transaksi yang akan di-soft delete.
     * @param userId ID pengguna pemilik transaksi.
     * @return true jika soft delete berhasil, false jika gagal.
     * @throws SQLException jika terjadi error database.
     */
    public boolean softDeleteTransaction(int transactionId, int userId) throws SQLException {
        String sql = "UPDATE transactions SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE transaction_id = ? AND user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, transactionId);
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * Merestore transaksi yang sudah di-soft delete.
     *
     * @param transactionId ID transaksi yang akan direstore.
     * @param userId ID pengguna pemilik transaksi.
     * @return true jika restore berhasil, false jika gagal.
     * @throws SQLException jika terjadi error database.
     */
    public boolean restoreTransaction(int transactionId, int userId) throws SQLException {
        String sql = "UPDATE transactions SET is_deleted = FALSE, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE transaction_id = ? AND user_id = ? AND is_deleted = TRUE"; // Pastikan hanya merestore yang terhapus
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, transactionId);
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }


    /**
     * Mengambil semua transaksi (yang tidak di-soft delete) untuk pengguna tertentu.
     *
     * @param userId ID pengguna.
     * @return List dari objek Transaction.
     * @throws SQLException jika terjadi error database.
     */
    // Di dalam kelas TransactionDAO.java
// ... (import yang sudah ada) ...

    public List<Transaction> getAllTransactionsByUserId(int userId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.transaction_id, t.user_id, t.type, t.amount, t.transaction_date, " +
                     "t.description, t.category_id, ec.category_name, t.source_id, isrc.source_name, " +
                     "t.is_deleted, t.created_at, t.updated_at " +
                     "FROM transactions t " +
                     "LEFT JOIN expense_categories ec ON t.category_id = ec.category_id " +
                     "LEFT JOIN income_sources isrc ON t.source_id = isrc.source_id " +
                     "WHERE t.user_id = ? AND t.is_deleted = FALSE " +
                     "ORDER BY t.transaction_date DESC, t.created_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction trx = new Transaction();
                trx.setTransactionId(rs.getInt("transaction_id"));
                trx.setUserId(rs.getInt("user_id"));

                String typeStr = rs.getString("type");
                if (typeStr != null) {
                    trx.setType(TransactionType.fromDatabaseValue(typeStr));
                }

                trx.setAmount(rs.getBigDecimal("amount"));
                trx.setTransactionDate(rs.getDate("transaction_date"));
                trx.setDescription(rs.getString("description"));

                // Category ID dan Name
                Integer categoryId = rs.getInt("category_id");
                trx.setCategoryId(rs.wasNull() ? null : categoryId);
                trx.setCategoryName(rs.getString("category_name")); // Akan null jika tidak ada join match

                // Source ID dan Name
                Integer sourceId = rs.getInt("source_id");
                trx.setSourceId(rs.wasNull() ? null : sourceId);
                trx.setSourceName(rs.getString("source_name")); // Akan null jika tidak ada join match

                trx.setDeleted(rs.getBoolean("is_deleted"));
                trx.setCreatedAt(rs.getTimestamp("created_at"));
                trx.setUpdatedAt(rs.getTimestamp("updated_at"));
                transactions.add(trx);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return transactions;
    }
    
    // Metode untuk Dashboard akan kita tambahkan di sini nanti
    // seperti getTotalAmountByTypeAndDateRange dan getRecentTransactions
    public BigDecimal getTotalAmountByTypeAndDateRange(int userId, TransactionType type, Date startDate, Date endDate) throws SQLException {
        // Bangun query SQL secara dinamis berdasarkan parameter tanggal
        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT SUM(amount) AS total FROM transactions WHERE user_id = ? AND type = ? AND is_deleted = FALSE"
        );

        if (startDate != null) {
            sqlBuilder.append(" AND transaction_date >= ?");
        }
        if (endDate != null) {
            sqlBuilder.append(" AND transaction_date <= ?");
        }

        String sql = sqlBuilder.toString();
        BigDecimal totalAmount = BigDecimal.ZERO; // Default jika tidak ada data
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            int paramIndex = 1;
            pstmt.setInt(paramIndex++, userId);
            pstmt.setString(paramIndex++, type.getDatabaseValue()); // "income" atau "expense"

            if (startDate != null) {
                pstmt.setDate(paramIndex++, startDate);
            }
            if (endDate != null) {
                pstmt.setDate(paramIndex++, endDate);
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                BigDecimal sum = rs.getBigDecimal("total");
                if (sum != null) {
                    totalAmount = sum;
                }
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            // Koneksi tidak ditutup di sini, biarkan dikelola oleh pemanggil atau DBConnectionUtil
        }
        return totalAmount;
    }

    /**
     * Mendapatkan daftar transaksi terbaru untuk pengguna tertentu.
     *
     * @param userId ID pengguna.
     * @param limit Jumlah maksimal transaksi yang ingin ditampilkan.
     * @return List dari objek Transaction.
     * @throws SQLException jika terjadi error database.
     */
    public List<com.desktopfinance.model.Transaction> getRecentTransactions(int userId, int limit) throws SQLException {
        List<com.desktopfinance.model.Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ? AND is_deleted = FALSE ORDER BY transaction_date DESC, created_at DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                com.desktopfinance.model.Transaction trx = new com.desktopfinance.model.Transaction();
                trx.setTransactionId(rs.getInt("transaction_id"));
                trx.setUserId(rs.getInt("user_id"));
                
                // Konversi string dari DB ke Enum TransactionType
                String typeStr = rs.getString("type");
                if (typeStr != null) {
                    trx.setType(TransactionType.fromDatabaseValue(typeStr));
                }

                trx.setAmount(rs.getBigDecimal("amount"));
                trx.setTransactionDate(rs.getDate("transaction_date"));
                trx.setDescription(rs.getString("description"));
                
                // category_id dan source_id bisa null
                Integer categoryId = rs.getInt("category_id");
                if (rs.wasNull()) { // Cek apakah nilai SQL terakhir yang dibaca adalah NULL
                    trx.setCategoryId(null);
                } else {
                    trx.setCategoryId(categoryId);
                }

                Integer sourceId = rs.getInt("source_id");
                if (rs.wasNull()) {
                    trx.setSourceId(null);
                } else {
                    trx.setSourceId(sourceId);
                }
                
                trx.setDeleted(rs.getBoolean("is_deleted"));
                trx.setCreatedAt(rs.getTimestamp("created_at"));
                trx.setUpdatedAt(rs.getTimestamp("updated_at"));
                transactions.add(trx);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        return transactions;
    }
    public List<Transaction> getSoftDeletedTransactionsByUserId(int userId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        // Query ini sama dengan getAllTransactionsByUserId, tapi dengan kondisi is_deleted = TRUE
        String sql = "SELECT t.transaction_id, t.user_id, t.type, t.amount, t.transaction_date, " +
                     "t.description, t.category_id, ec.category_name, t.source_id, isrc.source_name, " +
                     "t.is_deleted, t.created_at, t.updated_at " +
                     "FROM transactions t " +
                     "LEFT JOIN expense_categories ec ON t.category_id = ec.category_id " +
                     "LEFT JOIN income_sources isrc ON t.source_id = isrc.source_id " +
                     "WHERE t.user_id = ? AND t.is_deleted = TRUE " + // Kondisi utama di sini
                     "ORDER BY t.transaction_date DESC, t.created_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnectionUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction trx = new Transaction();
                trx.setTransactionId(rs.getInt("transaction_id"));
                trx.setUserId(rs.getInt("user_id"));

                String typeStr = rs.getString("type");
                if (typeStr != null) {
                    trx.setType(TransactionType.fromDatabaseValue(typeStr));
                }

                trx.setAmount(rs.getBigDecimal("amount"));
                trx.setTransactionDate(rs.getDate("transaction_date"));
                trx.setDescription(rs.getString("description"));

                Integer categoryId = rs.getInt("category_id");
                trx.setCategoryId(rs.wasNull() ? null : categoryId);
                trx.setCategoryName(rs.getString("category_name"));

                Integer sourceId = rs.getInt("source_id");
                trx.setSourceId(rs.wasNull() ? null : sourceId);
                trx.setSourceName(rs.getString("source_name"));

                trx.setDeleted(rs.getBoolean("is_deleted")); // Ini akan TRUE
                trx.setCreatedAt(rs.getTimestamp("created_at"));
                trx.setUpdatedAt(rs.getTimestamp("updated_at"));
                transactions.add(trx);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            // Koneksi tidak ditutup di sini
        }
        return transactions;
    }
}