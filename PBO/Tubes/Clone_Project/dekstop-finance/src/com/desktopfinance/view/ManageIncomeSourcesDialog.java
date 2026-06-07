package com.desktopfinance.view;

import com.desktopfinance.model.User;
import com.desktopfinance.model.IncomeSource; // Ganti ke IncomeSource
import com.desktopfinance.service.SourceService; // Ganti ke SourceService

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManageIncomeSourcesDialog extends JDialog {

    private User currentUser;
    private SourceService sourceService; // Menggunakan SourceService
    // private TransactionPanel transactionPanelRef; // Opsional, jika refresh ditangani MainFrame

    private JList<IncomeSource> listSources; // Menggunakan IncomeSource
    private DefaultListModel<IncomeSource> listModelSources;
    private JScrollPane scrollPaneSources;
    private JTextField txtSourceName;
    private JButton btnAddSource;
    private JButton btnEditSource;
    private JButton btnDeleteSource;
    private JButton btnClose;

    // Definisi Font dan Warna (bisa disamakan dengan ManageCategoriesDialog)
    private Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 16);
    private Color primaryButtonColor = new Color(70, 130, 180);
    private Color primaryButtonTextColor = Color.WHITE;
    private Color deleteButtonColor = new Color(220, 53, 69);


    public ManageIncomeSourcesDialog(Frame parent, boolean modal, User user) { // Hapus transactionPanelRef jika MainFrame handle refresh
        super(parent, "Kelola Sumber Pemasukan", modal);
        this.currentUser = user;
        this.sourceService = new SourceService();
        // this.transactionPanelRef = transactionPanelRef;

        initComponents();
        loadSources();

        pack();
        setMinimumSize(new Dimension(500, 420));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(15,15,15,15));
        getContentPane().setBackground(Color.WHITE);

        // --- Panel Input dan Aksi (Bagian Atas) ---
        JPanel topPanel = new JPanel(new BorderLayout(10,10));
        topPanel.setBackground(Color.WHITE);

        JPanel inputNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5,0));
        inputNamePanel.setBackground(Color.WHITE);
        JLabel lblNama = new JLabel("Nama Sumber:"); // Label diubah
        lblNama.setFont(labelFont);
        txtSourceName = new JTextField(25);
        txtSourceName.setFont(fieldFont);
        txtSourceName.setPreferredSize(new Dimension(txtSourceName.getPreferredSize().width, 30));
        
        inputNamePanel.add(lblNama);
        inputNamePanel.add(txtSourceName);
        topPanel.add(inputNamePanel, BorderLayout.NORTH);

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionButtonPanel.setBackground(Color.WHITE);
        btnAddSource = new JButton("Tambah Baru");
        btnEditSource = new JButton("Simpan Perubahan");
        btnDeleteSource = new JButton("Hapus Terpilih");
        
        styleActionButton(btnAddSource, primaryButtonColor);
        styleActionButton(btnEditSource, new Color(255, 193, 7));
        styleActionButton(btnDeleteSource, deleteButtonColor);

        actionButtonPanel.add(btnAddSource);
        actionButtonPanel.add(btnEditSource);
        actionButtonPanel.add(btnDeleteSource);
        topPanel.add(actionButtonPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // --- Panel Daftar Sumber (Bagian Tengah) ---
        listModelSources = new DefaultListModel<>();
        listSources = new JList<>(listModelSources); // Menggunakan IncomeSource
        listSources.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSources.setFont(fieldFont);
        listSources.setFixedCellHeight(28);
        listSources.setBorder(new EmptyBorder(5,5,5,5));
        scrollPaneSources = new JScrollPane(listSources);
        scrollPaneSources.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Daftar Sumber Pemasukan Tersimpan", // Judul diubah
            TitledBorder.LEFT, TitledBorder.TOP, titleFont, Color.DARK_GRAY
        ));
        scrollPaneSources.setPreferredSize(new Dimension(450, 200));
        add(scrollPaneSources, BorderLayout.CENTER);

        // --- Panel Tombol Bawah (Tutup) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10,0,0,0));
        btnClose = new JButton("Tutup");
        styleActionButton(btnClose, Color.GRAY);
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);

        btnEditSource.setEnabled(false);
        btnDeleteSource.setEnabled(false);

        // Action Listeners
        listSources.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    IncomeSource selectedSource = listSources.getSelectedValue(); // Menggunakan IncomeSource
                    if (selectedSource != null) {
                        txtSourceName.setText(selectedSource.getSourceName());
                        boolean isUserOwned = selectedSource.getUserId() != null && selectedSource.getUserId().equals(currentUser.getUserId());
                        btnEditSource.setEnabled(isUserOwned);
                        btnDeleteSource.setEnabled(isUserOwned);
                    } else {
                        txtSourceName.setText("");
                        btnEditSource.setEnabled(false);
                        btnDeleteSource.setEnabled(false);
                    }
                }
            }
        });

        btnAddSource.addActionListener(e -> handleAddSource());
        btnEditSource.addActionListener(e -> handleEditSource());
        btnDeleteSource.addActionListener(e -> handleDeleteSource());
        btnClose.addActionListener(e -> dispose());
    }
    
    private void styleActionButton(JButton button, Color bgColor) {
        button.setFont(buttonFont);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadSources() { // Metode diubah untuk sumber
        listModelSources.clear();
        List<IncomeSource> sources = sourceService.getSourcesForUser(currentUser.getUserId()); // Panggil sourceService
        if (sources != null) {
            for (IncomeSource source : sources) {
                listModelSources.addElement(source);
            }
        }
        listSources.clearSelection();
        txtSourceName.setText("");
        btnEditSource.setEnabled(false);
        btnDeleteSource.setEnabled(false);
    }

    private void handleAddSource() { // Metode diubah untuk sumber
        String sourceName = txtSourceName.getText().trim();
        if (sourceName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama sumber pemasukan tidak boleh kosong.", "Input Error", JOptionPane.ERROR_MESSAGE);
            txtSourceName.requestFocus();
            return;
        }

        boolean success = sourceService.addSource(sourceName, currentUser.getUserId()); // Panggil sourceService
        if (success) {
            JOptionPane.showMessageDialog(this, "Sumber pemasukan baru berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadSources(); 
            txtSourceName.setText(""); 
            // Refresh ComboBox di MainFrame akan menangani ini setelah dialog ditutup
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan sumber pemasukan. Nama mungkin sudah ada atau terjadi error.", "Error Penambahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEditSource() { // Metode diubah untuk sumber
        IncomeSource selectedSource = listSources.getSelectedValue();
        if (selectedSource == null) { /* ... pesan error ... */ return; }

        String newSourceName = txtSourceName.getText().trim();
        if (newSourceName.isEmpty()) { /* ... pesan error ... */ return; }
        if (newSourceName.equals(selectedSource.getSourceName())) { /* ... pesan info ... */ return; }

        boolean success = sourceService.updateSource(selectedSource.getSourceId(), newSourceName, currentUser.getUserId()); // Panggil sourceService
        if (success) {
            JOptionPane.showMessageDialog(this, "Sumber pemasukan berhasil diupdate.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadSources();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate sumber pemasukan. Nama mungkin sudah ada atau terjadi error.", "Error Update", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteSource() { // Metode diubah untuk sumber
        IncomeSource selectedSource = listSources.getSelectedValue();
        if (selectedSource == null) { /* ... pesan error ... */ return; }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus sumber pemasukan '" + selectedSource.getSourceName() + "'?\n" +
                "Transaksi yang menggunakan sumber ini akan kehilangan sumbernya.",
                "Konfirmasi Hapus Sumber", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = sourceService.deleteSource(selectedSource.getSourceId(), currentUser.getUserId()); // Panggil sourceService
            if (success) {
                JOptionPane.showMessageDialog(this, "Sumber pemasukan berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadSources();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus sumber pemasukan. Pastikan sumber ini milik Anda dan tidak terproteksi.", "Error Penghapusan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // Metode notifyTransactionPanelToRefresh() tidak lagi diperlukan di sini jika MainFrame menangani refresh.
}