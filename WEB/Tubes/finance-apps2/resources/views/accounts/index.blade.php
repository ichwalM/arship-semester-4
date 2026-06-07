@extends('layouts.app')

@section('title', 'Daftar Akun')

@section('content')
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Daftar Akun</h1>
        <a href="{{ route('accounts.create') }}" class="btn btn-primary">Tambah Akun</a>
    </div>
    
    @if(session('success'))
        <div class="alert alert-success">
            {{ session('success') }}
        </div>
    @endif
    
    <div class="row">
        @forelse($accounts as $account)
            <div class="col-md-4 mb-4">
                <div class="card h-100">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">{{ $account->name }}</h5>
                        <span class="badge bg-primary">{{ $account->type }}</span>
                    </div>
                    <div class="card-body">
                        <h2 class="text-center mb-4">{{ number_format($account->current_balance, 0, ',', '.') }} {{ $account->currency }}</h2>
                        <p class="text-muted">{{ $account->description }}</p>
                    </div>
                    <div class="card-footer d-flex justify-content-between">
                        <a href="{{ route('accounts.show', $account) }}" class="btn btn-sm btn-info">Detail</a>
                        <div>
                            <a href="{{ route('accounts.edit', $account) }}" class="btn btn-sm btn-warning">Edit</a>
                            <form action="{{ route('accounts.destroy', $account) }}" method="POST" class="d-inline">
                                @csrf
                                @method('DELETE')
                                <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Anda yakin ingin menghapus akun ini?')">Hapus</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        @empty
            <div class="col-12">
                <div class="alert alert-info">
                    Anda belum memiliki akun. Silakan buat akun baru.
                </div>
            </div>
        @endforelse
    </div>
</div>
@endsection

{{-- resources/views/accounts/create.blade.php --}}
@extends('layouts.app')

@section('title', 'Tambah Akun')

@section('content')
<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Tambah Akun</h5>
                </div>
                <div class="card-body">
                    <form action="{{ route('accounts.store') }}" method="POST">
                        @csrf
                        
                        <div class="mb-3">
                            <label for="name" class="form-label">Nama Akun</label>
                            <input type="text" class="form-control @error('name') is-invalid @enderror" id="name" name="name" value="{{ old('name') }}" required>
                            @error('name')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="type" class="form-label">Tipe Akun</label>
                            <select class="form-select @error('type') is-invalid @enderror" id="type" name="type" required>
                                <option value="" selected disabled>Pilih Tipe Akun</option>
                                <option value="cash" {{ old('type') == 'cash' ? 'selected' : '' }}>Kas</option>
                                <option value="bank" {{ old('type') == 'bank' ? 'selected' : '' }}>Bank</option>
                                <option value="e-wallet" {{ old('type') == 'e-wallet' ? 'selected' : '' }}>E-Wallet</option>
                            </select>
                            @error('type')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="initial_balance" class="form-label">Saldo Awal</label>
                            <div class="input-group">
                                <span class="input-group-text">Rp</span>
                                <input type="number" step="0.01" class="form-control @error('initial_balance') is-invalid @enderror" id="initial_balance" name="initial_balance" value="{{ old('initial_balance', 0) }}" required>
                                @error('initial_balance')
                                    <div class="invalid-feedback">{{ $message }}</div>
                                @enderror
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="currency" class="form-label">Mata Uang</label>
                            <input type="text" class="form-control @error('currency') is-invalid @enderror" id="currency" name="currency" value="{{ old('currency', 'IDR') }}" required>
                            @error('currency')
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
                        
                        <div class="d-flex justify-content-between">
                            <a href="{{ route('accounts.index') }}" class="btn btn-secondary">Kembali</a>
                            <button type="submit" class="btn btn-primary">Simpan</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

{{-- resources/views/accounts/edit.blade.php --}}
@extends('layouts.app')

@section('title', 'Edit Akun')

@section('content')
<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Edit Akun</h5>
                </div>
                <div class="card-body">
                    <form action="{{ route('accounts.update', $account) }}" method="POST">
                        @csrf
                        @method('PUT')
                        
                        <div class="mb-3">
                            <label for="name" class="form-label">Nama Akun</label>
                            <input type="text" class="form-control @error('name') is-invalid @enderror" id="name" name="name" value="{{ old('name', $account->name) }}" required>
                            @error('name')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="type" class="form-label">Tipe Akun</label>
                            <select class="form-select @error('type') is-invalid @enderror" id="type" name="type" required>
                                <option value="" disabled>Pilih Tipe Akun</option>
                                <option value="cash" {{ old('type', $account->type) == 'cash' ? 'selected' : '' }}>Kas</option>
                                <option value="bank" {{ old('type', $account->type) == 'bank' ? 'selected' : '' }}>Bank</option>
                                <option value="e-wallet" {{ old('type', $account->type) == 'e-wallet' ? 'selected' : '' }}>E-Wallet</option>
                            </select>
                            @error('type')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="currency" class="form-label">Mata Uang</label>
                            <input type="text" class="form-control @error('currency') is-invalid @enderror" id="currency" name="currency" value="{{ old('currency', $account->currency) }}" required>
                            @error('currency')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="description" class="form-label">Deskripsi (opsional)</label>
                            <textarea class="form-control @error('description') is-invalid @enderror" id="description" name="description" rows="3">{{ old('description', $account->description) }}</textarea>
                            @error('description')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="alert alert-info">
                            <strong>Catatan:</strong> Saldo saat ini adalah <strong>{{ number_format($account->current_balance, 0, ',', '.') }} {{ $account->currency }}</strong>. Untuk mengubah saldo, silakan tambahkan transaksi baru.
                        </div>
                        
                        <div class="d-flex justify-content-between">
                            <a href="{{ route('accounts.index') }}" class="btn btn-secondary">Kembali</a>
                            <button type="submit" class="btn btn-primary">Simpan Perubahan</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

