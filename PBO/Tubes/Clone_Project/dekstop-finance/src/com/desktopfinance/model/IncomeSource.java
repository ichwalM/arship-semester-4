package com.desktopfinance.model;

import java.sql.Timestamp;

public class IncomeSource {

    private int sourceId;
    private String sourceName;
    private Integer userId; // Menggunakan Integer agar bisa bernilai null (untuk sumber global)
    private Timestamp createdAt;

    // Default constructor
    public IncomeSource() {
    }

    // Constructor untuk membuat objek dengan semua field
    public IncomeSource(int sourceId, String sourceName, Integer userId, Timestamp createdAt) {
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Constructor untuk sumber baru (sebelum disimpan ke DB, tanpa sourceId dan createdAt)
    public IncomeSource(String sourceName, Integer userId) {
        this.sourceName = sourceName;
        this.userId = userId;
    }
    
    // Constructor untuk sumber baru yang global (tanpa userId)
    public IncomeSource(String sourceName) {
        this.sourceName = sourceName;
        this.userId = null; // Eksplisit set userId ke null untuk sumber global
    }

    // Getter dan Setter

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
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
        return this.sourceName; // JComboBox akan menampilkan ini
    }
}