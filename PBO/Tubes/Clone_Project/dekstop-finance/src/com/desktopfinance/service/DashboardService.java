package com.desktopfinance.service;

import com.desktopfinance.dao.TransactionDAO;
import com.desktopfinance.model.Transaction; // Pastikan Transaction diimport
import com.desktopfinance.model.TransactionType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList; // Untuk list kosong jika error
import java.util.Calendar;
import java.sql.Date; // java.sql.Date untuk DAO
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DashboardService {

    private TransactionDAO transactionDAO;

    public DashboardService() {
        this.transactionDAO = new TransactionDAO();
    }

    /**
     * Mendapatkan data ringkasan untuk dashboard utama pengguna.
     * Termasuk total pemasukan bulan ini, total pengeluaran bulan ini, dan saldo akhir keseluruhan.
     *
     * @param userId ID pengguna.
     * @return Map yang berisi "totalIncomeThisMonth", "totalExpenseThisMonth", dan "currentBalance".
     */
    public Map<String, BigDecimal> getDashboardSummary(int userId) {
        Map<String, BigDecimal> summary = new HashMap<>();
        BigDecimal totalIncomeThisMonth = BigDecimal.ZERO;
        BigDecimal totalExpenseThisMonth = BigDecimal.ZERO;
        BigDecimal currentBalance = BigDecimal.ZERO;

        // Tentukan tanggal awal dan akhir bulan ini untuk ringkasan dashboard
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1); // Tanggal 1 bulan ini
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        Date startDateThisMonth = new Date(cal.getTimeInMillis());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); // Tanggal terakhir bulan ini
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999);
        Date endDateThisMonth = new Date(cal.getTimeInMillis());
        
        try {
            // Pemasukan & Pengeluaran Bulan Ini untuk Dashboard
            totalIncomeThisMonth = transactionDAO.getTotalAmountByTypeAndDateRange(userId, TransactionType.INCOME, startDateThisMonth, endDateThisMonth);
            totalExpenseThisMonth = transactionDAO.getTotalAmountByTypeAndDateRange(userId, TransactionType.EXPENSE, startDateThisMonth, endDateThisMonth);

            // Saldo Keseluruhan (semua waktu)
            BigDecimal totalIncomeAllTime = transactionDAO.getTotalAmountByTypeAndDateRange(userId, TransactionType.INCOME, null, null);
            BigDecimal totalExpenseAllTime = transactionDAO.getTotalAmountByTypeAndDateRange(userId, TransactionType.EXPENSE, null, null);
            currentBalance = totalIncomeAllTime.subtract(totalExpenseAllTime);

        } catch (SQLException e) {
            System.err.println("Error saat mengambil ringkasan dashboard (Service): " + e.getMessage());
            // Biarkan nilai default ZERO jika error
        }

        summary.put("totalIncomeThisMonth", totalIncomeThisMonth != null ? totalIncomeThisMonth : BigDecimal.ZERO);
        summary.put("totalExpenseThisMonth", totalExpenseThisMonth != null ? totalExpenseThisMonth : BigDecimal.ZERO);
        summary.put("currentBalance", currentBalance != null ? currentBalance : BigDecimal.ZERO);
        return summary;
    }

    /**
     * Mendapatkan daftar transaksi terbaru untuk dashboard utama.
     *
     * @param userId ID pengguna.
     * @param limit Jumlah transaksi yang ingin ditampilkan.
     * @return List dari objek Transaction.
     */
    public List<Transaction> getRecentTransactionsForDashboard(int userId, int limit) {
        try {
            return transactionDAO.getRecentTransactions(userId, limit);
        } catch (SQLException e) {
            System.err.println("Error saat mengambil transaksi terbaru (Service): " + e.getMessage());
            return new ArrayList<>(); // Kembalikan list kosong jika error
        }
    }

    /**
     * Menghasilkan data laporan bulanan (total pemasukan dan pengeluaran) untuk pengguna tertentu.
     *
     * @param userId ID pengguna.
     * @param year Tahun laporan.
     * @param month Bulan laporan (1 untuk Januari, 12 untuk Desember).
     * @return Map yang berisi "totalIncome" dan "totalExpense" untuk bulan tersebut.
     */
    public Map<String, BigDecimal> getMonthlyReport(int userId, int year, int month) {
        Map<String, BigDecimal> reportData = new HashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        if (month < 1 || month > 12) {
            System.err.println("Bulan tidak valid untuk laporan: " + month);
            reportData.put("totalIncome", totalIncome);
            reportData.put("totalExpense", totalExpense);
            return reportData;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar.MONTH adalah 0-indexed
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        Date startDate = new Date(cal.getTimeInMillis());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999);
        Date endDate = new Date(cal.getTimeInMillis());

        try {
            totalIncome = transactionDAO.getTotalAmountByTypeAndDateRange(userId, TransactionType.INCOME, startDate, endDate);
            totalExpense = transactionDAO.getTotalAmountByTypeAndDateRange(userId, TransactionType.EXPENSE, startDate, endDate);
        } catch (SQLException e) {
            System.err.println("Error saat mengambil data laporan bulanan (Service): " + e.getMessage());
        }

        reportData.put("totalIncome", totalIncome != null ? totalIncome : BigDecimal.ZERO);
        reportData.put("totalExpense", totalExpense != null ? totalExpense : BigDecimal.ZERO);
        return reportData;
    }

    /**
     * Menghasilkan data laporan tahunan (total pemasukan dan pengeluaran) untuk pengguna tertentu.
     *
     * @param userId ID pengguna.
     * @param year Tahun laporan.
     * @return Map yang berisi "totalIncome" dan "totalExpense" untuk tahun tersebut.
     */
    public Map<String, BigDecimal> getAnnualReport(int userId, int year) {
        Map<String, BigDecimal> reportData = new HashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, Calendar.JANUARY); // Januari (0)
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        Date startDate = new Date(cal.getTimeInMillis());

        cal.set(Calendar.MONTH, Calendar.DECEMBER); // Desember (11)
        cal.set(Calendar.DAY_OF_MONTH, 31); // Hari terakhir Desember
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59); cal.set(Calendar.MILLISECOND, 999);
        Date endDate = new Date(cal.getTimeInMillis());
        
        try {
            totalIncome = transactionDAO.getTotalAmountByTypeAndDateRange(userId, TransactionType.INCOME, startDate, endDate);
            totalExpense = transactionDAO.getTotalAmountByTypeAndDateRange(userId, TransactionType.EXPENSE, startDate, endDate);
        } catch (SQLException e) {
            System.err.println("Error saat mengambil data laporan tahunan (Service): " + e.getMessage());
        }

        reportData.put("totalIncome", totalIncome != null ? totalIncome : BigDecimal.ZERO);
        reportData.put("totalExpense", totalExpense != null ? totalExpense : BigDecimal.ZERO);
        return reportData;
    }
}