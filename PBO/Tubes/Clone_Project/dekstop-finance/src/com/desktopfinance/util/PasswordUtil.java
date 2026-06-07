package com.desktopfinance.util;

import org.mindrot.jbcrypt.BCrypt; // Import library jBcrypt

public class PasswordUtil {

    /**
     * Menghasilkan hash BCrypt dari password plain text.
     * Salt akan digenerate secara otomatis oleh BCrypt.gensalt().
     *
     * @param plainPassword Password yang akan di-hash.
     * @return String yang berisi hash password.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong.");
        }
        // Parameter gensalt() adalah log_rounds, semakin tinggi semakin aman tapi lebih lambat.
        // Nilai defaultnya 10, yang umumnya cukup baik.
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Memverifikasi password plain text dengan hash yang tersimpan di database.
     *
     * @param plainPassword Password plain text yang diinput pengguna.
     * @param hashedPassword Hash password yang diambil dari database.
     * @return true jika password cocok, false jika tidak.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.isEmpty() || hashedPassword == null || hashedPassword.isEmpty()) {
            // Jika salah satu null atau kosong, anggap tidak cocok atau lemparkan error
            return false; 
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Ini bisa terjadi jika hashedPassword tidak valid formatnya
            System.err.println("Error saat memeriksa password: format hash tidak valid. " + e.getMessage());
            return false;
        }
    }

    // --- Main method untuk testing (opsional) ---
    public static void main(String[] args) {
        String originalPassword = "password123";
        String generatedHash = hashPassword(originalPassword);
        System.out.println("Original Password: " + originalPassword);
        System.out.println("Generated BCrypt Hash: " + generatedHash);
        System.out.println("Panjang Hash: " + generatedHash.length()); // Biasanya 60 karakter

        // Tes verifikasi
        boolean isPasswordCorrect = checkPassword(originalPassword, generatedHash);
        System.out.println("Verifikasi dengan password benar: " + isPasswordCorrect); // Harusnya true

        boolean isPasswordIncorrect = checkPassword("passwordyangsalah", generatedHash);
        System.out.println("Verifikasi dengan password salah: " + isPasswordIncorrect); // Harusnya false
        
        // Tes dengan hash yang salah (untuk melihat penanganan error jika ada)
        // boolean isHashInvalid = checkPassword(originalPassword, "hashyangrusak");
        // System.out.println("Verifikasi dengan hash rusak: " + isHashInvalid);
    }
}