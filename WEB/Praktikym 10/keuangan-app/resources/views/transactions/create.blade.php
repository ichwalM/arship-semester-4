@extends('layouts.app')

@section('content')
    <h1>Tambah Transaksi</h1>
    @if (session('success'))
        <div class="alert alert-success">{{ session('success') }}</div>
    @endif
    <div class="card p-3">
        <form action="{{ route('transactions.store') }}" method="POST">
            @csrf
            <div class="mb-3">
                <label for="date" class="form-label">Tanggal</label>
                <input type="date" name="date" id="date" class="form-control" required>
            </div>
            <div class="mb-3">
                <label for="description" class="form-label">Deskripsi</label>
                <input type="text" name="description" id="description" class="form-control" required>
            </div>
            <div class="mb-3">
                <label for="category" class="form-label">Kategori</label>
                <select name="cateory" id="category" class="form-control">
                    <option value="Uang Bulanan">Uang Bulanan</option>
                <option value="Bayar Kost">Bayar Kost</option>
                    <option value="Belanja Bulanan">Belanja Bulanan</option>
                    <option value="Investatis Bulanan">Investasi Bulanan</option>
                    <option value="Jajan / Pekan">Jajan / Pekan</option>
                </select>
                <!-- <input type="text" name="category" id="category" class="form-control"> -->
            </div>
            <div class="mb-3">
                <label for="type" class="form-label">Tipe</label>
                <select name="type" id="type" class="form-control" required>
                    <option value="income">Pemasukan</option>
                    <option value="expense">Pengeluaran</option>
                </select>
            </div>
            <div class="mb-3">
                <label for="amount" class="form-label">Jumlah</label>
                <input type="number" name="amount" id="amount" class="form-control" step="0.01" required>
            </div>
            <button type="submit" class="btn ">Simpan</button>
            <a href="{{ route('dashboard') }}" class="btn">Batal</a>
        </form>
    </div>
@endsection