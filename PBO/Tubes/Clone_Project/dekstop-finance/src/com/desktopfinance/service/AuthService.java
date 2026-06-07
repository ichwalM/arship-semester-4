package com.desktopfinance.service;

import com.desktopfinance.dao.UserDAO;
import com.desktopfinance.model.User;
import com.desktopfinance.util.PasswordUtil; // Ingat, ini masih stub!

import java.sql.SQLException;

public class AuthService {

    private UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO(); // Membuat instance UserDAO
    }

    /**
     * Meregistrasikan pengguna baru.
     *
     * @param username Username yang diinginkan.
     * @param plainPassword Password dalam bentuk plain text.
     * @return true jika registrasi berhasil, false jika username sudah ada atau terjadi error.
     */
    public boolean registerUser(String username, String plainPassword) {
        try {
            // 1. Cek apakah username sudah ada
            if (userDAO.getUserByUsername(username) != null) {
                System.err.println("Registrasi gagal: Username '" + username + "' sudah digunakan.");
                return false; // Username sudah ada
            }

            // 2. Buat objek User baru
            User newUser = new User();
            newUser.setUsername(username);
            // Password akan di-hash oleh UserDAO.addUser() jika kita memodifikasinya
            // atau kita hash di sini sebelum diserahkan ke UserDAO
            // Sesuai implementasi UserDAO.addUser() kita sebelumnya, hashing dilakukan di sana.
            newUser.setPassword(plainPassword); // Kirim plain password, DAO akan hash

            // 3. Simpan user baru melalui DAO
            return userDAO.addUser(newUser);

        } catch (SQLException e) {
            System.err.println("Error saat registrasi: " + e.getMessage());
            // e.printStackTrace(); // Untuk debugging lebih detail
            return false;
        }
    }

    /**
     * Melakukan login pengguna.
     *
     * @param username Username pengguna.
     * @param plainPassword Password dalam bentuk plain text yang diinput pengguna.
     * @return Objek User jika login berhasil, null jika gagal (username tidak ditemukan atau password salah).
     */
    public User loginUser(String username, String plainPassword) {
        try {
            // 1. Ambil user berdasarkan username
            User userFromDB = userDAO.getUserByUsername(username);

            if (userFromDB == null) {
                System.err.println("Login gagal: Username '" + username + "' tidak ditemukan.");
                return null; // Username tidak ditemukan
            }

            // 2. Verifikasi password
            // PasswordUtil.checkPassword() akan membandingkan plainPassword dengan hashedPassword dari DB
            // Ingat, PasswordUtil kita masih stub!
            if (PasswordUtil.checkPassword(plainPassword, userFromDB.getPassword())) {
                // Login berhasil, kembalikan objek User (tanpa plain password)
                // Untuk keamanan, kita bisa set password di objek userFromDB menjadi null sebelum dikembalikan
                // userFromDB.setPassword(null); // Opsional, tergantung kebutuhan
                return userFromDB;
            } else {
                System.err.println("Login gagal: Password salah untuk username '" + username + "'.");
                return null; // Password salah
            }

        } catch (SQLException e) {
            System.err.println("Error saat login: " + e.getMessage());
            // e.printStackTrace();
            return null;
        }
    }
}