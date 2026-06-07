package com.desktopfinance.view;

import com.desktopfinance.model.User;
import com.desktopfinance.model.ExpenseCategory;
import com.desktopfinance.service.CategoryService; // Service yang baru kita buat

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManageCategoriesDialog extends JDialog {

    private User currentUser;
    private CategoryService categoryService;
    private TransactionPanel transactionPanelRef; // Referensi untuk refresh ComboBox
    private UpdateTransactionDialog updateTransactionDialogRef; // Referensi untuk refresh ComboBox

    private JList<ExpenseCategory> listCategories;
    private DefaultListModel<ExpenseCategory> listModelCategories;
    private JScrollPane scrollPaneCategories;
    private JTextField txtCategoryName;
    private JButton btnAddCategory;
    private JButton btnEditCategory;
    private JButton btnDeleteCategory;
    private JButton btnClose;

    public ManageCategoriesDialog(Frame parent, boolean modal, User user, 
                                  TransactionPanel transactionPanelRef, 
                                  UpdateTransactionDialog updateDialogRef) { // Tambahkan parameter referensi
        super(parent, modal);
        this.currentUser = user;
        this.categoryService = new CategoryService();
        this.transactionPanelRef = transactionPanelRef;
        this.updateTransactionDialogRef = updateDialogRef;

        initComponents();
        loadCategories();

        setTitle("Kelola Kategori Pengeluaran");
        // setSize(500, 400);
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Panel Daftar Kategori
        listModelCategories = new DefaultListModel<>();
        listCategories = new JList<>(listModelCategories);
        listCategories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPaneCategories = new JScrollPane(listCategories);
        scrollPaneCategories.setBorder(BorderFactory.createTitledBorder("Daftar Kategori"));
        add(scrollPaneCategories, BorderLayout.CENTER);

        // Panel Input dan Tombol Aksi (Tambah, Edit, Hapus)
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Detail Kategori"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Nama Kategori:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCategoryName = new JTextField(20);
        inputPanel.add(txtCategoryName, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAddCategory = new JButton("Tambah Baru");
        btnEditCategory = new JButton("Simpan Perubahan");
        btnDeleteCategory = new JButton("Hapus Terpilih");
        actionButtonPanel.add(btnAddCategory);
        actionButtonPanel.add(btnEditCategory);
        actionButtonPanel.add(btnDeleteCategory);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(actionButtonPanel, gbc);
        
        add(inputPanel, BorderLayout.NORTH);


        // Panel Tombol Bawah (Tutup)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnClose = new JButton("Tutup");
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);

        // Atur kondisi awal tombol
        btnEditCategory.setEnabled(false);
        btnDeleteCategory.setEnabled(false);

        // Action Listeners
        listCategories.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ExpenseCategory selectedCategory = listCategories.getSelectedValue();
                    if (selectedCategory != null) {
                        txtCategoryName.setText(selectedCategory.getCategoryName());
                        // Hanya boleh edit/hapus kategori milik sendiri (bukan yang global/user_id null)
                        boolean isUserOwned = selectedCategory.getUserId() != null && selectedCategory.getUserId().equals(currentUser.getUserId());
                        btnEditCategory.setEnabled(isUserOwned);
                        btnDeleteCategory.setEnabled(isUserOwned);
                    } else {
                        txtCategoryName.setText("");
                        btnEditCategory.setEnabled(false);
                        btnDeleteCategory.setEnabled(false);
                    }
                }
            }
        });

        btnAddCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddCategory();
            }
        });

        btnEditCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEditCategory();
            }
        });

        btnDeleteCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteCategory();
            }
        });

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Tutup dialog
            }
        });
    }

    private void loadCategories() {
        listModelCategories.clear(); // Bersihkan list dulu
        List<ExpenseCategory> categories = categoryService.getCategoriesForUser(currentUser.getUserId());
        if (categories != null) {
            for (ExpenseCategory category : categories) {
                listModelCategories.addElement(category);
            }
        }
        // Reset pilihan dan field
        listCategories.clearSelection();
        txtCategoryName.setText("");
        btnEditCategory.setEnabled(false);
        btnDeleteCategory.setEnabled(false);
    }

    private void handleAddCategory() {
        String categoryName = txtCategoryName.getText().trim();
        if (categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori tidak boleh kosong.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = categoryService.addCategory(categoryName, currentUser.getUserId());
        if (success) {
            JOptionPane.showMessageDialog(this, "Kategori baru berhasil ditambahkan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadCategories(); // Muat ulang daftar kategori
            txtCategoryName.setText(""); // Kosongkan field input
            refreshTransactionPanelComboBoxes(); // Refresh ComboBox di TransactionPanel
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan kategori. Nama mungkin sudah ada atau terjadi error lain.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEditCategory() {
        ExpenseCategory selectedCategory = listCategories.getSelectedValue();
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newCategoryName = txtCategoryName.getText().trim();
        if (newCategoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori tidak boleh kosong.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Pastikan pengguna tidak mencoba mengedit nama menjadi sama dengan kategori lain (opsional, bisa ditangani service)

        boolean success = categoryService.updateCategory(selectedCategory.getCategoryId(), newCategoryName, currentUser.getUserId());
        if (success) {
            JOptionPane.showMessageDialog(this, "Kategori berhasil diupdate.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadCategories();
            refreshTransactionPanelComboBoxes();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate kategori.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteCategory() {
        ExpenseCategory selectedCategory = listCategories.getSelectedValue();
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus kategori '" + selectedCategory.getCategoryName() + "'?\n" +
                "Transaksi yang menggunakan kategori ini akan kehilangan kategorinya (menjadi tidak berkategori).",
                "Konfirmasi Hapus Kategori",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = categoryService.deleteCategory(selectedCategory.getCategoryId(), currentUser.getUserId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Kategori berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadCategories();
                refreshTransactionPanelComboBoxes();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus kategori.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Metode untuk memberitahu TransactionPanel dan UpdateTransactionDialog agar me-refresh ComboBox kategori mereka.
     */
    private void refreshTransactionPanelComboBoxes() {
        if (transactionPanelRef != null) {
            // Anda perlu membuat metode publik di TransactionPanel, misal: public void refreshCategoryComboBox()
            // transactionPanelRef.refreshCategoryComboBox(); 
            System.out.println("ManageCategoriesDialog: Meminta TransactionPanel refresh ComboBox (belum diimplementasi di TransactionPanel)");
        }
        if (updateTransactionDialogRef != null && updateTransactionDialogRef.isDisplayable()) {
            // Anda perlu membuat metode publik di UpdateTransactionDialog, misal: public void refreshCategoryComboBox()
            // updateTransactionDialogRef.refreshCategoryComboBox();
            System.out.println("ManageCategoriesDialog: Meminta UpdateTransactionDialog refresh ComboBox (belum diimplementasi di UpdateTransactionDialog)");
        }
        // Cara yang lebih umum adalah MainFrame yang mengelola notifikasi ini,
        // atau menggunakan PropertyChangeListener jika ingin lebih decoupled.
        // Untuk saat ini, kita bisa memanggil metode refresh langsung jika referensinya ada.
    }


    // Main method untuk testing (opsional)
    // public static void main(String[] args) {
    //     EventQueue.invokeLater(() -> {
    //         // Buat User dummy untuk testing
    //         User testUser = new User();
    //         testUser.setUserId(1); // Asumsi user dengan ID 1 ada
    //         testUser.setUsername("testuser");
    //         
    //         // Buat Frame dummy sebagai parent
    //         JFrame dummyFrame = new JFrame();
    //         ManageCategoriesDialog dialog = new ManageCategoriesDialog(dummyFrame, true, testUser, null, null);
    //         dialog.setVisible(true);
    //         System.exit(0); // Keluar setelah dialog ditutup
    //     });
    // }
}