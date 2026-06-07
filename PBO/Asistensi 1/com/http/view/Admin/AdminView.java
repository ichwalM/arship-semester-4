package com.http.view.Admin;

import com.http.controller.AdminController;
import com.http.model.Admin;

import java.util.Scanner;
import java.util.List;

public class AdminView {
    private Scanner input = new Scanner(System.in);
    private AdminController adminController;

    // Konstruktor dengan dependency injection
    public AdminView(AdminController controller) {
        this.adminController = controller;
    }

    // Form input untuk menambah admin baru
    public void addAdmin() {
        System.out.println("\n=== Tambah Admin ===");
        String name = getInput("Input Name     : ");
        String username = getInput("Input Username : ");
        String password = getInput("Input Password : ");

        adminController.addAdmin(name, username, password);
        System.out.println("Admin berhasil ditambahkan!");
    }

    // Tampilkan daftar admin
    public void showAdmin(List<Admin> adminList) {
        System.out.println("\n=== Daftar Admin ===");
        for (Admin admin : adminList) {
            System.out.printf("Name: %s | Username: %s | Role: %s%n",
                admin.getName(), admin.getUsername(), admin.role);
        }
    }

    // Helper untuk mendapatkan input dari pengguna
    private String getInput(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }
}