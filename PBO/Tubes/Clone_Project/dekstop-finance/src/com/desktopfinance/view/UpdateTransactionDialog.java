package com.desktopfinance.view;

import com.desktopfinance.model.User;
import com.desktopfinance.model.Transaction;
import com.desktopfinance.model.TransactionType;
import com.desktopfinance.model.ExpenseCategory;
import com.desktopfinance.model.IncomeSource;
import com.desktopfinance.service.TransactionService;

import javax.swing.*;
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

public class UpdateTransactionDialog extends JDialog {

    private User currentUser;
    private Transaction transactionToUpdate;
    private TransactionService transactionService;
    private ReportPanel reportPanelRef; // Referensi ke ReportPanel untuk refresh

    private JLabel lblTanggal, lblJumlah, lblDeskripsi, lblKategori, lblSumber, lblTipe;
    private JTextField txtTanggal, txtJumlah, txtDeskripsi;
    private JComboBox<ExpenseCategory> cmbKategori;
    private JComboBox<IncomeSource> cmbSumber;
    private JComboBox<TransactionType> cmbTipe; // Atau radio button, atau non-editable

    private JButton btnSimpanPerubahan;
    private JButton btnBatal;
    
    private NumberFormat amountFormatter;

    public UpdateTransactionDialog(Frame parent, boolean modal, User user, Transaction transaction, 
                                   TransactionService service, ReportPanel reportPanel) {
        super(parent, modal); // Membuat dialog modal
        this.currentUser = user;
        this.transactionToUpdate = transaction;
        this.transactionService = service;
        this.reportPanelRef = reportPanel;

        this.amountFormatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        this.amountFormatter.setMaximumFractionDigits(2);
        this.amountFormatter.setGroupingUsed(false);

        initComponents();
        populateFields(); // Isi field dengan data transaksi yang ada
        
        setTitle("Update Transaksi - ID: " + transaction.getTransactionId());
        // setSize(450, 350); // Sesuaikan ukuran
        pack(); // Ukuran otomatis berdasarkan komponen
        setLocationRelativeTo(parent); // Tampil di tengah parent frame
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Hanya menutup dialog ini
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Tipe Transaksi (mungkin non-editable atau pilihan terbatas)
        lblTipe = new JLabel("Tipe Transaksi:");
        cmbTipe = new JComboBox<>(TransactionType.values()); // Isi dengan INCOME, EXPENSE
        cmbTipe.setEnabled(false); // Umumnya tipe transaksi tidak diubah, jika boleh diubah, set ke true

        lblTanggal = new JLabel("Tanggal (YYYY-MM-DD):");
        txtTanggal = new JTextField(15);
        
        lblJumlah = new JLabel("Jumlah/Nominal:");
        txtJumlah = new JTextField(15);

        lblDeskripsi = new JLabel("Deskripsi:");
        txtDeskripsi = new JTextField(15);

        lblKategori = new JLabel("Kategori Pengeluaran:");
        cmbKategori = new JComboBox<>();

        lblSumber = new JLabel("Sumber Pemasukan:");
        cmbSumber = new JComboBox<>();

        btnSimpanPerubahan = new JButton("Simpan Perubahan");
        btnBatal = new JButton("Batal");

        // Baris 0: Tipe
        gbc.gridx = 0; gbc.gridy = 0; add(lblTipe, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; add(cmbTipe, gbc);
        
        // Baris 1: Tanggal
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; add(lblTanggal, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; add(txtTanggal, gbc);

        // Baris 2: Jumlah
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; add(lblJumlah, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; add(txtJumlah, gbc);

        // Baris 3: Deskripsi
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; add(lblDeskripsi, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; add(txtDeskripsi, gbc);
        
        // Baris 4: Kategori (hanya tampil jika tipe EXPENSE)
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; add(lblKategori, gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.HORIZONTAL; add(cmbKategori, gbc);

        // Baris 5: Sumber (hanya tampil jika tipe INCOME)
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; add(lblSumber, gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL; add(cmbSumber, gbc);

        // Panel Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSimpanPerubahan);
        buttonPanel.add(btnBatal);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Action Listeners
        btnSimpanPerubahan.addActionListener(e -> performUpdate());
        btnBatal.addActionListener(e -> dispose()); // Tutup dialog
    }

    private void populateFields() {
        if (transactionToUpdate == null) return;

        cmbTipe.setSelectedItem(transactionToUpdate.getType());
        txtTanggal.setText(new SimpleDateFormat("yyyy-MM-dd").format(transactionToUpdate.getTransactionDate()));
        txtJumlah.setText(amountFormatter.format(transactionToUpdate.getAmount()));
        txtDeskripsi.setText(transactionToUpdate.getDescription());

        // Load dan set Kategori/Sumber ComboBox
        loadComboBoxData(); // Pastikan ini memuat data yang relevan

        if (transactionToUpdate.getType() == TransactionType.EXPENSE) {
            lblSumber.setVisible(false);
            cmbSumber.setVisible(false);
            lblKategori.setVisible(true);
            cmbKategori.setVisible(true);
            // Pilih kategori yang sesuai
            if (transactionToUpdate.getCategoryId() != null) {
                for (int i = 0; i < cmbKategori.getItemCount(); i++) {
                    if (cmbKategori.getItemAt(i).getCategoryId() == transactionToUpdate.getCategoryId()) {
                        cmbKategori.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } else if (transactionToUpdate.getType() == TransactionType.INCOME) {
            lblKategori.setVisible(false);
            cmbKategori.setVisible(false);
            lblSumber.setVisible(true);
            cmbSumber.setVisible(true);
            // Pilih sumber yang sesuai
            if (transactionToUpdate.getSourceId() != null) {
                for (int i = 0; i < cmbSumber.getItemCount(); i++) {
                    if (cmbSumber.getItemAt(i).getSourceId() == transactionToUpdate.getSourceId()) {
                        cmbSumber.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void loadComboBoxData() {
        // Mirip dengan di TransactionPanel, ambil data dari service
        cmbKategori.removeAllItems();
        cmbSumber.removeAllItems();

        if (currentUser != null && transactionService != null) {
            List<ExpenseCategory> categories = transactionService.getActiveExpenseCategories(currentUser.getUserId());
            if (categories != null) {
                for (ExpenseCategory category : categories) {
                    cmbKategori.addItem(category);
                }
            }
            List<IncomeSource> sources = transactionService.getActiveIncomeSources(currentUser.getUserId());
            if (sources != null) {
                for (IncomeSource source : sources) {
                    cmbSumber.addItem(source);
                }
            }
        }
    }

    private void performUpdate() {
        // 1. Validasi Input
        String tanggalStr = txtTanggal.getText().trim();
        if (tanggalStr.isEmpty()) { /* ... pesan error ... */ return; }
        // ... (validasi lain seperti di TransactionPanel: tanggal, jumlah, deskripsi jika perlu) ...
        Date sqlDate;
        try {
            sqlDate = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(tanggalStr).getTime());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah (YYYY-MM-DD).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(txtJumlah.getText().replace(",", "").trim());
             if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah/Nominal harus lebih besar dari nol.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format jumlah/nominal salah.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String description = txtDeskripsi.getText().trim();
        if (transactionToUpdate.getType() == TransactionType.EXPENSE && description.isEmpty()){
            JOptionPane.showMessageDialog(this, "Deskripsi tidak boleh kosong untuk pengeluaran.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        // 2. Update objek transactionToUpdate
        transactionToUpdate.setTransactionDate(sqlDate);
        transactionToUpdate.setAmount(amount);
        transactionToUpdate.setDescription(description);
        // Tipe tidak diubah, jadi transactionToUpdate.getType() masih sama

        if (transactionToUpdate.getType() == TransactionType.EXPENSE) {
            ExpenseCategory selectedCategory = (ExpenseCategory) cmbKategori.getSelectedItem();
            if (selectedCategory != null) {
                transactionToUpdate.setCategoryId(selectedCategory.getCategoryId());
            } else {
                transactionToUpdate.setCategoryId(null); // Atau beri error jika wajib
            }
            transactionToUpdate.setSourceId(null); // Pastikan sourceId null untuk expense
        } else if (transactionToUpdate.getType() == TransactionType.INCOME) {
            IncomeSource selectedSource = (IncomeSource) cmbSumber.getSelectedItem();
            if (selectedSource != null) {
                transactionToUpdate.setSourceId(selectedSource.getSourceId());
            } else {
                transactionToUpdate.setSourceId(null); // Atau beri error jika wajib
            }
            transactionToUpdate.setCategoryId(null); // Pastikan categoryId null untuk income
        }

        // 3. Panggil service untuk update
        // Kita perlu metode updateTransaction di TransactionService dan TransactionDAO
        // boolean success = transactionService.updateTransaction(transactionToUpdate);
//        boolean success = true; // Placeholder - GANTI DENGAN PEMANGGILAN SERVICE YANG BENAR
        boolean success = transactionService.updateTransaction(transactionToUpdate);
        System.out.println("Placeholder: Memanggil transactionService.updateTransaction()");
        System.out.println("Data yang akan diupdate: " + transactionToUpdate);


        if (success) { // Ganti dengan hasil dari service
            JOptionPane.showMessageDialog(this, "Transaksi berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            if (reportPanelRef != null) {
                reportPanelRef.refreshReportData(); // Ini me-refresh tabel laporan
            }

            // --- REFRESH DASHBOARD ---
            // Parent dari JDialog adalah Frame yang kita teruskan saat pembuatan (yaitu MainFrame)
            Window parentWindow = SwingUtilities.getWindowAncestor(this); 
            if (parentWindow instanceof MainFrame) {
                MainFrame mainFrame = (MainFrame) parentWindow;
                mainFrame.refreshDashboard(); // Panggil metode refreshDashboard di MainFrame
                System.out.println("UpdateTransactionDialog: Perintah refresh dashboard dikirim."); 
            } else {
                // Jika parent bukan MainFrame (seharusnya tidak terjadi jika dipanggil dari ReportPanel di MainFrame)
                // Kita bisa coba cara lain jika di atas tidak bekerja
                Object parentDialogOwner = getParent(); // getParent() dari JDialog mengembalikan Owner
                if (parentDialogOwner instanceof MainFrame) {
                     MainFrame mainFrame = (MainFrame) parentDialogOwner;
                     mainFrame.refreshDashboard();
                     System.out.println("UpdateTransactionDialog (via getParent()): Perintah refresh dashboard dikirim.");
                } else {
                     System.err.println("UpdateTransactionDialog: Tidak bisa mendapatkan MainFrame untuk refresh dashboard.");
                }
            }
            dispose(); // Tutup dialog
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate transaksi. Cek konsol.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}