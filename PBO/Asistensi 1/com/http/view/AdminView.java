package com.http.view;

import java.util.Scanner;
import com.http.controller.AdminController;
import com.http.model.Admin;

import java.util.List;

public class AdminView {
    private Scanner input = new Scanner(System.in); // Properti private untuk enkapsulasi
    private AdminController adminController;

    // Konstruktor dengan dependency injection
    public AdminView(AdminController adminController) {
        this.adminController = adminController;
    }

    // Metode untuk menampilkan daftar admin
    public void showAdmins(List<Admin> adminList) {
        System.out.printf("%-20s %-15s%n", "Name", "Username");
        System.out.println("----------------------------");
        for (Admin admin : adminList) {
            System.out.printf("%-20s %-15s%n", admin.getName(), admin.getUsername());
        }
    }

    // Metode untuk menambah admin baru
    public void addAdmin() {
        System.out.println("=== Tambah Admin ===");
        String name = getInput("Input Name     : ");
        String username = getInput("Input Username : ");
        String password = getInput("Input Password : ");

        if (name.isEmpty() || username.isEmpty() || password.length() < 6) {
            System.out.println("Input tidak valid. Pastikan semua field terisi dan password minimal 6 karakter.");
            return;
        }

        // Memanggil controller untuk menambah admin
        adminController.addAdmin(name, username, password);
    }

    // Helper untuk input
    private String getInput(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }

    // Metode untuk menampilkan pesan
    public void showMessage(String message) {
        System.out.println(message);
    }
}