package com.desktopfinance.model;

import java.sql.Timestamp;

public class ExpenseCategory {

    private int categoryId;
    private String categoryName;
    private Integer userId; // Menggunakan Integer agar bisa bernilai null (untuk kategori global)
    private Timestamp createdAt;

    // Default constructor
    public ExpenseCategory() {
    }

    // Constructor untuk membuat objek dengan semua field
    public ExpenseCategory(int categoryId, String categoryName, Integer userId, Timestamp createdAt) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Constructor untuk kategori baru (sebelum disimpan ke DB, tanpa categoryId dan createdAt)
    public ExpenseCategory(String categoryName, Integer userId) {
        this.categoryName = categoryName;
        this.userId = userId;
    }
    
    // Constructor untuk kategori baru yang global (tanpa userId)
    public ExpenseCategory(String categoryName) {
        this.categoryName = categoryName;
        this.userId = null; // Eksplisit set userId ke null untuk kategori global
    }

    // Getter dan Setter

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Opsional: Override toString() untuk debugging
    @Override
    public String toString() {
        return this.categoryName; // JComboBox akan menampilkan ini
    }
}