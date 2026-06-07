create database finance_dekstop_final;
use finance_dekstop;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Simpan password yang sudah di-hash
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE expense_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    user_id INT NULL, -- NULL untuk kategori global/default, atau FOREIGN KEY ke users jika spesifik pengguna
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_user 
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE -- Jika pengguna dihapus, kategori spesifik miliknya juga terhapus
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE income_sources (
    source_id INT AUTO_INCREMENT PRIMARY KEY,
    source_name VARCHAR(100) NOT NULL,
    user_id INT NULL, -- NULL untuk sumber global/default, atau FOREIGN KEY ke users jika spesifik pengguna
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_source_user 
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE -- Jika pengguna dihapus, sumber spesifik miliknya juga terhapus
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL, -- Transaksi ini milik siapa
    type ENUM('income', 'expense') NOT NULL, -- Jenis transaksi
    amount DECIMAL(15, 2) NOT NULL, -- Jumlah uang (15 digit total, 2 di belakang koma)
    transaction_date DATE NOT NULL, -- Tanggal transaksi
    description TEXT NULL, -- Deskripsi (bisa NULL, terutama untuk pemasukan)
    category_id INT NULL, -- FOREIGN KEY ke expense_categories (NULL jika type='income')
    source_id INT NULL,   -- FOREIGN KEY ke income_sources (NULL jika type='expense')
    is_deleted BOOLEAN DEFAULT FALSE, -- Untuk Soft Delete (FALSE = aktif, TRUE = dihapus)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Waktu pencatatan
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Waktu terakhir update
    
    CONSTRAINT fk_transaction_user 
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE -- Jika pengguna dihapus, semua transaksinya juga terhapus
        ON UPDATE CASCADE,
    CONSTRAINT fk_transaction_category 
        FOREIGN KEY (category_id) REFERENCES expense_categories(category_id)
        ON DELETE SET NULL -- Jika kategori dihapus, transaksi tetap ada tapi category_id jadi NULL
        ON UPDATE CASCADE,
    CONSTRAINT fk_transaction_source 
        FOREIGN KEY (source_id) REFERENCES income_sources(source_id)
        ON DELETE SET NULL -- Jika sumber dihapus, transaksi tetap ada tapi source_id jadi NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;