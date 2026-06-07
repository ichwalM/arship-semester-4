@extends('layouts.app')

@section('title', 'Daftar Transaksi')

@section('actions')
    <a href="{{ route('transactions.create') }}" class="btn btn-sm btn-primary">
        <i class="fas fa-plus"></i> Tambah Transaksi
    </a>
@endsection

@section('content')
<!-- Filter Section -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">Filter Transaksi</h6>
    </div>
    <div class="card-body">
        <form action="{{ route('transactions.index') }}" method="GET" class="row g-3">
            <div class="col-md-3">
                <label for="type">Tipe Transaksi</label>
                <select name="type" id="type" class="form-select">
                    <option value="">Semua</option>
                    <option value="income" {{ request('type') == 'income' ? 'selected' : '' }}>Pemasukan</option>
                    <option value="expense" {{ request('type') == 'expense' ? 'selected' : '' }}>Pengeluaran</option>
                </select>
            </div>
            <div class="col-md-3">
                <label for="category">Kategori</label>
                <select name="category" id="category" class="form-select">
                    <option value="">Semua Kategori</option>
                    <optgroup label="Pemasukan">
                        @foreach($categories['income'] as $category)
                            <option value="{{ $category }}" {{ request('category') == $category ? 'selected' : '' }}>
                                {{ ucfirst($category) }}
                            </option>
                        @endforeach
                    </optgroup>
                    <optgroup label="Pengeluaran">
                        @foreach($categories['expense'] as $category)
                            <option value="{{ $category }}" {{ request('category') == $category ? 'selected' : '' }}>
                                {{ ucfirst($category) }}
                            </option>
                        @endforeach
                    </optgroup>
                </select>
            </div>
            <div class="col-md-2">
                <label for="start_date">Tanggal Mulai</label>
                <input type="date" class="form-control" id="start_date" name="start_date" value="{{ request('start_date') }}">
            </div>
            <div class="col-md-2">
                <label for="end_date">Tanggal Akhir</label>
                <input type="date" class="form-control" id="end_date" name="end_date" value="{{ request('end_date') }}">
            </div>
            <div class="col-md-2 d-flex align-items-end">
                <button type="submit" class="btn btn-primary me-2">
                    <i class="fas fa-filter"></i> Filter
                </button>
                <a href="{{ route('transactions.index') }}" class="btn btn-secondary">
                    <i class="fas fa-redo"></i> Reset
                </a>
            </div>
        </form>
    </div>