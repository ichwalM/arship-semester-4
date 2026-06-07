package com.desktopfinance.service;

import com.desktopfinance.dao.TransactionDAO;
import com.desktopfinance.model.Transaction;
import com.desktopfinance.dao.ExpenseCategoryDAO; // Import baru
import com.desktopfinance.dao.IncomeSourceDAO;   // Import baru
import com.desktopfinance.model.ExpenseCategory; // Import baru
import com.desktopfinance.model.IncomeSource;   // Import baru


import java.sql.SQLException;
import java.util.ArrayList; // Untuk mengembalikan list kosong jika error
import java.util.List;      // Untuk tipe return List

public class TransactionService {

    private TransactionDAO transactionDAO;
    private ExpenseCategoryDAO expenseCategoryDAO; // Tambahkan ini
    private IncomeSourceDAO incomeSourceDAO;     // Tambahkan ini

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
        this.expenseCategoryDAO = new ExpenseCategoryDAO(); // Inisialisasi
        this.incomeSourceDAO = new IncomeSourceDAO();     // Inisialisasi
    }

    /**
     * Menambahkan transaksi baru.
     *
     * @param transaction Objek Transaction yang akan ditambahkan.
     * @return true jika transaksi berhasil ditambahkan, false jika gagal.
     */
    public boolean addTransaction(Transaction transaction) {
        try {
            return transactionDAO.addTransaction(transaction);
        } catch (SQLException e) {
            System.err.println("Error saat menyimpan transaksi (Service): " + e.getMessage());
            // e.printStackTrace(); // Untuk debugging lebih detail
            return false;
        }
    }

    /**
     * Mengambil semua kategori pengeluaran yang aktif untuk pengguna tertentu (termasuk global).
     *
     * @param userId ID pengguna.
     * @return List dari objek ExpenseCategory.
     */
    public List<ExpenseCategory> getActiveExpenseCategories(int userId) {
        try {
            // Metode DAO kita sudah menangani pengambilan kategori global dan user-spesifik
            return expenseCategoryDAO.getAllActiveCategoriesForUser(userId);
        } catch (SQLException e) {
            System.err.println("Error mengambil kategori pengeluaran (Service): " + e.getMessage());
            // e.printStackTrace();
            return new ArrayList<>(); // Kembalikan list kosong jika error
        }
    }
    //for soft dellete
    public boolean softDeleteTransaction(int transactionId, int userId) {
        try {
            return transactionDAO.softDeleteTransaction(transactionId, userId);
        } catch (SQLException e) {
            System.err.println("Error saat soft delete transaksi (Service): " + e.getMessage());
            // e.printStackTrace(); // Untuk debugging lebih detail
            return false;
        }
    }
    
    //for restort delette
    public boolean restoreTransaction(int transactionId, int userId) {
        try {
            return transactionDAO.restoreTransaction(transactionId, userId);
        } catch (SQLException e) {
            System.err.println("Error saat restore transaksi (Service): " + e.getMessage());
            // e.printStackTrace();
            return false;
        }
    }
    
    //for update
    public boolean updateTransaction(Transaction transaction) {
        try {
            return transactionDAO.updateTransaction(transaction);
        } catch (SQLException e) {
            System.err.println("Error saat update transaksi (Service): " + e.getMessage());
            return false;
        }
    }

    /**
     * Mengambil semua sumber pemasukan yang aktif untuk pengguna tertentu (termasuk global).
     *
     * @param userId ID pengguna.
     * @return List dari objek IncomeSource.
     */
    public List<IncomeSource> getActiveIncomeSources(int userId) {
        try {
            // Metode DAO kita sudah menangani pengambilan sumber global dan user-spesifik
            return incomeSourceDAO.getAllActiveSourcesForUser(userId);
        } catch (SQLException e) {
            System.err.println("Error mengambil sumber pemasukan (Service): " + e.getMessage());
            // e.printStackTrace();
            return new ArrayList<>(); // Kembalikan list kosong jika error
        }
    }
    
    // Di TransactionService.java
    public List<Transaction> getAllTransactionsForUser(int userId) {
        try {
            return transactionDAO.getAllTransactionsByUserId(userId); // Panggil metode DAO yang sudah diupdate
        } catch (SQLException e) {
            System.err.println("Error saat mengambil semua transaksi (Service): " + e.getMessage());
            // e.printStackTrace();
            return new ArrayList<>(); // Kembalikan list kosong jika error
        }
    }
    
    public List<Transaction> getSoftDeletedTransactionsForUser(int userId) {
        try {
            return transactionDAO.getSoftDeletedTransactionsByUserId(userId);
        } catch (SQLException e) {
            System.err.println("Error saat mengambil transaksi yang terhapus (Service): " + e.getMessage());
            // e.printStackTrace();
            return new ArrayList<>(); // Kembalikan list kosong jika error
        }
    }
    
    
    // Lalu di ReportPanel.loadAllTransactions(), ganti pemanggilan DAO dengan:
    // transactions = transactionService.getAllTransactionsForUser(currentUser.getUserId());
    
    // Metode lain terkait transaksi bisa ditambahkan di sini nanti
    // seperti updateTransaction, deleteTransaction, getAllTransactions, dll.
}