package com.desktopfinance.view;

import com.desktopfinance.model.User;
// Pastikan semua kelas panel Anda sudah di-import jika belum
// import com.desktopfinance.view.DashboardPanel;
// import com.desktopfinance.view.TransactionPanel;
// import com.desktopfinance.view.ReportPanel;
// import com.desktopfinance.view.ManageCategoriesDialog;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private User currentUser;

    // Komponen Navigasi Kiri
    private JPanel navigationPanel;
    private JLabel lblAppTitle;
    private JButton btnNavDashboard;
    private JButton btnNavTransaksi;
    private JButton btnNavLaporan;
    private JButton btnNavKelolaKategori; // Tombol baru
    private JButton btnNavLogout;
    
    private List<JButton> navButtons; // Untuk mengelola state tombol navigasi
    private JButton currentSelectedButton; // Tombol navigasi yang sedang dipilih

    // Panel Konten Utama
    private JPanel mainPanelContainer;
    private CardLayout cardLayout;

    // Instance Panel Konten
    DashboardPanel dashboardPanel;
    TransactionPanel transactionPanel;
    ReportPanel reportPanel;

    // Definisi Warna (bisa disesuaikan)
    private Color navBackgroundColor = new Color(235, 238, 242);
    private Color navButtonDefaultColor = navBackgroundColor;
    private Color navButtonHoverColor = new Color(210, 220, 230);
    private Color navButtonSelectedColor = new Color(190, 205, 220);
    private Color navTextColor = Color.DARK_GRAY;
    private Color navTextSelectedColor = Color.BLACK;


    public MainFrame(User user) {
        this.currentUser = user;
        this.navButtons = new ArrayList<>();
        initComponents();
        setTitle("Aplikasi Manajemen Keuangan");
        setSize(950, 720);
        setMinimumSize(new Dimension(850, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }

    private void initComponents() {
        getContentPane().setLayout(new BorderLayout(0, 0));

        // --- Panel Navigasi Kiri ---
        navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setPreferredSize(new Dimension(220, getHeight()));
        navigationPanel.setBackground(navBackgroundColor);
        navigationPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)),
                new EmptyBorder(20, 15, 20, 15) 
        ));

        lblAppTitle = new JLabel("Managemnt Keuangan");
        lblAppTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        lblAppTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAppTitle.setForeground(new Color(50, 50, 50));

        btnNavDashboard = new JButton("Dashboard");
        btnNavTransaksi = new JButton("Transaksi");
        btnNavLaporan = new JButton("Laporan");
        btnNavKelolaKategori = new JButton("Kelola Kategori"); // Tombol baru
        btnNavLogout = new JButton("Log Out");

        navButtons.add(btnNavDashboard);
        navButtons.add(btnNavTransaksi);
        navButtons.add(btnNavLaporan);
        navButtons.add(btnNavKelolaKategori); // Tambahkan ke list untuk styling umum (jika perlu)
        // btnNavLogout tidak perlu state selected yang sama

        Dimension navButtonMinSize = new Dimension(180, 45);
        
        styleNavButton(btnNavDashboard, navButtonMinSize);
        styleNavButton(btnNavTransaksi, navButtonMinSize);
        styleNavButton(btnNavLaporan, navButtonMinSize);
        styleNavButton(btnNavKelolaKategori, navButtonMinSize); // Style tombol baru
        styleNavButton(btnNavLogout, navButtonMinSize); 
        btnNavLogout.setForeground(new Color(200, 0, 0));

        navigationPanel.add(lblAppTitle);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        navigationPanel.add(btnNavDashboard);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        navigationPanel.add(btnNavTransaksi);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        navigationPanel.add(btnNavLaporan);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        navigationPanel.add(btnNavKelolaKategori); // Tambahkan tombol baru
        navigationPanel.add(Box.createVerticalGlue());
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        navigationPanel.add(btnNavLogout);

        // Action Listener untuk Tombol Navigasi
        btnNavDashboard.addActionListener(e -> {
            showPanel("DASHBOARD");
            setSelectedButton(btnNavDashboard);
        });
        btnNavTransaksi.addActionListener(e -> {
            showPanel("TRANSAKSI");
            setSelectedButton(btnNavTransaksi);
        });
        btnNavLaporan.addActionListener(e -> {
            showPanel("LAPORAN");
            setSelectedButton(btnNavLaporan);
        });
        btnNavKelolaKategori.addActionListener(new ActionListener() { // Tombol Kelola Kategori
            @Override
            public void actionPerformed(ActionEvent e) {
                openManageCategoriesDialog();
                setSelectedButton(btnNavKelolaKategori); // Set terpilih setelah dialog ditutup atau langsung
            }
        });
        btnNavLogout.addActionListener(e -> handleLogout());

        getContentPane().add(navigationPanel, BorderLayout.WEST);

        // --- Panel Konten Utama dengan CardLayout ---
        cardLayout = new CardLayout();
        mainPanelContainer = new JPanel(cardLayout);
        mainPanelContainer.setBackground(Color.WHITE);
        mainPanelContainer.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        dashboardPanel = new DashboardPanel(currentUser);
        mainPanelContainer.add(dashboardPanel, "DASHBOARD");
        transactionPanel = new TransactionPanel(currentUser);
        mainPanelContainer.add(transactionPanel, "TRANSAKSI");
        reportPanel = new ReportPanel(currentUser);
        mainPanelContainer.add(reportPanel, "LAPORAN");

        getContentPane().add(mainPanelContainer, BorderLayout.CENTER);

        showPanel("DASHBOARD"); 
        setSelectedButton(btnNavDashboard);
    }
    
    // Di MainFrame.java
    // Di MainFrame.java
    private void openManageIncomeSourcesDialog() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Sesi pengguna tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ManageIncomeSourcesDialog dialog = new ManageIncomeSourcesDialog(this, true, currentUser);
        dialog.setVisible(true); // Ini memblok sampai dialog ditutup

        // Setelah dialog ditutup, refresh ComboBox di panel yang relevan
        System.out.println("ManageIncomeSourcesDialog ditutup, MainFrame me-refresh panel...");
        if (this.transactionPanel != null) {
            this.transactionPanel.loadComboBoxData(); 
            System.out.println("ComboBox sumber di TransactionPanel di-refresh oleh MainFrame.");
        }
        if (this.reportPanel != null) { 
             this.reportPanel.refreshReportData();
             System.out.println("ReportPanel di-refresh oleh MainFrame.");
        }
         if (this.dashboardPanel != null) { 
            this.dashboardPanel.refreshDashboardData();
            System.out.println("DashboardPanel di-refresh oleh MainFrame.");
        }
        // setSelectedButton(btnNavKelolaSumber); // Uncomment jika ingin tombol ini tetap terpilih
    }

    private void styleNavButton(JButton button, Dimension minSize) {
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, minSize.height + 10)); 
        button.setPreferredSize(new Dimension(180, minSize.height));
        button.setMinimumSize(minSize);
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setForeground(navTextColor);
        button.setBackground(navButtonDefaultColor);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (button != currentSelectedButton && button != btnNavLogout) { // Logout tidak ganti hover jika beda style
                    button.setBackground(navButtonHoverColor);
                    button.setForeground(navTextSelectedColor);
                }
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                if (button != currentSelectedButton && button != btnNavLogout) {
                    button.setBackground(navButtonDefaultColor);
                    button.setForeground(navTextColor);
                }
            }
        });
    }

    private void setSelectedButton(JButton selectedButton) {
        for (JButton button : navButtons) { // Hanya tombol di list navButtons yang direset
            button.setBackground(navButtonDefaultColor);
            button.setForeground(navTextColor);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        }
        if (selectedButton != null && navButtons.contains(selectedButton)) { // Pastikan tombol ada di list utama
            selectedButton.setBackground(navButtonSelectedColor);
            selectedButton.setForeground(navTextSelectedColor);
            selectedButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
            currentSelectedButton = selectedButton;
        }
    }

    private void openManageCategoriesDialog() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Sesi pengguna tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Teruskan referensi TransactionPanel. Untuk UpdateTransactionDialog bisa null.
        ManageCategoriesDialog dialog = new ManageCategoriesDialog(this, true, currentUser, this.transactionPanel, null);
        dialog.setVisible(true);

        // Setelah dialog ditutup, refresh ComboBox di panel yang relevan
        System.out.println("ManageCategoriesDialog ditutup, mencoba refresh ComboBoxes dan data...");
        if (this.transactionPanel != null) {
            this.transactionPanel.loadComboBoxData(); 
            System.out.println("ComboBox kategori di TransactionPanel di-refresh.");
        }
        if (this.reportPanel != null) { 
             this.reportPanel.refreshReportData(); // Refresh data laporan (termasuk nama kategori)
             System.out.println("ReportPanel di-refresh.");
        }
         if (this.dashboardPanel != null) { // Jika dashboard menampilkan ringkasan per kategori
             this.dashboardPanel.refreshDashboardData();
             System.out.println("DashboardPanel di-refresh.");
        }
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanelContainer, panelName);
        System.out.println("Menampilkan panel: " + panelName);
        String displayName = panelName.substring(0,1).toUpperCase() + panelName.substring(1).toLowerCase();
        setTitle("Aplikasi Manajemen Keuangan - " + displayName + " (" + currentUser.getUsername() + ")");
        
        // Update selected button state jika panelName cocok dengan tombol navigasi
        switch (panelName) {
            case "DASHBOARD": setSelectedButton(btnNavDashboard); break;
            case "TRANSAKSI": setSelectedButton(btnNavTransaksi); break;
            case "LAPORAN": setSelectedButton(btnNavLaporan); break;
            // default: setSelectedButton(null); // Atau biarkan tombol Kelola Kategori tetap terpilih jika itu yang terakhir diklik
        }
    }

    private void handleLogout() {
        int response = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin logout?", "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            this.dispose(); 
            new LoginFrame().setVisible(true); 
        }
    }

    private void handleExit() { 
        int response = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin keluar dari aplikasi?", "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            System.exit(0); 
        }
    }   
    
    public void refreshDashboard() {
         if (dashboardPanel != null) {
            System.out.println("MainFrame: Mengirim perintah refresh ke DashboardPanel...");
            dashboardPanel.refreshDashboardData();
        } else {
            System.err.println("MainFrame: dashboardPanel belum diinisialisasi, tidak bisa refresh.");
        }
    }

    public void refreshReportPanel() {
        if (reportPanel != null) {
            System.out.println("MainFrame: Mengirim perintah refresh ke ReportPanel...");
            reportPanel.refreshReportData();
        } else {
            System.err.println("MainFrame: reportPanel belum diinisialisasi, tidak bisa refresh.");
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            User dummyUser = new User("testuser", "password"); 
            dummyUser.setUserId(1);
            new MainFrame(dummyUser).setVisible(true);
        });
    }
}