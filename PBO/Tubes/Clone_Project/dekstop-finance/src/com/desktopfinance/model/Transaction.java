package com.desktopfinance.model;

import java.math.BigDecimal; // Untuk jumlah uang (amount) agar presisi
import java.sql.Date;       // Untuk tanggal transaksi (transactionDate)
import java.sql.Timestamp;  // Untuk createdAt dan updatedAt

public class Transaction {

    private int transactionId;
    private int userId; // Foreign key ke tabel users
    private TransactionType type; // Menggunakan Enum yang sudah kita buat
    private BigDecimal amount;
    private Date transactionDate;
    private String description;
    private Integer categoryId; // Foreign key ke expense_categories, bisa null
    private Integer sourceId;   // Foreign key ke income_sources, bisa null
    private boolean isDeleted;  // Untuk soft delete
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    private String categoryName; // Untuk menyimpan nama kategori dari join query
    private String sourceName;   // Untuk menyimpan nama sumber dari join query

    // Default constructor
    public Transaction() {
    }

    // Constructor dengan semua field (atau yang paling sering digunakan)
    public Transaction(int transactionId, int userId, TransactionType type, BigDecimal amount,
                       Date transactionDate, String description, Integer categoryId,
                       Integer sourceId, boolean isDeleted, Timestamp createdAt, Timestamp updatedAt) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
        this.categoryId = categoryId;
        this.sourceId = sourceId;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Constructor untuk transaksi baru (sebelum disimpan ke DB)
    // isDeleted defaultnya false, createdAt dan updatedAt biasanya di-set oleh DB/DAO
    public Transaction(int userId, TransactionType type, BigDecimal amount,
                       Date transactionDate, String description, Integer categoryId, Integer sourceId) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
        this.categoryId = categoryId;
        this.sourceId = sourceId;
        this.isDeleted = false; // Default untuk transaksi baru
    }


    // Getter dan Setter untuk semua atribut

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public boolean isDeleted() { // Getter untuk boolean biasanya isNamaVariabel()
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }


    // Opsional: Override toString() untuk debugging
    @Override
    public String toString() {
        return "Transaction{" +
               "transactionId=" + transactionId +
               ", userId=" + userId +
               ", type=" + type +
               ", amount=" + amount +
               ", transactionDate=" + transactionDate +
               ", description='" + description + '\'' +
               ", categoryId=" + categoryId +
               ", sourceId=" + sourceId +
               ", isDeleted=" + isDeleted +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}