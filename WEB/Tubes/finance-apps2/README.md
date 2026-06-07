# Aplikasi Manajemen Keuangan

Aplikasi Manajemen Keuangan adalah aplikasi berbasis web yang dibangun menggunakan Laravel 12 dan Bootstrap 5. Aplikasi ini dirancang untuk membantu pengguna mengelola keuangan pribadi dengan fitur-fitur seperti pencatatan transaksi, pengelolaan akun, kategori, anggaran, dan laporan keuangan.

## Fitur

- **Autentikasi Pengguna**: Login dan Registrasi
- **Dashboard**: Tampilan ringkasan keuangan
- **Kelola Kategori**: Kategorisasi pendapatan dan pengeluaran
- **Kelola Akun**: Pengelolaan berbagai jenis akun keuangan (Kas, Bank, E-Wallet)
- **Kelola Transaksi**: Pencatatan dan pelacakan transaksi keuangan
- **Anggaran**: Membuat dan memantau anggaran pengeluaran
- **Laporan Keuangan**: Laporan pendapatan dan pengeluaran dengan visualisasi
- **Arus Kas**: Laporan arus kas untuk memantau tren keuangan

## Teknologi yang Digunakan

- **Laravel 12**: Framework PHP
- **Bootstrap 5**: Framework CSS untuk tampilan responsif
- **MySQL**: Database
- **Chart.js**: Untuk visualisasi data
- **Font Awesome**: Untuk ikon

## Persyaratan Sistem

- PHP >= 8.1
- Composer
- MySQL atau MariaDB
- Node.js & NPM (untuk mengkompilasi asset)
- Web Server (Apache atau Nginx)

## Instalasi

1. **Clone repositori:**
   ```bash
   git clone https://github.com/username/manajemen-keuangan.git
   cd manajemen-keuangan
   ```

2. **Instal dependensi PHP:**
   ```bash
   composer install
   ```

3. **Salin file .env.example menjadi .env:**
   ```bash
   cp .env.example .env
   ```

4. **Konfigurasi database di file .env:**
   ```env
   DB_CONNECTION=mysql
   DB_HOST=127.0.0.1
   DB_PORT=3306
   DB_DATABASE=manajemen_keuangan
   DB_USERNAME=root
   DB_PASSWORD=
   ```

5. **Generate application key:**
   ```bash
   php artisan key:generate
   ```

6. **Jalankan migrasi dan seeder:**
   ```bash
   php artisan migrate --seed
   ```

7. **Buat symlink storage:**
   ```bash
   php artisan storage:link
   ```

8. **Jalankan aplikasi:**
   ```bash
   php artisan serve
   ```

9. **Buka aplikasi di browser:**
   http://localhost:8000

## Akun Demo

Aplikasi ini memiliki dua akun demo yang bisa digunakan untuk login:

1. **Admin**
   - Email: admin@example.com
   - Password: password

2. **User**
   - Email: user@example.com
   - Password: password

## Struktur Database

### Tabel Master
1. **users**: Menyimpan data pengguna
2. **categories**: Kategori transaksi keuangan (Pendapatan, Pengeluaran, dll)
3. **accounts**: Jenis akun keuangan (Kas, Bank, E-Wallet, dll)

### Tabel Transaksi
1. **transactions**: Mencatat semua transaksi keuangan
2. **budgets**: Anggaran keuangan per kategori

## Cara Penggunaan

1. **Login atau Registrasi**:
   - Gunakan akun demo atau registrasi akun baru.

2. **Dashboard**:
   - Lihat ringkasan saldo, pendapatan, pengeluaran, dan statistik lainnya.

3. **Mengelola Kategori**:
   - Buat, edit, atau hapus kategori pendapatan dan pengeluaran.

4. **Mengelola Akun**:
   - Tambahkan akun keuangan seperti kas, rekening bank, atau e-wallet.

5. **Mengelola Transaksi**:
   - Catat pendapatan dan pengeluaran dengan memilih kategori dan akun.

6. **Membuat Anggaran**:
   - Buat anggaran untuk kategori pengeluaran tertentu dengan periode waktu.

7. **Melihat Laporan**:
   - Lihat laporan keuangan dengan visualisasi grafik.
   - Lihat tren pendapatan dan pengeluaran dalam laporan arus kas.

## Kontribusi

Silakan fork repositori ini dan kirimkan pull request untuk berkontribusi.

## Lisensi

Aplikasi ini dilisensikan di bawah [MIT License](LICENSE).