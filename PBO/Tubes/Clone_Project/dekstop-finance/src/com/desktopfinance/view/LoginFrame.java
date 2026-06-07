package com.desktopfinance.view;

import com.desktopfinance.service.AuthService;
import com.desktopfinance.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JLabel lblTitle; // Tambahan untuk judul
    private JLabel lblUsername;
    private JTextField txtUsername;
    private JLabel lblPassword;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnGoToRegister;

    private AuthService authService;

    public LoginFrame() {
        authService = new AuthService();
        initComponents(); // Panggil metode yang sudah diupdate
        setTitle("Login Aplikasi Keuangan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setSize(400, 300); // Ukuran bisa disesuaikan atau biarkan pack() yang mengatur
        setMinimumSize(new Dimension(350, 250)); // Ukuran minimum agar tidak terlalu kecil
        setLocationRelativeTo(null);
        // setResizable(false); // Bisa diatur true jika ingin bisa di-resize
    }

    private void initComponents() {
        // Menggunakan panel utama dengan GridBagLayout untuk menampung semua komponen
        JPanel mainLoginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainLoginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding untuk panel

        // Judul
        lblTitle = new JLabel("USER LOGIN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Contoh pengaturan font

        // Username
        lblUsername = new JLabel("Username:");
        txtUsername = new JTextField(20); // Lebar field

        // Password
        lblPassword = new JLabel("Password:");
        txtPassword = new JPasswordField(20);

        // Tombol Login
        btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(100, 30)); // Ukuran tombol

        // Tombol ke Registrasi
        btnGoToRegister = new JButton("Belum punya akun? Registrasi");
        // Membuat tampilan tombol registrasi seperti link (opsional)
        btnGoToRegister.setFocusPainted(false);
        btnGoToRegister.setBorderPainted(false);
        btnGoToRegister.setContentAreaFilled(false);
        btnGoToRegister.setForeground(Color.BLUE.darker());
        btnGoToRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));


        // --- Menambahkan komponen ke mainLoginPanel menggunakan GridBagConstraints ---

        // Baris 0: Judul
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Rentang 2 kolom
        gbc.insets = new Insets(0, 0, 20, 0); // Padding bawah untuk judul
        gbc.anchor = GridBagConstraints.CENTER; // Tengah
        mainLoginPanel.add(lblTitle, gbc);

        // Reset gridwidth dan anchor untuk komponen berikutnya
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST; // Rata kiri untuk label

        // Baris 1: Label Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5); // Padding standar
        mainLoginPanel.add(lblUsername, gbc);

        // Baris 1: Text Field Username
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Agar field mengisi lebar
        gbc.weightx = 1.0; // Beri bobot agar bisa melebar
        mainLoginPanel.add(txtUsername, gbc);

        // Reset fill dan weightx
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        // Baris 2: Label Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainLoginPanel.add(lblPassword, gbc);

        // Baris 2: Text Field Password
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainLoginPanel.add(txtPassword, gbc);
        
        // Reset fill dan weightx
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        // Baris 3: Tombol Login
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Rentang 2 kolom
        gbc.anchor = GridBagConstraints.CENTER; // Tengah
        gbc.insets = new Insets(15, 5, 5, 5); // Padding atas lebih besar
        mainLoginPanel.add(btnLogin, gbc);

        // Baris 4: Tombol Registrasi
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5); // Padding standar
        mainLoginPanel.add(btnGoToRegister, gbc);

        // Menambahkan mainLoginPanel ke content pane dari JFrame
        // Kita gunakan BorderLayout untuk JFrame agar bisa mengatur panel utama di tengah jika perlu
        // Namun, karena mainLoginPanel sudah menggunakan GridBagLayout yang bisa mengatur alignment,
        // kita bisa langsung add mainLoginPanel.
        // Untuk memastikan panelnya tidak memenuhi seluruh frame jika frame di-resize besar,
        // kita bisa bungkus mainLoginPanel dalam panel lain yang menggunakan GridBagLayout juga.
        
        setLayout(new GridBagLayout()); // Set layout JFrame ke GridBagLayout
        GridBagConstraints frameGbc = new GridBagConstraints();
        frameGbc.gridx = 0;
        frameGbc.gridy = 0;
        frameGbc.weightx = 1.0; // Agar panel bisa di tengah jika ada ruang ekstra
        frameGbc.weighty = 1.0; // Agar panel bisa di tengah jika ada ruang ekstra
        // frameGbc.anchor = GridBagConstraints.CENTER; // Tidak perlu jika panel sudah pas
        add(mainLoginPanel, frameGbc); // Tambahkan panel utama ke JFrame


        // --- Action Listener untuk Tombol ---
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        btnGoToRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterFrame().setVisible(true);
            }
        });
        
        pack(); // Sangat penting setelah menggunakan GridBagLayout agar ukuran frame pas
    }

    private void performLogin() {
        String username = txtUsername.getText().trim(); // Tambahkan trim()
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username dan Password tidak boleh kosong!",
                    "Error Login",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = authService.loginUser(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Login berhasil! Selamat datang " + user.getUsername(),
                    "Login Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new MainFrame(user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Login gagal. Username atau Password salah.",
                    "Error Login",
                    JOptionPane.ERROR_MESSAGE);
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
                new LoginFrame().setVisible(true);
            }
        });
    }
}