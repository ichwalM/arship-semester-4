package com.desktopfinance.view;

import com.desktopfinance.model.User;
import com.desktopfinance.model.Transaction; // Pastikan Transaction diimport
import com.desktopfinance.model.TransactionType;
import com.desktopfinance.service.DashboardService;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal; // Pastikan BigDecimal diimport
import java.text.NumberFormat;
import java.util.List; // Pastikan List diimport
import java.util.Locale;
import java.util.Map; // Pastikan Map diimport

public class DashboardPanel extends JPanel {

    private User currentUser;
    private DashboardService dashboardService;

    // Label untuk nilai-nilai utama tetap sebagai member class agar mudah diupdate
    private JLabel lblSaldoValue;
    private JLabel lblTotalPemasukanValue;
    private JLabel lblTotalPengeluaranValue;
    
    private JTable tblTransaksiTerbaru;
    private JScrollPane scrollPaneTransaksi;
    private DefaultTableModel tableModelTransaksi;

    public DashboardPanel(User user) {
        this.currentUser = user;
        this.dashboardService = new DashboardService();
        initComponents();
        loadDashboardData();
    }

    private void initComponents() {
        // Layout utama untuk DashboardPanel: BoxLayout vertikal
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Padding keseluruhan
        setBackground(Color.WHITE); // Latar belakang utama panel dashboard

        // --- Judul Panel Dashboard ---
        JLabel lblPanelTitle = new JLabel("Dashboard");
        lblPanelTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblPanelTitle.setAlignmentX(Component.LEFT_ALIGNMENT); // Rata kiri
        lblPanelTitle.setBorder(new EmptyBorder(0, 0, 15, 0)); // Padding bawah
        add(lblPanelTitle);

        // --- Panel untuk Kartu Ringkasan ---
        JPanel summaryCardsPanel = new JPanel(new GridLayout(1, 3, 15, 15)); // 1 baris, 3 kolom, gap 15px
        summaryCardsPanel.setBackground(Color.WHITE); // Latar belakang panel kartu
        summaryCardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Rata kiri
        summaryCardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); // Batasi tinggi maksimum panel kartu


        // Inisialisasi label nilai (akan dimasukkan ke dalam kartu)
        lblSaldoValue = createValueLabel("Rp 0");
        lblTotalPemasukanValue = createValueLabel("Rp 0", new Color(0, 150, 0)); // Hijau untuk pemasukan
        lblTotalPengeluaranValue = createValueLabel("Rp 0", new Color(200, 0, 0)); // Merah untuk pengeluaran

        // Membuat dan menambahkan kartu
        summaryCardsPanel.add(createSummaryCard("SALDO SAAT INI", lblSaldoValue, new Color(220, 235, 250)));
        summaryCardsPanel.add(createSummaryCard("TOTAL PEMASUKAN (BULAN INI)", lblTotalPemasukanValue, new Color(220, 250, 220)));
        summaryCardsPanel.add(createSummaryCard("TOTAL PENGELUARAN (BULAN INI)", lblTotalPengeluaranValue, new Color(250, 220, 220)));
        
        add(summaryCardsPanel);
        add(Box.createRigidArea(new Dimension(0, 20))); // Spasi setelah kartu

