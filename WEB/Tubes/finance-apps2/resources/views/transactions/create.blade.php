@extends('layouts.app')

@section('title', 'Tambah Transaksi')

@section('content')
<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Tambah Transaksi</h5>
                </div>
                <div class="card-body">
                    <form action="{{ route('transactions.store') }}" method="POST" enctype="multipart/form-data">
                        @csrf
                        
                        <div class="mb-3">
                            <label for="type" class="form-label">Tipe Transaksi</label>
                            <select class="form-select @error('type') is-invalid @enderror" id="type" name="type" required>
                                <option value="" selected disabled>Pilih Tipe Transaksi</option>
                                <option value="income" {{ old('type') == 'income' ? 'selected' : '' }}>Pendapatan</option>
                                <option value="expense" {{ old('type') == 'expense' ? 'selected' : '' }}>Pengeluaran</option>
                                <option value="transfer" {{ old('type') == 'transfer' ? 'selected' : '' }}>Transfer</option>
                            </select>
                            @error('type')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="category_id" class="form-label">Kategori</label>
                            <select class="form-select @error('category_id') is-invalid @enderror" id="category_id" name="category_id" required>
                                <option value="" selected disabled>Pilih Kategori</option>
                                @foreach($categories as $category)
                                    <option value="{{ $category->id }}" data-type="{{ $category->type }}" {{ old('category_id') == $category->id ? 'selected' : '' }}>{{ $category->name }}</option>
                                @endforeach
                            </select>
                            @error('category_id')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="account_id" class="form-label">Akun</label>
                            <select class="form-select @error('account_id') is-invalid @enderror" id="account_id" name="account_id" required>
                                <option value="" selected disabled>Pilih Akun</option>
                                @foreach($accounts as $account)
                                    <option value="{{ $account->id }}" {{ old('account_id') == $account->id ? 'selected' : '' }}>{{ $account->name }} ({{ number_format($account->current_balance, 0, ',', '.') }} {{ $account->currency }})</option>
                                @endforeach
                            </select>
                            @error('account_id')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="amount" class="form-label">Jumlah</label>
                            <div class="input-group">
                                <span class="input-group-text">Rp</span>
                                <input type="number" step="0.01" class="form-control @error('amount') is-invalid @enderror" id="amount" name="amount" value="{{ old('amount') }}" required>
                                @error('amount')
                                    <div class="invalid-feedback">{{ $message }}</div>
                                @enderror
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="transaction_date" class="form-label">Tanggal Transaksi</label>
                            <input type="date" class="form-control @error('transaction_date') is-invalid @enderror" id="transaction_date" name="transaction_date" value="{{ old('transaction_date', date('Y-m-d')) }}" required>
                            @error('transaction_date')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="description" class="form-label">Deskripsi (opsional)</label>
                            <textarea class="form-control @error('description') is-invalid @enderror" id="description" name="description" rows="3">{{ old('description') }}</textarea>
                            @error('description')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="attachment" class="form-label">Lampiran (opsional)</label>
                            <input type="file" class="form-control @error('attachment') is-invalid @enderror" id="attachment" name="attachment">
                            <small class="text-muted">Format yang didukung: jpg, jpeg, png, pdf. Maksimal 2MB.</small>
                            @error('attachment')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="d-flex justify-content-between">
                            <a href="{{ route('transactions.index') }}" class="btn btn-secondary">Kembali</a>
                            <button type="submit" class="btn btn-primary">Simpan</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script>
    // Fungsi untuk filter kategori berdasarkan tipe transaksi
    document.addEventListener('DOMContentLoaded', function() {
        const typeSelect = document.getElementById('type');
        const categorySelect = document.getElementById('category_id');
        const categoryOptions = Array.from(categorySelect.options);
        
        typeSelect.addEventListener('change', function() {
            const selectedType = this.value;
            
            // Reset kategori
            categorySelect.innerHTML = '<option value="" selected disabled>Pilih Kategori</option>';
            
            // Filter kategori berdasarkan tipe
            categoryOptions.forEach(option => {
                if (option.value === '') return; // Skip option placeholder
                
                const categoryType = option.getAttribute('data-type');
                if (
                    (selectedType === 'income' && categoryType === 'income') || 
                    (selectedType === 'expense' && categoryType === 'expense') ||
                    (selectedType === 'transfer')
                ) {
                    categorySelect.appendChild(option.cloneNode(true));
                }
            });
        });
        
        // Trigger change event if type is already selected (e.g. after validation error)
        if (typeSelect.value) {
            typeSelect.dispatchEvent(new Event('change'));
            
            // Reselect previously selected category if exists
            const oldCategoryId = "{{ old('category_id') }}";
            if (oldCategoryId) {
                for(let i = 0; i < categorySelect.options.length; i++) {
                    if (categorySelect.options[i].value === oldCategoryId) {
                        categorySelect.options[i].selected = true;
                        break;
                    }
                }
            }
        }
    });
</script>
@endsection

{{-- resources/views/transactions/edit.blade.php --}}
@extends('layouts.app')

@section('title', 'Edit Transaksi')

