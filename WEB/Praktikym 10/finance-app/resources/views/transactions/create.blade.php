@extends('layouts.app')

@section('title', 'Tambah Transaksi Baru')

@section('actions')
    <a href="{{ route('transactions.index') }}" class="btn btn-sm btn-secondary">
        <i class="fas fa-arrow-left"></i> Kembali
    </a>
@endsection

@section('content')
<div class="row">
    <div class="col-lg-8 mx-auto">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Form Tambah Transaksi</h6>
            </div>
            <div class="card-body">
                <form action="{{ route('transactions.store') }}" method="POST">
                    @csrf
                    
                    <div class="mb-3">
                        <label for="description" class="form-label">Deskripsi <span class="text-danger">*</span></label>
                        <input type="text" class="form-control @error('description') is-invalid @enderror" 
                               id="description" name="description" value="{{ old('description') }}" required>
                        @error('description')
                            <div class="invalid-feedback">{{ $message }}</div>
                        @enderror
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="amount" class="form-label">Jumlah <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text">Rp</span>
                                <input type="number" class="form-control @error('amount') is-invalid @enderror" 
                                       id="amount" name="amount" value="{{ old('amount') }}" min="0" step="0.01" required>
                                @error('amount')
                                    <div class="invalid-feedback">{{ $message }}</div>
                                @enderror
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label for="transaction_date" class="form-label">Tanggal Transaksi <span class="text-danger">*</span></label>
                            <input type="date" class="form-control @error('transaction_date') is-invalid @enderror" 
                                   id="transaction_date" name="transaction_date" value="{{ old('transaction_date') ?? date('Y-m-d') }}" required>
                            @error('transaction_date')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="type" class="form-label">Tipe Transaksi <span class="text-danger">*</span></label>
                            <select class="form-select @error('type') is-invalid @enderror" id="type" name="type" required>
                                <option value="" selected disabled>-- Pilih Tipe --</option>
                                <option value="income" {{ old('type') == 'income' ? 'selected' : '' }}>Pemasukan</option>
                                <option value="expense" {{ old('type') == 'expense' ? 'selected' : '' }}>Pengeluaran</option>
                            </select>
                            @error('type')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        <div class="col-md-6">
                            <label for="category" class="form-label">Kategori <span class="text-danger">*</span></label>
                            <select class="form-select @error('category') is-invalid @enderror" id="category" name="category" required>
                                <option value="" selected disabled>-- Pilih Kategori --</option>
                                <!-- Kategori akan dimuat melalui JavaScript berdasarkan tipe -->
                            </select>
                            @error('category')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="notes" class="form-label">Catatan</label>
                        <textarea class="form-control @error('notes') is-invalid @enderror" 
                                  id="notes" name="notes" rows="3">{{ old('notes') }}</textarea>
                        @error('notes')
                            <div class="invalid-feedback">{{ $message }}</div>
                        @enderror
                    </div>

                    <div class="text-center">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Simpan Transaksi
                        </button>
                        <button type="reset" class="btn btn-secondary">
                            <i class="fas fa-undo"></i> Reset
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
@endsection

@push('scripts')
<script>
    // Kategori berdasarkan tipe transaksi
    const categories = @json($categories);
    
    // Fungsi untuk memuat kategori berdasarkan tipe yang dipilih
    function loadCategories(type) {
        const categorySelect = document.getElementById('category');
        categorySelect.innerHTML = '<option value="" selected disabled>-- Pilih Kategori --</option>';
        
        if (type && categories[type]) {
            categories[type].forEach(category => {
                const option = document.createElement('option');
                option.value = category;
                option.textContent = category.charAt(0).toUpperCase() + category.slice(1);
                categorySelect.appendChild(option);
            });
        }
    }
    
    // Event listener untuk perubahan tipe transaksi
    document.getElementById('type').addEventListener('change', function() {
        loadCategories(this.value);
    });
    
    // Memuat kategori saat halaman dimuat jika tipe sudah dipilih
    document.addEventListener('DOMContentLoaded', function() {
        const selectedType = document.getElementById('type').value;
        if (selectedType) {
            loadCategories(selectedType);
            
            // Restore selected category if exists
            const oldCategory = "{{ old('category') }}";
            if (oldCategory) {
                document.querySelectorAll('#category option').forEach(option => {
                    if (option.value === oldCategory) {
                        option.selected = true;
                    }
                });
            }
        }
    });
</script>
@endpush