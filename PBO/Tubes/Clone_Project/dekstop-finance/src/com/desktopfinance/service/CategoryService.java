package com.desktopfinance.service;

import com.desktopfinance.dao.ExpenseCategoryDAO;
import com.desktopfinance.model.ExpenseCategory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    private ExpenseCategoryDAO expenseCategoryDAO;

    public CategoryService() {
        this.expenseCategoryDAO = new ExpenseCategoryDAO();
    }

    /**
     * Mengambil semua kategori pengeluaran yang relevan untuk pengguna
     * (kategori global dan kategori milik pengguna).
     *
     * @param userId ID pengguna.
     * @return List dari objek ExpenseCategory.
     */
    public List<ExpenseCategory> getCategoriesForUser(int userId) {
        try {
            return expenseCategoryDAO.getAllActiveCategoriesForUser(userId);
        } catch (SQLException e) {
            System.err.println("Error saat mengambil kategori (Service): " + e.getMessage());
            // e.printStackTrace();
            return new ArrayList<>(); // Kembalikan list kosong jika error
        }
    }

    /**
     * Menambahkan kategori pengeluaran baru untuk pengguna.
     *
     * @param categoryName Nama kategori baru.
     * @param userId ID pengguna yang membuat kategori.
     * @return true jika berhasil ditambahkan, false jika gagal atau nama kategori kosong.
     */
    public boolean addCategory(String categoryName, int userId) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            System.err.println("Nama kategori tidak boleh kosong.");
            return false;
        }

        // Cek duplikasi nama kategori (untuk user yang sama atau global jika ada aturan)
        // Untuk saat ini, kita asumsikan DAO atau database akan menangani unique constraint jika ada.
        // Atau bisa ditambahkan logika cek duplikasi di sini:
        // List<ExpenseCategory> existingCategories = getCategoriesForUser(userId);
        // for (ExpenseCategory ec : existingCategories) {
        //     if (ec.getCategoryName().equalsIgnoreCase(categoryName.trim())) {
        //         System.err.println("Nama kategori sudah ada.");
        //         return false;
        //     }
        // }


        ExpenseCategory newCategory = new ExpenseCategory();
        newCategory.setCategoryName(categoryName.trim());
        newCategory.setUserId(userId); // Kategori ini milik pengguna yang sedang login

        try {
            int newCategoryId = expenseCategoryDAO.addCategory(newCategory);
            return newCategoryId != -1;
        } catch (SQLException e) {
            System.err.println("Error saat menambah kategori (Service): " + e.getMessage());
            // e.printStackTrace();
            return false;
        }
    }

    /**
     * Mengupdate nama kategori pengeluaran milik pengguna.
     *
     * @param categoryId ID kategori yang akan diupdate.
     * @param newCategoryName Nama kategori baru.
     * @param userId ID pengguna (untuk verifikasi kepemilikan).
     * @return true jika berhasil diupdate, false jika gagal.
     */
    public boolean updateCategory(int categoryId, String newCategoryName, int userId) {
        if (newCategoryName == null || newCategoryName.trim().isEmpty()) {
            System.err.println("Nama kategori baru tidak boleh kosong.");
            return false;
        }

        try {
            // Verifikasi apakah kategori ini milik pengguna atau global (jika ada aturan khusus)
            ExpenseCategory existingCategory = expenseCategoryDAO.getCategoryById(categoryId);
            if (existingCategory == null) {
                System.err.println("Kategori dengan ID " + categoryId + " tidak ditemukan.");
                return false;
            }
            // Pengguna hanya boleh mengedit kategori miliknya, bukan kategori global.
            if (existingCategory.getUserId() == null || existingCategory.getUserId() != userId) {
                 System.err.println("Pengguna tidak memiliki hak untuk mengupdate kategori ini.");
                 return false;
            }

            // Cek duplikasi nama kategori lain (opsional, tergantung aturan bisnis)

            ExpenseCategory categoryToUpdate = new ExpenseCategory();
            categoryToUpdate.setCategoryId(categoryId);
            categoryToUpdate.setCategoryName(newCategoryName.trim());
            // userId pada objek tidak perlu di-set karena DAO akan menggunakan parameter userId untuk WHERE clause

            return expenseCategoryDAO.updateCategory(categoryToUpdate, userId);
        } catch (SQLException e) {
            System.err.println("Error saat mengupdate kategori (Service): " + e.getMessage());
            // e.printStackTrace();
            return false;
        }
    }

    /**
     * Menghapus kategori pengeluaran milik pengguna.
     *
     * @param categoryId ID kategori yang akan dihapus.
     * @param userId ID pengguna (untuk verifikasi kepemilikan).
     * @return true jika berhasil dihapus, false jika gagal.
     */
    public boolean deleteCategory(int categoryId, int userId) {
        try {
            // Verifikasi apakah kategori ini milik pengguna atau global
            ExpenseCategory categoryToDelete = expenseCategoryDAO.getCategoryById(categoryId);
            if (categoryToDelete == null) {
                System.err.println("Kategori dengan ID " + categoryId + " tidak ditemukan untuk dihapus.");
                return false;
            }
            // Pengguna hanya boleh menghapus kategori miliknya. Kategori global (userId == null) tidak boleh dihapus.
            if (categoryToDelete.getUserId() == null || categoryToDelete.getUserId() != userId) {
                 System.err.println("Pengguna tidak memiliki hak untuk menghapus kategori ini.");
                 return false;
            }
            
            // Pertimbangan: Cek apakah kategori sedang digunakan di transaksi?
            // DAO kita sudah diset ON DELETE SET NULL, jadi transaksi akan aman, category_id nya jadi NULL.
            // Jika ingin mencegah delete jika masih dipakai, tambahkan logika cek di sini.

            return expenseCategoryDAO.deleteCategory(categoryId, userId);
        } catch (SQLException e) {
            System.err.println("Error saat menghapus kategori (Service): " + e.getMessage());
            // e.printStackTrace();
            return false;
        }
    }
}