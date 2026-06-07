package com.desktopfinance.view;

import com.desktopfinance.model.User;
import com.desktopfinance.model.Transaction;
import com.desktopfinance.model.TransactionType;
import com.desktopfinance.service.TransactionService;
import com.desktopfinance.service.DashboardService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportPanel extends JPanel {

    private User currentUser;
    private TransactionService transactionService;
    private DashboardService dashboardService;

    private JTable tblAllTransactions;
    private JScrollPane scrollPaneTransactions;
    private DefaultTableModel tableModelTransactions;
    private JButton btnDeleteTransaction;
    private JButton btnUpdateTransaction;
    private List<Transaction> displayedTransactionsList;

    // Komponen untuk Laporan Periodik
    private JPanel panelPeriodicReport;
    private JLabel lblPilihTahun, lblPilihBulan;
    private JComboBox<Integer> cmbTahun;
    private JComboBox<String> cmbBulan;
    private JButton btnTampilkanLaporanBulanan;
    private JButton btnTampilkanLaporanTahunan;
    private JLabel lblHasilLaporanTitle;
    private JLabel lblHasilTotalPemasukan;
    private JLabel lblHasilTotalPengeluaran;

    // Komponen untuk Fitur Restore dan Opsi Tampilan
    private JCheckBox chkShowDeleted;
    private JButton btnRestoreTransaction;

    // Definisi Font dan Warna (bisa Anda sesuaikan)
    private Font panelTitleFont = new Font("Segoe UI", Font.BOLD, 24);
    private Font sectionTitleFont = new Font("Segoe UI", Font.BOLD, 16);
    private Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font resultLabelFont = new Font("Segoe UI", Font.BOLD, 14);
    private Color reportValueColor = new Color(0, 100, 0); // Hijau tua
    private Color reportExpenseColor = new Color(180, 0, 0); // Merah tua


    public ReportPanel(User user) {
        this.currentUser = user;
        this.transactionService = new TransactionService();
        this.dashboardService = new DashboardService();
        this.displayedTransactionsList = new ArrayList<>();
        initComponents();
        loadAllTransactions();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        setBackground(Color.WHITE);

        // --- Judul Panel Utama ---
        JLabel lblPanelTitle = new JLabel("Laporan Transaksi");
        lblPanelTitle.setFont(panelTitleFont);
        lblPanelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblPanelTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(lblPanelTitle, BorderLayout.NORTH);

        // --- Panel Konten Tengah (Laporan Periodik dan Tabel Transaksi) ---
        JPanel centerContentPanel = new JPanel();
        centerContentPanel.setLayout(new BoxLayout(centerContentPanel, BoxLayout.Y_AXIS));
        centerContentPanel.setBackground(Color.WHITE);

        // --- Panel untuk Laporan Periodik ---
        panelPeriodicReport = new JPanel(new GridBagLayout());
        panelPeriodicReport.setBackground(new Color(245, 248, 250));
        panelPeriodicReport.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Laporan Periodik",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                sectionTitleFont, Color.DARK_GRAY
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbcReport = new GridBagConstraints();
        gbcReport.insets = new Insets(8, 8, 8, 8);
        gbcReport.anchor = GridBagConstraints.WEST;

        lblPilihTahun = new JLabel("Pilih Tahun:");
        lblPilihTahun.setFont(labelFont);
        cmbTahun = new JComboBox<>();
        cmbTahun.setFont(labelFont);
        cmbTahun.setPreferredSize(new Dimension(120, 28));
        int tahunSekarang = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = tahunSekarang; i >= tahunSekarang - 5; i--) {
            cmbTahun.addItem(i);
        }

        lblPilihBulan = new JLabel("Pilih Bulan:");
        lblPilihBulan.setFont(labelFont);
        cmbBulan = new JComboBox<>(new String[]{
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        });
        cmbBulan.setFont(labelFont);
        cmbBulan.setPreferredSize(new Dimension(150, 28));

        btnTampilkanLaporanBulanan = new JButton("Laporan Bulanan");
        btnTampilkanLaporanBulanan.setFont(labelFont);
        btnTampilkanLaporanTahunan = new JButton("Laporan Tahunan");
        btnTampilkanLaporanTahunan.setFont(labelFont);

        lblHasilLaporanTitle = new JLabel("Hasil Laporan:");
        lblHasilLaporanTitle.setFont(sectionTitleFont);
        lblHasilLaporanTitle.setBorder(new EmptyBorder(10,0,5,0));

        lblHasilTotalPemasukan = new JLabel("Total Pemasukan: Rp 0");
        lblHasilTotalPemasukan.setFont(resultLabelFont);
        lblHasilTotalPemasukan.setForeground(reportValueColor);

        lblHasilTotalPengeluaran = new JLabel("Total Pengeluaran: Rp 0");
        lblHasilTotalPengeluaran.setFont(resultLabelFont);
        lblHasilTotalPengeluaran.setForeground(reportExpenseColor);

        gbcReport.gridx = 0; gbcReport.gridy = 0; panelPeriodicReport.add(lblPilihTahun, gbcReport);
        gbcReport.gridx = 1; gbcReport.gridy = 0; gbcReport.fill = GridBagConstraints.HORIZONTAL; panelPeriodicReport.add(cmbTahun, gbcReport);
        gbcReport.gridx = 0; gbcReport.gridy = 1; gbcReport.fill = GridBagConstraints.NONE; panelPeriodicReport.add(lblPilihBulan, gbcReport);
        gbcReport.gridx = 1; gbcReport.gridy = 1; gbcReport.fill = GridBagConstraints.HORIZONTAL; panelPeriodicReport.add(cmbBulan, gbcReport);
        
        JPanel reportButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        reportButtonPanel.setBackground(panelPeriodicReport.getBackground());
        reportButtonPanel.add(btnTampilkanLaporanBulanan);
        reportButtonPanel.add(Box.createRigidArea(new Dimension(10,0)));
        reportButtonPanel.add(btnTampilkanLaporanTahunan);
        gbcReport.gridx = 0; gbcReport.gridy = 2; gbcReport.gridwidth = 2; gbcReport.anchor = GridBagConstraints.CENTER; panelPeriodicReport.add(reportButtonPanel, gbcReport);

        gbcReport.gridx = 0; gbcReport.gridy = 3; gbcReport.gridwidth = 2; gbcReport.anchor = GridBagConstraints.WEST; panelPeriodicReport.add(lblHasilLaporanTitle, gbcReport);
        gbcReport.gridx = 0; gbcReport.gridy = 4; gbcReport.gridwidth = 2; panelPeriodicReport.add(lblHasilTotalPemasukan, gbcReport);
        gbcReport.gridx = 0; gbcReport.gridy = 5; gbcReport.gridwidth = 2; panelPeriodicReport.add(lblHasilTotalPengeluaran, gbcReport);

        centerContentPanel.add(panelPeriodicReport);
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- Setup Tabel untuk Semua Transaksi ---
        String[] columnNames = {"ID", "Tanggal", "Deskripsi", "Tipe", "Kategori/Sumber", "Jumlah"};
        tableModelTransactions = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblAllTransactions = new JTable(tableModelTransactions);
        tblAllTransactions.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblAllTransactions.setRowHeight(25);
        tblAllTransactions.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblAllTransactions.setFillsViewportHeight(true);
        tblAllTransactions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPaneTransactions = new JScrollPane(tblAllTransactions);
        centerContentPanel.add(scrollPaneTransactions);

        add(centerContentPanel, BorderLayout.CENTER);

        // --- Panel Kontrol Bawah ---
        JPanel bottomControlsPanel = new JPanel(new BorderLayout(10,5)); 
        bottomControlsPanel.setBorder(new EmptyBorder(10,0,0,0));
        bottomControlsPanel.setBackground(Color.WHITE);
        
        JPanel transactionActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5,0));
        transactionActionPanel.setBackground(bottomControlsPanel.getBackground());
        btnUpdateTransaction = new JButton("Update Terpilih");
        btnDeleteTransaction = new JButton("Hapus Terpilih");
        btnRestoreTransaction = new JButton("Restore Terpilih");
        styleActionButton(btnUpdateTransaction);
        styleActionButton(btnDeleteTransaction);
        styleActionButton(btnRestoreTransaction);
        btnRestoreTransaction.setEnabled(false); 

        transactionActionPanel.add(btnUpdateTransaction);
        transactionActionPanel.add(btnDeleteTransaction);
        transactionActionPanel.add(btnRestoreTransaction);

        JPanel viewOptionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5,0));
        viewOptionsPanel.setBackground(bottomControlsPanel.getBackground());
        chkShowDeleted = new JCheckBox("Tampilkan Transaksi Terhapus");
        chkShowDeleted.setFont(labelFont);
        chkShowDeleted.setBackground(bottomControlsPanel.getBackground());
        viewOptionsPanel.add(chkShowDeleted);

        bottomControlsPanel.add(transactionActionPanel, BorderLayout.WEST);
        bottomControlsPanel.add(viewOptionsPanel, BorderLayout.EAST);
        
        add(bottomControlsPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        btnDeleteTransaction.addActionListener(e -> handleDeleteTransaction());
        btnUpdateTransaction.addActionListener(e -> handleUpdateTransaction());
        btnTampilkanLaporanBulanan.addActionListener(e -> showLaporanBulanan());
        btnTampilkanLaporanTahunan.addActionListener(e -> showLaporanTahunan());
        btnRestoreTransaction.addActionListener(e -> handleRestoreTransaction());
        chkShowDeleted.addActionListener(e -> {
            loadAllTransactions(); 
            boolean isDeletedSelected = chkShowDeleted.isSelected();
            btnDeleteTransaction.setEnabled(!isDeletedSelected);
            btnUpdateTransaction.setEnabled(!isDeletedSelected);
            btnRestoreTransaction.setEnabled(isDeletedSelected);
        });
    }
    
    private void styleActionButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(160, 30)); // Ukuran tombol aksi
        // Anda bisa tambahkan styling lain jika perlu
    }

    public void loadAllTransactions() {
        if (currentUser == null) { /* ... error handling ... */ return; }
        
        boolean showDeleted = chkShowDeleted.isSelected();
        System.out.println("ReportPanel: Memuat transaksi (showDeleted=" + showDeleted + ") untuk pengguna: " + currentUser.getUsername());

        if (showDeleted) {
            this.displayedTransactionsList = transactionService.getSoftDeletedTransactionsForUser(currentUser.getUserId());
        } else {
            this.displayedTransactionsList = transactionService.getAllTransactionsForUser(currentUser.getUserId());
        }

        tableModelTransactions.setRowCount(0); 
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        if (displayedTransactionsList != null && !displayedTransactionsList.isEmpty()) {
            for (Transaction trx : displayedTransactionsList) {
                String categoryOrSourceName = ""; 
                if (trx.getType() == TransactionType.EXPENSE && trx.getCategoryName() != null) {
                    categoryOrSourceName = trx.getCategoryName();
                } else if (trx.getType() == TransactionType.INCOME && trx.getSourceName() != null) {
                    categoryOrSourceName = trx.getSourceName();
                }
                Object[] row = {
                    trx.getTransactionId(), trx.getTransactionDate(), trx.getDescription(),
                    trx.getType().toString(), categoryOrSourceName,
                    currencyFormatter.format(trx.getAmount())
                };
                tableModelTransactions.addRow(row);
            }
        } else {
             System.out.println("ReportPanel: Tidak ada transaksi (" + (showDeleted ? "terhapus" : "aktif") + ") untuk ditampilkan.");
        }
    }

    private void handleDeleteTransaction() {
        int selectedRow = tblAllTransactions.getSelectedRow();
        if (selectedRow == -1) { /* ... error handling ... */ return; }
        int transactionId = (int) tableModelTransactions.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus transaksi ini (ID: " + transactionId + ")?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = transactionService.softDeleteTransaction(transactionId, currentUser.getUserId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshReportData(); 
                refreshDashboard(); 
            } else { /* ... error handling ... */ }
        }
    }
    
    private void handleUpdateTransaction() {
        int selectedRow = tblAllTransactions.getSelectedRow();
        if (selectedRow == -1) { /* ... error handling ... */ return; }
        if (displayedTransactionsList == null || selectedRow >= displayedTransactionsList.size()) { /* ... error handling ... */ return; }
        Transaction transactionToEdit = displayedTransactionsList.get(selectedRow);
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        UpdateTransactionDialog updateDialog = new UpdateTransactionDialog(
                parentFrame, true, currentUser, transactionToEdit, transactionService, this);
        updateDialog.setVisible(true);
    }

    private void handleRestoreTransaction() {
        int selectedRow = tblAllTransactions.getSelectedRow();
        if (selectedRow == -1) { /* ... error handling ... */ return; }
        if (!chkShowDeleted.isSelected()) { /* ... error handling ... */ return; }
        int transactionId = (int) tableModelTransactions.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin me-restore transaksi ini (ID: " + transactionId + ")?",
                "Konfirmasi Restore", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = transactionService.restoreTransaction(transactionId, currentUser.getUserId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Transaksi berhasil di-restore.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshReportData();
                refreshDashboard();   
            } else { /* ... error handling ... */ }
        }
    }
    
    private void showLaporanBulanan() {
        if (currentUser == null) { /* ... */ return; }
        Integer selectedYear = (Integer) cmbTahun.getSelectedItem();
        int selectedMonthIndex = cmbBulan.getSelectedIndex(); 
        int monthForService = selectedMonthIndex + 1;
        if (selectedYear == null) { /* ... */ return; }
        
        Map<String, BigDecimal> reportData = dashboardService.getMonthlyReport(currentUser.getUserId(), selectedYear, monthForService);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        BigDecimal totalPemasukan = reportData.getOrDefault("totalIncome", BigDecimal.ZERO);
        BigDecimal totalPengeluaran = reportData.getOrDefault("totalExpense", BigDecimal.ZERO);
        
        lblHasilLaporanTitle.setText("Hasil Laporan Bulanan (" + cmbBulan.getSelectedItem() + " " + selectedYear + "):");
        lblHasilTotalPemasukan.setText("Total Pemasukan: " + currencyFormatter.format(totalPemasukan));
        lblHasilTotalPengeluaran.setText("Total Pengeluaran: " + currencyFormatter.format(totalPengeluaran));
    }

    private void showLaporanTahunan() {
        if (currentUser == null) { /* ... */ return; }
        Integer selectedYear = (Integer) cmbTahun.getSelectedItem();
        if (selectedYear == null) { /* ... */ return; }

        Map<String, BigDecimal> reportData = dashboardService.getAnnualReport(currentUser.getUserId(), selectedYear);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        BigDecimal totalPemasukan = reportData.getOrDefault("totalIncome", BigDecimal.ZERO);
        BigDecimal totalPengeluaran = reportData.getOrDefault("totalExpense", BigDecimal.ZERO);

        lblHasilLaporanTitle.setText("Hasil Laporan Tahunan (" + selectedYear + "):");
        lblHasilTotalPemasukan.setText("Total Pemasukan: " + currencyFormatter.format(totalPemasukan));
        lblHasilTotalPengeluaran.setText("Total Pengeluaran: " + currencyFormatter.format(totalPengeluaran));
    }

    public void refreshReportData() {
        System.out.println("ReportPanel: Menerima perintah refresh data laporan...");
        loadAllTransactions();
        lblHasilLaporanTitle.setText("Hasil Laporan:");
        lblHasilTotalPemasukan.setText("Total Pemasukan: Rp 0");
        lblHasilTotalPengeluaran.setText("Total Pengeluaran: Rp 0");
    }

    private void refreshDashboard() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof MainFrame) {
            ((MainFrame) parentWindow).refreshDashboard();
        }
    }
}