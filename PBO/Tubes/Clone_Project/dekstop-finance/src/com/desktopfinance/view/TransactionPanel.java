package com.desktopfinance.view;

import com.desktopfinance.model.User;
import com.desktopfinance.model.Transaction;
import com.desktopfinance.model.TransactionType;
import com.desktopfinance.model.ExpenseCategory;
import com.desktopfinance.model.IncomeSource;
import com.desktopfinance.service.TransactionService;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionPanel extends JPanel {

    private User currentUser;
    private TransactionService transactionService;

    // Komponen UI
    private JTextField txtTanggalPemasukan, txtNominalPemasukan;
    private JComboBox<IncomeSource> cmbSumberPemasukan;
    private JButton btnSimpanPemasukan;

    private JTextField txtTanggalPengeluaran, txtJumlahPengeluaran, txtDeskripsiPengeluaran;
    private JComboBox<ExpenseCategory> cmbKategoriPengeluaran;
    private JButton btnSimpanPengeluaran;

    private JTabbedPane tabbedPane;
    private NumberFormat amountFormatter;

    // Definisi Font dan Warna
    private Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 16);
    private Color primaryButtonColor = new Color(70, 130, 180); // SteelBlue
    private Color primaryButtonTextColor = Color.WHITE;
    private Color panelBackgroundColor = new Color(245, 248, 250);


    public TransactionPanel(User user) {
        this.currentUser = user;
        this.transactionService = new TransactionService();

        this.amountFormatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        this.amountFormatter.setMaximumFractionDigits(2);
        this.amountFormatter.setGroupingUsed(false);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        setBackground(Color.WHITE);

        initComponents();
        // loadComboBoxData() dipanggil di akhir initComponents()
    }

    private void initComponents() {
        JLabel lblPanelTitle = new JLabel("Input Transaksi Baru");
        lblPanelTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblPanelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblPanelTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(lblPanelTitle, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        Dimension fieldPreferredSize = new Dimension(280, 30);

        // --- Membuat Form Pemasukan ---
        JPanel panelPemasukan = createFormPanel("Detail Pemasukan");
        GridBagConstraints gbcPemasukan = new GridBagConstraints();
        gbcPemasukan.insets = new Insets(10, 10, 10, 10);
        gbcPemasukan.anchor = GridBagConstraints.WEST;

        JLabel lblTanggalPemasukan = new JLabel("Tanggal (YYYY-MM-DD):");
        lblTanggalPemasukan.setFont(labelFont);
        txtTanggalPemasukan = new JTextField();
        txtTanggalPemasukan.setFont(fieldFont);
        txtTanggalPemasukan.setPreferredSize(fieldPreferredSize);

        JLabel lblNominalPemasukan = new JLabel("Nominal:");
        lblNominalPemasukan.setFont(labelFont);
        txtNominalPemasukan = new JTextField();
        txtNominalPemasukan.setFont(fieldFont);
        txtNominalPemasukan.setPreferredSize(fieldPreferredSize);

        JLabel lblSumberPemasukan = new JLabel("Sumber Pemasukan:");
        lblSumberPemasukan.setFont(labelFont);
        cmbSumberPemasukan = new JComboBox<>();
        cmbSumberPemasukan.setFont(fieldFont);
        cmbSumberPemasukan.setPreferredSize(fieldPreferredSize);

        btnSimpanPemasukan = new JButton("Simpan Pemasukan");
        stylePrimaryButton(btnSimpanPemasukan);

        gbcPemasukan.gridx = 0; gbcPemasukan.gridy = 0; panelPemasukan.add(lblTanggalPemasukan, gbcPemasukan);
        gbcPemasukan.gridx = 1; gbcPemasukan.gridy = 0; gbcPemasukan.fill = GridBagConstraints.HORIZONTAL; gbcPemasukan.weightx = 1.0; panelPemasukan.add(txtTanggalPemasukan, gbcPemasukan);
        gbcPemasukan.gridx = 0; gbcPemasukan.gridy = 1; gbcPemasukan.fill = GridBagConstraints.NONE; gbcPemasukan.weightx = 0; panelPemasukan.add(lblNominalPemasukan, gbcPemasukan);
        gbcPemasukan.gridx = 1; gbcPemasukan.gridy = 1; gbcPemasukan.fill = GridBagConstraints.HORIZONTAL; gbcPemasukan.weightx = 1.0; panelPemasukan.add(txtNominalPemasukan, gbcPemasukan);
        gbcPemasukan.gridx = 0; gbcPemasukan.gridy = 2; gbcPemasukan.fill = GridBagConstraints.NONE; gbcPemasukan.weightx = 0; panelPemasukan.add(lblSumberPemasukan, gbcPemasukan);
        gbcPemasukan.gridx = 1; gbcPemasukan.gridy = 2; gbcPemasukan.fill = GridBagConstraints.HORIZONTAL; gbcPemasukan.weightx = 1.0; panelPemasukan.add(cmbSumberPemasukan, gbcPemasukan);
        gbcPemasukan.gridx = 1; gbcPemasukan.gridy = 3; gbcPemasukan.fill = GridBagConstraints.NONE; gbcPemasukan.anchor = GridBagConstraints.EAST; gbcPemasukan.weightx = 0; gbcPemasukan.insets = new Insets(20, 5, 5, 0); panelPemasukan.add(btnSimpanPemasukan, gbcPemasukan);
        
        tabbedPane.addTab("  Pemasukan  ", panelPemasukan);

        // --- Membuat Form Pengeluaran ---
        JPanel panelPengeluaran = createFormPanel("Detail Pengeluaran");
        GridBagConstraints gbcPengeluaran = new GridBagConstraints();
        gbcPengeluaran.insets = new Insets(10, 10, 10, 10);
        gbcPengeluaran.anchor = GridBagConstraints.WEST;

        JLabel lblTanggalPengeluaran = new JLabel("Tanggal (YYYY-MM-DD):");
        lblTanggalPengeluaran.setFont(labelFont);
        txtTanggalPengeluaran = new JTextField();
        txtTanggalPengeluaran.setFont(fieldFont);
        txtTanggalPengeluaran.setPreferredSize(fieldPreferredSize);

        JLabel lblJumlahPengeluaran = new JLabel("Jumlah:");
        lblJumlahPengeluaran.setFont(labelFont);
        txtJumlahPengeluaran = new JTextField();
        txtJumlahPengeluaran.setFont(fieldFont);
        txtJumlahPengeluaran.setPreferredSize(fieldPreferredSize);

        JLabel lblDeskripsiPengeluaran = new JLabel("Deskripsi:");
        lblDeskripsiPengeluaran.setFont(labelFont);
        txtDeskripsiPengeluaran = new JTextField();
        txtDeskripsiPengeluaran.setFont(fieldFont);
        txtDeskripsiPengeluaran.setPreferredSize(fieldPreferredSize);

        JLabel lblKategoriPengeluaran = new JLabel("Kategori Pengeluaran:");
        lblKategoriPengeluaran.setFont(labelFont);
        cmbKategoriPengeluaran = new JComboBox<>();
        cmbKategoriPengeluaran.setFont(fieldFont);
        cmbKategoriPengeluaran.setPreferredSize(fieldPreferredSize);

        btnSimpanPengeluaran = new JButton("Simpan Pengeluaran");
        stylePrimaryButton(btnSimpanPengeluaran);
        
        gbcPengeluaran.gridx = 0; gbcPengeluaran.gridy = 0; panelPengeluaran.add(lblTanggalPengeluaran, gbcPengeluaran);
        gbcPengeluaran.gridx = 1; gbcPengeluaran.gridy = 0; gbcPengeluaran.fill = GridBagConstraints.HORIZONTAL; gbcPengeluaran.weightx = 1.0; panelPengeluaran.add(txtTanggalPengeluaran, gbcPengeluaran);
        gbcPengeluaran.gridx = 0; gbcPengeluaran.gridy = 1; gbcPengeluaran.fill = GridBagConstraints.NONE; gbcPengeluaran.weightx = 0; panelPengeluaran.add(lblJumlahPengeluaran, gbcPengeluaran);
        gbcPengeluaran.gridx = 1; gbcPengeluaran.gridy = 1; gbcPengeluaran.fill = GridBagConstraints.HORIZONTAL; gbcPengeluaran.weightx = 1.0; panelPengeluaran.add(txtJumlahPengeluaran, gbcPengeluaran);
        gbcPengeluaran.gridx = 0; gbcPengeluaran.gridy = 2; gbcPengeluaran.fill = GridBagConstraints.NONE; gbcPengeluaran.weightx = 0; panelPengeluaran.add(lblDeskripsiPengeluaran, gbcPengeluaran);
        gbcPengeluaran.gridx = 1; gbcPengeluaran.gridy = 2; gbcPengeluaran.fill = GridBagConstraints.HORIZONTAL; gbcPengeluaran.weightx = 1.0; panelPengeluaran.add(txtDeskripsiPengeluaran, gbcPengeluaran);
        gbcPengeluaran.gridx = 0; gbcPengeluaran.gridy = 3; gbcPengeluaran.fill = GridBagConstraints.NONE; gbcPengeluaran.weightx = 0; panelPengeluaran.add(lblKategoriPengeluaran, gbcPengeluaran);
        gbcPengeluaran.gridx = 1; gbcPengeluaran.gridy = 3; gbcPengeluaran.fill = GridBagConstraints.HORIZONTAL; gbcPengeluaran.weightx = 1.0; panelPengeluaran.add(cmbKategoriPengeluaran, gbcPengeluaran);
        gbcPengeluaran.gridx = 1; gbcPengeluaran.gridy = 4; gbcPengeluaran.fill = GridBagConstraints.NONE; gbcPengeluaran.anchor = GridBagConstraints.EAST; gbcPengeluaran.weightx = 0; gbcPengeluaran.insets = new Insets(20, 5, 5, 0); panelPengeluaran.add(btnSimpanPengeluaran, gbcPengeluaran);

        tabbedPane.addTab("  Pengeluaran  ", panelPengeluaran);

        add(tabbedPane, BorderLayout.CENTER);

        loadComboBoxData(); 

        btnSimpanPemasukan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePemasukan();
            }
        });
        btnSimpanPengeluaran.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePengeluaran();
            }
        });
    }

    private JPanel createFormPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelBackgroundColor);
        Border padding = new EmptyBorder(20, 20, 20, 20); // Padding luar lebih besar
        Border titled = BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            titleFont,
            Color.DARK_GRAY
        );
        panel.setBorder(BorderFactory.createCompoundBorder(padding, titled));
        return panel;
    }
    
    private void stylePrimaryButton(JButton button) {
        button.setFont(buttonFont);
        button.setBackground(primaryButtonColor);
        button.setForeground(primaryButtonTextColor);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 38));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void loadComboBoxData() {
        System.out.println("TransactionPanel: Memulai loadComboBoxData().");
        cmbSumberPemasukan.removeAllItems();
        cmbKategoriPengeluaran.removeAllItems();
        System.out.println("TransactionPanel: ComboBoxes dikosongkan.");

        if (currentUser != null && transactionService != null) {
            System.out.println("TransactionPanel: Mengambil sumber pemasukan untuk user ID: " + currentUser.getUserId());
            List<IncomeSource> sources = transactionService.getActiveIncomeSources(currentUser.getUserId());
            if (sources != null && !sources.isEmpty()) {
                System.out.println("TransactionPanel: Ditemukan " + sources.size() + " sumber pemasukan.");
                for (IncomeSource source : sources) {
                    cmbSumberPemasukan.addItem(source);
                }
            } else {
                System.out.println("TransactionPanel: Tidak ada sumber pemasukan atau daftar null/kosong.");
            }

            System.out.println("TransactionPanel: Mengambil kategori pengeluaran untuk user ID: " + currentUser.getUserId());
            List<ExpenseCategory> categories = transactionService.getActiveExpenseCategories(currentUser.getUserId());
            if (categories != null && !categories.isEmpty()) {
                System.out.println("TransactionPanel: Ditemukan " + categories.size() + " kategori pengeluaran.");
                for (ExpenseCategory category : categories) {
                    cmbKategoriPengeluaran.addItem(category);
                }
            } else {
                System.out.println("TransactionPanel: Tidak ada kategori pengeluaran atau daftar null/kosong.");
            }
        } else {
             System.err.println("TransactionPanel: currentUser atau transactionService null di loadComboBoxData.");
        }
        System.out.println("TransactionPanel: Selesai loadComboBoxData(). Item sumber: " + cmbSumberPemasukan.getItemCount() + ", Item kategori: " + cmbKategoriPengeluaran.getItemCount());
    }

    private void savePemasukan() {
        System.out.println("--- Memulai savePemasukan ---");
        try {
            String tanggalStr = txtTanggalPemasukan.getText().trim();
            if (tanggalStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tanggal pemasukan tidak boleh kosong.", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                txtTanggalPemasukan.requestFocus();
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date utilDate = sdf.parse(tanggalStr);
            Date sqlDate = new Date(utilDate.getTime());
            System.out.println("Tanggal: " + sqlDate);

            String nominalStr = txtNominalPemasukan.getText().replace(".", "").replace(",", ".").trim(); // Handle input Indonesia & konversi
            if (nominalStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nominal pemasukan tidak boleh kosong.", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                txtNominalPemasukan.requestFocus();
                return;
            }
            BigDecimal nominal = new BigDecimal(nominalStr);
             if (nominal.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Nominal pemasukan harus lebih besar dari nol.", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                txtNominalPemasukan.requestFocus();
                return;
            }
            System.out.println("Nominal: " + nominal);

            Object selectedItemSumber = cmbSumberPemasukan.getSelectedItem();
            System.out.println("Item terpilih dari cmbSumberPemasukan: " + selectedItemSumber);
            
            Integer sourceId = null;
            String sumberName = "Tidak diketahui";

            if (selectedItemSumber instanceof IncomeSource) {
                IncomeSource selectedSource = (IncomeSource) selectedItemSumber;
                sourceId = selectedSource.getSourceId();
                sumberName = selectedSource.getSourceName();
                System.out.println("Selected IncomeSource: ID=" + sourceId + ", Name=" + sumberName);
            } else {
                JOptionPane.showMessageDialog(this, "Sumber pemasukan belum dipilih atau tidak valid.", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                cmbSumberPemasukan.requestFocus();
                return;
            }

            Transaction trxPemasukan = new Transaction();
            trxPemasukan.setUserId(currentUser.getUserId());
            trxPemasukan.setType(TransactionType.INCOME);
            trxPemasukan.setAmount(nominal);
            trxPemasukan.setTransactionDate(sqlDate);
            trxPemasukan.setDescription("Pemasukan dari " + sumberName); 
            trxPemasukan.setSourceId(sourceId); 
            trxPemasukan.setCategoryId(null); 

            System.out.println("Objek Transaksi yang akan disimpan: " + trxPemasukan);

            boolean success = transactionService.addTransaction(trxPemasukan);
            System.out.println("Hasil penyimpanan dari service: " + success);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Pemasukan berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearFormPemasukan();
                refreshOtherPanels();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan pemasukan. Cek konsol untuk detail.", "Error Penyimpanan", JOptionPane.ERROR_MESSAGE);
            }

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah. Gunakan yyyy-MM-dd.", "Error Format Tanggal", JOptionPane.ERROR_MESSAGE);
            txtTanggalPemasukan.requestFocus();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format nominal salah. Masukkan angka yang valid (misal: 50000 atau 50000.00).", "Error Format Nominal", JOptionPane.ERROR_MESSAGE);
            txtNominalPemasukan.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error Umum", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        System.out.println("--- Selesai savePemasukan ---");
    }

    private void savePengeluaran() {
        System.out.println("--- Memulai savePengeluaran ---");
        try {
            String tanggalStr = txtTanggalPengeluaran.getText().trim();
            if (tanggalStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tanggal pengeluaran tidak boleh kosong.", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                txtTanggalPengeluaran.requestFocus();
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            java.util.Date utilDate = sdf.parse(tanggalStr);
            Date sqlDate = new Date(utilDate.getTime());
            System.out.println("Tanggal: " + sqlDate);

            String jumlahStr = txtJumlahPengeluaran.getText().replace(".", "").replace(",", ".").trim();
            if (jumlahStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Jumlah pengeluaran tidak boleh kosong.", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                txtJumlahPengeluaran.requestFocus();
                return;
            }
            BigDecimal jumlah = new BigDecimal(jumlahStr);
            if (jumlah.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah pengeluaran harus lebih besar dari nol.", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                txtJumlahPengeluaran.requestFocus();
                return;
            }
            System.out.println("Jumlah: " + jumlah);

            String deskripsi = txtDeskripsiPengeluaran.getText().trim();
            if (deskripsi.isEmpty()) { 
                JOptionPane.showMessageDialog(this, "Deskripsi pengeluaran tidak boleh kosong.", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                txtDeskripsiPengeluaran.requestFocus();
                return;
            }
            System.out.println("Deskripsi: " + deskripsi);

            Object selectedItemKategori = cmbKategoriPengeluaran.getSelectedItem();
            System.out.println("Item terpilih dari cmbKategoriPengeluaran: " + selectedItemKategori);
            
            Integer categoryId = null;
            // String kategoriName = "Tidak diketahui"; // Tidak perlu jika hanya simpan ID

            if (selectedItemKategori instanceof ExpenseCategory) {
                ExpenseCategory selectedCategory = (ExpenseCategory) selectedItemKategori;
                categoryId = selectedCategory.getCategoryId();
                // kategoriName = selectedCategory.getCategoryName();
                System.out.println("Selected ExpenseCategory: ID=" + categoryId + ", Name=" + selectedCategory.getCategoryName());
            } else {
                JOptionPane.showMessageDialog(this, "Kategori pengeluaran belum dipilih atau tidak valid.", "Error Validasi", JOptionPane.ERROR_MESSAGE);
                cmbKategoriPengeluaran.requestFocus();
                return;
            }

            Transaction trxPengeluaran = new Transaction();
            trxPengeluaran.setUserId(currentUser.getUserId());
            trxPengeluaran.setType(TransactionType.EXPENSE);
            trxPengeluaran.setAmount(jumlah);
            trxPengeluaran.setTransactionDate(sqlDate);
            trxPengeluaran.setDescription(deskripsi);
            trxPengeluaran.setCategoryId(categoryId);
            trxPengeluaran.setSourceId(null);

            System.out.println("Objek Transaksi yang akan disimpan: " + trxPengeluaran);

            boolean success = transactionService.addTransaction(trxPengeluaran);
            System.out.println("Hasil penyimpanan dari service: " + success);

            if (success) {
                JOptionPane.showMessageDialog(this, "Pengeluaran berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                clearFormPengeluaran();
                refreshOtherPanels();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan pengeluaran. Cek konsol untuk detail.", "Error Penyimpanan", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah. Gunakan yyyy-MM-dd.", "Error Format Tanggal", JOptionPane.ERROR_MESSAGE);
            txtTanggalPengeluaran.requestFocus();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format jumlah salah. Masukkan angka yang valid (misal: 50000 atau 50000.00).", "Error Format Jumlah", JOptionPane.ERROR_MESSAGE);
            txtJumlahPengeluaran.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error Umum", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        System.out.println("--- Selesai savePengeluaran ---");
    }
    
    private void clearFormPemasukan() {
        txtTanggalPemasukan.setText("");
        txtNominalPemasukan.setText("");
        if (cmbSumberPemasukan.getItemCount() > 0) cmbSumberPemasukan.setSelectedIndex(0);
        txtTanggalPemasukan.requestFocus(); // Kembalikan fokus ke field pertama
    }

    private void clearFormPengeluaran() {
        txtTanggalPengeluaran.setText("");
        txtJumlahPengeluaran.setText("");
        txtDeskripsiPengeluaran.setText("");
        if (cmbKategoriPengeluaran.getItemCount() > 0) cmbKategoriPengeluaran.setSelectedIndex(0);
        txtTanggalPengeluaran.requestFocus(); // Kembalikan fokus ke field pertama
    }

    private void refreshOtherPanels() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) parentWindow;
            mainFrame.refreshDashboard();
            mainFrame.refreshReportPanel();
        }
    }
}