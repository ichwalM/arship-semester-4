package com.http.controller;

import com.http.model.Admin;
import com.http.view.Admin.AdminView;

import java.util.ArrayList;
import java.util.List;

public class AdminController {
    private List<Admin> adminList; // List untuk menyimpan data admin
    private AdminView adminView;

    // Konstruktor dengan dependency injection
    public AdminController(AdminView view) {
        this.adminList = new ArrayList<>();
        this.adminView = view;
    }

    // Metode untuk menambah admin
    public void addAdmin(String name, String username, String password) {
        // Buat objek Admin dari data yang diterima
        Admin admin = new Admin(name, username, password);

        // Simpan admin ke dalam list
        adminList.add(admin);

        // Berikan feedback ke View
        adminView.showMessage("Admin berhasil ditambahkan: " + name);
    }

    // Metode untuk menampilkan daftar admin
    public List<Admin> getAllAdmins() {
        return adminList;
    }
}