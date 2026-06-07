package com.desktopfinance.model;

import java.sql.Timestamp; // Untuk createdAt dan updatedAt

public class User {

    private int userId;
    private String username;
    private String password; // Sebaiknya hanya digunakan untuk transfer, bukan penyimpanan jangka panjang di objek
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public User() {
    }

    // Constructor dengan semua field (atau yang relevan)
    public User(int userId, String username, String password, Timestamp createdAt, Timestamp updatedAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Constructor tanpa userId (untuk user baru sebelum disimpan ke DB)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }


    // Getter dan Setter untuk setiap atribut

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    // Opsional: Override toString() untuk debugging yang lebih mudah
    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
               ", username='" + username + '\'' +
               // Jangan sertakan password dalam toString untuk keamanan
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}