@section('content')
<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Edit Transaksi</h5>
                </div>
                <div class="card-body">
                    <form action="{{ route('transactions.update', $transaction) }}" method="POST" enctype="multipart/form-data">
                        @csrf
                        @method('PUT')
                        
                        <div class="mb-3">
                            <label for="type" class="form-label">Tipe Transaksi</label>
                            <select class="form-select @error('type') is-invalid @enderror" id="type" name="type" required>
                                <option value="" disabled>Pilih Tipe Transaksi</option>
                                <option value="income" {{ old('type', $transaction->type) == 'income' ? 'selected' : '' }}>Pendapatan</option>
                                <option value="expense" {{ old('type', $transaction->type) == 'expense' ? 'selected' : '' }}>Pengeluaran</option>
                                <option value="transfer" {{ old('type', $transaction->type) == 'transfer' ? 'selected' : '' }}>Transfer</option>
                            </select>
                            @error('type')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="category_id" class="form-label">Kategori</label>
                            <select class="form-select @error('category_id') is-invalid @enderror" id="category_id" name="category_id" required>
                                <option value="" disabled>Pilih Kategori</option>
                                @foreach($categories as $category)
                                    <option value="{{ $category->id }}" data-type="{{ $category->type }}" {{ old('category_id', $transaction->category_id) == $category->id ? 'selected' : '' }}>{{ $category->name }}</option>
                                @endforeach
                            </select>
                            @error('category_id')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="account_id" class="form-label">Akun</label>
                            <select class="form-select @error('account_id') is-invalid @enderror" id="account_id" name="account_id" required>
                                <option value="" disabled>Pilih Akun</option>
                                @foreach($accounts as $account)
                                    <option value="{{ $account->id }}" {{ old('account_id', $transaction->account_id) == $account->id ? 'selected' : '' }}>{{ $account->name }} ({{ number_format($account->current_balance, 0, ',', '.') }})</option>
                                @endforeach
                            </select>
                            @error('account_id')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="amount" class="form-label">Jumlah</label>
                            <div class="input-group">
                                <span class="input-group-text">Rp</span>
                                <input type="number" step="0.01" class="form-control @error('amount') is-invalid @enderror" id="amount" name="amount" value="{{ old('amount', $transaction->amount) }}" required>
                                @error('amount')
                                    <div class="invalid-feedback">{{ $message }}</div>
                                @enderror
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="transaction_date" class="form-label">Tanggal Transaksi</label>
                            <input type="date" class="form-control @error('transaction_date') is-invalid @enderror" id="transaction_date" name="transaction_date" value="{{ old('transaction_date', $transaction->transaction_date->format('Y-m-d')) }}" required>
                            @error('transaction_date')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="description" class="form-label">Deskripsi (opsional)</label>
                            <textarea class="form-control @error('description') is-invalid @enderror" id="description" name="description" rows="3">{{ old('description', $transaction->description) }}</textarea>
                            @error('description')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="attachment" class="form-label">Lampiran (opsional)</label>
                            <input type="file" class="form-control @error('attachment') is-invalid @enderror" id="attachment" name="attachment">
                            <small class="text-muted">Format yang didukung: jpg, jpeg, png, pdf. Maksimal 2MB.</small>
                            
                            @if($transaction->attachment)
                                <div class="mt-2">
                                    <p>Lampiran saat ini: <a href="{{ asset('storage/' . $transaction->attachment) }}" target="_blank">Lihat Lampiran</a></p>
                                </div>
                            @endif
                            
                            @error('attachment')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="d-flex justify-content-between">
                            <a href="{{ route('transactions.index') }}" class="btn btn-secondary">Kembali</a>
                            <button type="submit" class="btn btn-primary">Simpan Perubahan</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script>
    // Fungsi untuk filter kategori berdasarkan tipe transaksi
    document.addEventListener('DOMContentLoaded', function() {
        const typeSelect = document.getElementById('type');
        const categorySelect = document.getElementById('category_id');
        const categoryOptions = Array.from(categorySelect.options);
        
        typeSelect.addEventListener('change', function() {
            const selectedType = this.value;
            
            // Reset kategori
            categorySelect.innerHTML = '<option value="" disabled>Pilih Kategori</option>';
            
            // Filter kategori berdasarkan tipe
            categoryOptions.forEach(option => {
                if (option.value === '') return; // Skip option placeholder
                
                const categoryType = option.getAttribute('data-type');
                if (
                    (selectedType === 'income' && categoryType === 'income') || 
                    (selectedType === 'expense' && categoryType === 'expense') ||
                    (selectedType === 'transfer')
                ) {
                    categorySelect.appendChild(option.cloneNode(true));
                }
            });
        });
        
        // Trigger change event if type is already selected
        if (typeSelect.value) {
            typeSelect.dispatchEvent(new Event('change'));
            
            // Reselect previously selected category
            const selectedCategoryId = "{{ old('category_id', $transaction->category_id) }}";
            if (selectedCategoryId) {
                for(let i = 0; i < categorySelect.options.length; i++) {
                    if (categorySelect.options[i].value === selectedCategoryId) {
                        categorySelect.options[i].selected = true;
                        break;
                    }
                }
            }
        }
    });
</script>
@endsection