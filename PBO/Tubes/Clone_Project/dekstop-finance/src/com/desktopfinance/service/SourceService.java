package com.desktopfinance.service;

import com.desktopfinance.dao.IncomeSourceDAO;
import com.desktopfinance.model.IncomeSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SourceService {

    private IncomeSourceDAO incomeSourceDAO;

    public SourceService() {
        this.incomeSourceDAO = new IncomeSourceDAO();
    }

    /**
     * Mengambil semua sumber pemasukan yang relevan untuk pengguna
     * (sumber global dan sumber milik pengguna).
     *
     * @param userId ID pengguna.
     * @return List dari objek IncomeSource.
     */
    public List<IncomeSource> getSourcesForUser(int userId) {
        try {
            return incomeSourceDAO.getAllActiveSourcesForUser(userId);
        } catch (SQLException e) {
            System.err.println("Error saat mengambil sumber pemasukan (Service): " + e.getMessage());
            return new ArrayList<>(); // Kembalikan list kosong jika error
        }
    }

    /**
     * Menambahkan sumber pemasukan baru untuk pengguna.
     *
     * @param sourceName Nama sumber baru.
     * @param userId ID pengguna yang membuat sumber.
     * @return true jika berhasil ditambahkan, false jika gagal atau nama sumber kosong.
     */
    public boolean addSource(String sourceName, int userId) {
        if (sourceName == null || sourceName.trim().isEmpty()) {
            System.err.println("Nama sumber pemasukan tidak boleh kosong.");
            return false;
        }

        // Opsional: Cek duplikasi nama sumber untuk pengguna yang sama
        // List<IncomeSource> existingSources = getSourcesForUser(userId);
        // for (IncomeSource existingSource : existingSources) {
        //     if (existingSource.getSourceName().equalsIgnoreCase(sourceName.trim()) && 
        //         (existingSource.getUserId() != null && existingSource.getUserId() == userId)) {
        //         System.err.println("Nama sumber pemasukan sudah ada untuk pengguna ini.");
        //         return false;
        //     }
        // }

        IncomeSource newSource = new IncomeSource();
        newSource.setSourceName(sourceName.trim());
        newSource.setUserId(userId); // Sumber ini milik pengguna yang sedang login

        try {
            int newSourceId = incomeSourceDAO.addSource(newSource);
            return newSourceId != -1;
        } catch (SQLException e) {
            System.err.println("Error saat menambah sumber pemasukan (Service): " + e.getMessage());
            return false;
        }
    }

    /**
     * Mengupdate nama sumber pemasukan milik pengguna.
     *
     * @param sourceId ID sumber yang akan diupdate.
     * @param newSourceName Nama sumber baru.
     * @param userId ID pengguna (untuk verifikasi kepemilikan).
     * @return true jika berhasil diupdate, false jika gagal.
     */
    public boolean updateSource(int sourceId, String newSourceName, int userId) {
        if (newSourceName == null || newSourceName.trim().isEmpty()) {
            System.err.println("Nama sumber baru tidak boleh kosong.");
            return false;
        }

        try {
            IncomeSource existingSource = incomeSourceDAO.getSourceById(sourceId);
            if (existingSource == null) {
                System.err.println("Sumber pemasukan dengan ID " + sourceId + " tidak ditemukan.");
                return false;
            }
            // Pengguna hanya boleh mengedit sumber miliknya, bukan sumber global.
            if (existingSource.getUserId() == null || existingSource.getUserId() != userId) {
                 System.err.println("Pengguna tidak memiliki hak untuk mengupdate sumber ini.");
                 return false;
            }

            // Opsional: Cek duplikasi nama sumber lain

            IncomeSource sourceToUpdate = new IncomeSource();
            sourceToUpdate.setSourceId(sourceId);
            sourceToUpdate.setSourceName(newSourceName.trim());
            // userId pada objek tidak di-set karena DAO akan menggunakan parameter userId untuk WHERE clause

            return incomeSourceDAO.updateSource(sourceToUpdate, userId);
        } catch (SQLException e) {
            System.err.println("Error saat mengupdate sumber pemasukan (Service): " + e.getMessage());
            return false;
        }
    }

    /**
     * Menghapus sumber pemasukan milik pengguna.
     *
     * @param sourceId ID sumber yang akan dihapus.
     * @param userId ID pengguna (untuk verifikasi kepemilikan).
     * @return true jika berhasil dihapus, false jika gagal.
     */
    public boolean deleteSource(int sourceId, int userId) {
        try {
            IncomeSource sourceToDelete = incomeSourceDAO.getSourceById(sourceId);
            if (sourceToDelete == null) {
                System.err.println("Sumber pemasukan dengan ID " + sourceId + " tidak ditemukan untuk dihapus.");
                return false;
            }
            // Pengguna hanya boleh menghapus sumber miliknya. Sumber global (userId == null) tidak boleh dihapus.
            if (sourceToDelete.getUserId() == null || sourceToDelete.getUserId() != userId) {
                 System.err.println("Pengguna tidak memiliki hak untuk menghapus sumber ini.");
                 return false;
            }
            
            return incomeSourceDAO.deleteSource(sourceId, userId);
        } catch (SQLException e) {
            System.err.println("Error saat menghapus sumber pemasukan (Service): " + e.getMessage());
            return false;
        }
    }
}