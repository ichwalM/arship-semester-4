package com.http;

import com.http.controller.AdminController;
import com.http.model.Admin;
import com.http.view.AdminView;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        // Inisialisasi View
        AdminView adminView = new AdminView();

        // Inisialisasi Controller dengan View
        AdminController adminController = new AdminController(adminView);

        // Loop untuk Menu Interaktif
        boolean isRunning = true;
        Scanner scanner = new Scanner(System.in);

        while (isRunning) {
            System.out.println("\n=== Menu Aplikasi ===");
            System.out.println("1. Tambah Admin");
            System.out.println("2. Tampilkan Daftar Admin");
            System.out.println("3. Keluar");
            System.out.print("Pilih opsi: ");

            int pilihan = scanner.nextInt();
            scanner.nextLine(); // Kosongkan buffer input

            switch (pilihan) {
                case 1:
                    // Panggil metode addAdmin di View
                    adminView.addAdmin();
                    break;
                case 2:
                    // Panggil metode displayAllAdmins di Controller
                    adminController.displayAllAdmins();
                    break;
                case 3:
                    System.out.println("Keluar dari aplikasi...");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Coba lagi.");
            }
        }

        scanner.close();
    }
}