        // --- Panel untuk Transaksi Terbaru ---
        JPanel recentTransactionsPanel = new JPanel(new BorderLayout(5,5));
        recentTransactionsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(10,0,0,0), // Padding atas untuk judul TitledBorder
            "Transaksi Terbaru", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 16) // Font untuk judul TitledBorder
        ));
        recentTransactionsPanel.setBackground(Color.WHITE);
        recentTransactionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Rata kiri
        
        String[] columnNames = {"Tanggal", "Deskripsi", "Tipe", "Kategori/Sumber", "Jumlah"};
        tableModelTransaksi = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblTransaksiTerbaru = new JTable(tableModelTransaksi);
        tblTransaksiTerbaru.setFillsViewportHeight(true);
        tblTransaksiTerbaru.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTransaksiTerbaru.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblTransaksiTerbaru.setRowHeight(25); // Tinggi baris
        tblTransaksiTerbaru.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14)); // Font header tabel
        
        scrollPaneTransaksi = new JScrollPane(tblTransaksiTerbaru);
        recentTransactionsPanel.add(scrollPaneTransaksi, BorderLayout.CENTER);

        add(recentTransactionsPanel);
    }

    // Metode helper untuk membuat panel kartu ringkasan
    private JPanel createSummaryCard(String title, JLabel valueLabel, Color backgroundColor) {
        JPanel cardPanel = new JPanel(new BorderLayout(5,5));
        cardPanel.setBackground(backgroundColor);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1), // Border luar tipis
            new EmptyBorder(15, 15, 15, 15) // Padding dalam kartu
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        lblTitle.setForeground(Color.DARK_GRAY);
        cardPanel.add(lblTitle, BorderLayout.NORTH);
        
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT); // Nilai rata kanan
        cardPanel.add(valueLabel, BorderLayout.CENTER);
        
        return cardPanel;
    }
    
    // Metode helper untuk membuat label nilai di kartu
    private JLabel createValueLabel(String initialText) {
        return createValueLabel(initialText, Color.BLACK);
    }

    private JLabel createValueLabel(String initialText, Color textColor) {
        JLabel label = new JLabel(initialText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(textColor);
        return label;
    }


    public void loadDashboardData() {
        // ... (Logika loadDashboardData Anda yang sudah ada tetap sama) ...
        // Pastikan ia mengupdate lblSaldoValue, lblTotalPemasukanValue, lblTotalPengeluaranValue,
        // dan tableModelTransaksi
        if (currentUser == null) { /* ... */ return; }
        System.out.println("DashboardPanel: Memuat ulang data dashboard untuk pengguna: " + currentUser.getUsername()); 

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        Map<String, BigDecimal> summary = dashboardService.getDashboardSummary(currentUser.getUserId());
        
        BigDecimal totalIncomeThisMonth = summary.getOrDefault("totalIncomeThisMonth", BigDecimal.ZERO);
        BigDecimal totalExpenseThisMonth = summary.getOrDefault("totalExpenseThisMonth", BigDecimal.ZERO);
        BigDecimal currentBalance = summary.getOrDefault("currentBalance", BigDecimal.ZERO);

        lblTotalPemasukanValue.setText(currencyFormatter.format(totalIncomeThisMonth));
        lblTotalPengeluaranValue.setText(currencyFormatter.format(totalExpenseThisMonth));
        lblSaldoValue.setText(currencyFormatter.format(currentBalance));

        List<Transaction> recentTransactions = 
            dashboardService.getRecentTransactionsForDashboard(currentUser.getUserId(), 5); 

        tableModelTransaksi.setRowCount(0); 

        if (recentTransactions != null && !recentTransactions.isEmpty()) {
            for (Transaction trx : recentTransactions) {
                String categoryOrSourceName = ""; 
                if (trx.getType() == TransactionType.EXPENSE && trx.getCategoryName() != null) {
                    categoryOrSourceName = trx.getCategoryName();
                } else if (trx.getType() == TransactionType.INCOME && trx.getSourceName() != null) {
                    categoryOrSourceName = trx.getSourceName();
                }
                Object[] row = {
                    trx.getTransactionDate(), trx.getDescription(),
                    trx.getType().toString(), categoryOrSourceName,
                    currencyFormatter.format(trx.getAmount())
                };
                tableModelTransaksi.addRow(row);
            }
        } else {
            System.out.println("Tidak ada transaksi terbaru untuk ditampilkan.");
        }
    }

    public void refreshDashboardData() {
        System.out.println("DashboardPanel: Menerima perintah refresh...");
        loadDashboardData();
    }
}