{{-- resources/views/accounts/show.blade.php --}}
@extends('layouts.app')

@section('title', 'Detail Akun')

@section('content')
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>{{ $account->name }}</h1>
        <div>
            <a href="{{ route('transactions.create') }}" class="btn btn-success me-2">Tambah Transaksi</a>
            <a href="{{ route('accounts.index') }}" class="btn btn-secondary">Kembali</a>
        </div>
    </div>
    
    <div class="row mb-4">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Informasi Akun</h5>
                </div>
                <div class="card-body">
                    <table class="table">
                        <tr>
                            <th>Nama Akun</th>
                            <td>{{ $account->name }}</td>
                        </tr>
                        <tr>
                            <th>Tipe</th>
                            <td>
                                @if($account->type == 'cash')
                                    Kas
                                @elseif($account->type == 'bank')
                                    Bank
                                @else
                                    E-Wallet
                                @endif
                            </td>
                        </tr>
                        <tr>
                            <th>Saldo Awal</th>
                            <td>{{ number_format($account->initial_balance, 0, ',', '.') }} {{ $account->currency }}</td>
                        </tr>
                        <tr>
                            <th>Saldo Saat Ini</th>
                            <td>{{ number_format($account->current_balance, 0, ',', '.') }} {{ $account->currency }}</td>
                        </tr>
                        <tr>
                            <th>Mata Uang</th>
                            <td>{{ $account->currency }}</td>
                        </tr>
                        <tr>
                            <th>Deskripsi</th>
                            <td>{{ $account->description ?: '-' }}</td>
                        </tr>
                    </table>
                </div>
                <div class="card-footer">
                    <a href="{{ route('accounts.edit', $account) }}" class="btn btn-warning">Edit Akun</a>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card h-100">
                <div class="card-header">
                    <h5 class="mb-0">Ringkasan Transaksi</h5>
                </div>
                <div class="card-body">
                    <canvas id="transactionChart" height="250"></canvas>
                </div>
            </div>
        </div>
    </div>
    
    <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Riwayat Transaksi</h5>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Tanggal</th>
                            <th>Kategori</th>
                            <th>Jumlah</th>
                            <th>Tipe</th>
                            <th>Deskripsi</th>
                            <th>Aksi</th>
                        </tr>
                    </thead>
                    <tbody>
                        @forelse($transactions as $transaction)
                            <tr>
                                <td>{{ $transaction->transaction_date->format('d/m/Y') }}</td>
                                <td>{{ $transaction->category->name }}</td>
                                <td>{{ number_format($transaction->amount, 0, ',', '.') }} {{ $account->currency }}</td>
                                <td>
                                    @if($transaction->type == 'income')
                                        <span class="badge bg-success">Pendapatan</span>
                                    @elseif($transaction->type == 'expense')
                                        <span class="badge bg-danger">Pengeluaran</span>
                                    @else
                                        <span class="badge bg-info">Transfer</span>
                                    @endif
                                </td>
                                <td>{{ $transaction->description ?: '-' }}</td>
                                <td>
                                    <a href="{{ route('transactions.edit', $transaction) }}" class="btn btn-sm btn-warning">Edit</a>
                                    <form action="{{ route('transactions.destroy', $transaction) }}" method="POST" class="d-inline">
                                        @csrf
                                        @method('DELETE')
                                        <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Anda yakin ingin menghapus transaksi ini?')">Hapus</button>
                                    </form>
                                </td>
                            </tr>
                        @empty
                            <tr>
                                <td colspan="6" class="text-center">Tidak ada data transaksi</td>
                            </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>
            
            <div class="d-flex justify-content-center mt-4">
                {{ $transactions->links() }}
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    // Chart.js untuk ringkasan transaksi
    const ctxTransaction = document.getElementById('transactionChart').getContext('2d');
    
    // Hitung total pendapatan dan pengeluaran
    let totalIncome = 0;
    let totalExpense = 0;
    
    @foreach($transactions as $transaction)
        @if($transaction->type == 'income')
            totalIncome += {{ $transaction->amount }};
        @elseif($transaction->type == 'expense')
            totalExpense += {{ $transaction->amount }};
        @endif
    @endforeach
    
    const transactionChart = new Chart(ctxTransaction, {
        type: 'pie',
        data: {
            labels: ['Pendapatan', 'Pengeluaran'],
            datasets: [{
                data: [totalIncome, totalExpense],
                backgroundColor: ['#28a745', '#dc3545'],
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom',
                }
            }
        }
    });
</script>
@endsection