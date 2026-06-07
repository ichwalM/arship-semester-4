package com.desktopfinance.view;

import com.desktopfinance.service.AuthService;

import javax.swing.*;
import java.awt.*; // Diperlukan untuk GridBagLayout, GridBagConstraints, Insets, EventQueue
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {

    private JLabel lblTitle; // Tambahan untuk judul
    private JLabel lblNewUsername;
    private JTextField txtNewUsername;
    private JLabel lblNewPassword;
    private JPasswordField txtNewPassword;
    private JLabel lblConfirmPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegister;
    private JButton btnGoToLogin;

    private AuthService authService;

    public RegisterFrame() {
        authService = new AuthService();
        initComponents(); // Panggil metode yang sudah diupdate
        setTitle("Registrasi Akun Baru");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setSize(450, 350); // Ukuran bisa disesuaikan atau biarkan pack()
        setMinimumSize(new Dimension(400, 300)); // Ukuran minimum
        setLocationRelativeTo(null);
        // setResizable(false);
    }

    private void initComponents() {
        // Panel utama dengan GridBagLayout
        JPanel mainRegisterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainRegisterPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Judul
        lblTitle = new JLabel("REGISTRASI AKUN"); // Mirip "Singn Up" di PDF [cite: 1]
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        // Username
        lblNewUsername = new JLabel("Username Baru:");
        txtNewUsername = new JTextField(20);

        // Password
        lblNewPassword = new JLabel("Password Baru:");
        txtNewPassword = new JPasswordField(20);

        // Konfirmasi Password
        lblConfirmPassword = new JLabel("Konfirmasi Password:");
        txtConfirmPassword = new JPasswordField(20);

        // Tombol Registrasi
        btnRegister = new JButton("Registrasi");
        btnRegister.setPreferredSize(new Dimension(120, 30)); // Ukuran tombol

        // Tombol ke Login
        btnGoToLogin = new JButton("Sudah punya akun? Login");
        btnGoToLogin.setFocusPainted(false);
        btnGoToLogin.setBorderPainted(false);
        btnGoToLogin.setContentAreaFilled(false);
        btnGoToLogin.setForeground(Color.BLUE.darker());
        btnGoToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // --- Menambahkan komponen ke mainRegisterPanel ---

        // Baris 0: Judul
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        mainRegisterPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE; // Reset fill

        // Baris 1: Label Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainRegisterPanel.add(lblNewUsername, gbc);

        // Baris 1: Text Field Username
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainRegisterPanel.add(txtNewUsername, gbc);
        gbc.weightx = 0; // Reset weightx
        gbc.fill = GridBagConstraints.NONE; // Reset fill

        // Baris 2: Label Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainRegisterPanel.add(lblNewPassword, gbc);

        // Baris 2: Text Field Password
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainRegisterPanel.add(txtNewPassword, gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        // Baris 3: Label Konfirmasi Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainRegisterPanel.add(lblConfirmPassword, gbc);

        // Baris 3: Text Field Konfirmasi Password
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainRegisterPanel.add(txtConfirmPassword, gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        // Baris 4: Tombol Registrasi
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        mainRegisterPanel.add(btnRegister, gbc);

        // Baris 5: Tombol Login
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainRegisterPanel.add(btnGoToLogin, gbc);

        // Menambahkan mainRegisterPanel ke JFrame
        setLayout(new GridBagLayout());
        GridBagConstraints frameGbc = new GridBagConstraints();
        frameGbc.gridx = 0;
        frameGbc.gridy = 0;
        frameGbc.weightx = 1.0;
        frameGbc.weighty = 1.0;
        add(mainRegisterPanel, frameGbc);

        // --- Action Listener untuk Tombol ---
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        btnGoToLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        
        pack(); // Penting untuk GridBagLayout
    }

    private void performRegistration() {
        String username = txtNewUsername.getText().trim();
        String password = new String(txtNewPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Semua field tidak boleh kosong!",
                    "Error Registrasi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Password dan Konfirmasi Password tidak cocok!",
                    "Error Registrasi",
                    JOptionPane.ERROR_MESSAGE);
            // Mungkin fokuskan kembali ke field password
            txtNewPassword.requestFocus();
            txtNewPassword.setText("");
            txtConfirmPassword.setText("");
            return;
        }

        // Minimal panjang password (contoh)
        if (password.length() < 6) {
             JOptionPane.showMessageDialog(this,
                    "Password minimal harus 6 karakter.",
                    "Error Registrasi",
                    JOptionPane.ERROR_MESSAGE);
            txtNewPassword.requestFocus();
            return;
        }

        boolean registrationSuccessful = authService.registerUser(username, password);

        if (registrationSuccessful) {
            JOptionPane.showMessageDialog(this,
                    "Registrasi berhasil! Silakan login dengan akun baru Anda.",
                    "Registrasi Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new LoginFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Registrasi gagal. Username mungkin sudah digunakan atau terjadi kesalahan lain.",
                    "Error Registrasi",
                    JOptionPane.ERROR_MESSAGE);
            txtNewUsername.requestFocus(); // Fokuskan ke username jika mungkin sudah ada
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new RegisterFrame().setVisible(true);
            }
        });
    }
}