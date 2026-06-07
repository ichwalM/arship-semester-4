{{-- resources/views/budgets/index.blade.php --}}
@extends('layouts.app')

@section('title', 'Daftar Anggaran')

@section('content')
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Daftar Anggaran</h1>
        <div>
            <a href="{{ route('budgets.report') }}" class="btn btn-info me-2">
                <i class="fas fa-chart-line me-1"></i> Laporan Anggaran
            </a>
            <a href="{{ route('budgets.create') }}" class="btn btn-primary">
                <i class="fas fa-plus me-1"></i> Tambah Anggaran
            </a>
        </div>
    </div>
    
    @if(session('success'))
        <div class="alert alert-success">
            {{ session('success') }}
        </div>
    @endif
    
    <div class="row">
        @forelse($budgets as $budget)
            <div class="col-md-4 mb-4">
                <div class="card h-100">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">{{ $budget->category->name }}</h5>
                        <span class="badge bg-primary">{{ $budget->period_type == 'monthly' ? 'Bulanan' : 'Tahunan' }}</span>
                    </div>
                    <div class="card-body">
                        <div class="d-flex justify-content-between mb-2">
                            <span>Anggaran:</span>
                            <span><strong>{{ number_format($budget->amount, 0, ',', '.') }}</strong></span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Terpakai:</span>
                            <span><strong>{{ number_format($budget->spent, 0, ',', '.') }}</strong></span>
                        </div>
                        <div class="d-flex justify-content-between mb-3">
                            <span>Sisa:</span>
                            <span><strong>{{ number_format(max(0, $budget->amount - $budget->spent), 0, ',', '.') }}</strong></span>
                        </div>
                        
                        <div class="progress mb-3">
                            @php
                                $progressClass = $budget->percentage <= 50 ? 'bg-success' : ($budget->percentage <= 85 ? 'bg-warning' : 'bg-danger');
                            @endphp
                            <div class="progress-bar {{ $progressClass }}" role="progressbar" style="width: {{ min($budget->percentage, 100) }}%" aria-valuenow="{{ $budget->percentage }}" aria-valuemin="0" aria-valuemax="100"></div>
                        </div>
                        
                        <div class="text-center">
                            <h5>{{ number_format($budget->percentage, 1) }}% Terpakai</h5>
                        </div>
                        
                        <p class="text-muted mt-3">
                            Periode: {{ $budget->start_date->format('d M Y') }} - {{ $budget->end_date->format('d M Y') }}
                        </p>
                        
                        @if($budget->description)
                            <p class="text-muted">{{ $budget->description }}</p>
                        @endif
                    </div>
                    <div class="card-footer d-flex justify-content-between">
                        <a href="{{ route('budgets.show', $budget) }}" class="btn btn-sm btn-info">Detail</a>
                        <div>
                            <a href="{{ route('budgets.edit', $budget) }}" class="btn btn-sm btn-warning">Edit</a>
                            <form action="{{ route('budgets.destroy', $budget) }}" method="POST" class="d-inline">
                                @csrf
                                @method('DELETE')
                                <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Anda yakin ingin menghapus anggaran ini?')">Hapus</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        @empty
            <div class="col-12">
                <div class="alert alert-info">
                    Anda belum memiliki anggaran. Silakan buat anggaran baru.
                </div>
            </div>
        @endforelse
    </div>
</div>
@endsection

{{-- resources/views/budgets/create.blade.php --}}
@extends('layouts.app')

@section('title', 'Tambah Anggaran')

@section('content')
<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Tambah Anggaran</h5>
                </div>
                <div class="card-body">
                    <form action="{{ route('budgets.store') }}" method="POST">
                        @csrf
                        
                        <div class="mb-3">
                            <label for="category_id" class="form-label">Kategori</label>
                            <select class="form-select @error('category_id') is-invalid @enderror" id="category_id" name="category_id" required>
                                <option value="" selected disabled>Pilih Kategori</option>
                                @foreach($categories as $category)
                                    <option value="{{ $category->id }}" {{ old('category_id') == $category->id ? 'selected' : '' }}>{{ $category->name }}</option>
                                @endforeach
                            </select>
                            @error('category_id')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="amount" class="form-label">Jumlah Anggaran</label>
                            <div class="input-group">
                                <span class="input-group-text">Rp</span>
                                <input type="number" step="0.01" class="form-control @error('amount') is-invalid @enderror" id="amount" name="amount" value="{{ old('amount') }}" required>
                                @error('amount')
                                    <div class="invalid-feedback">{{ $message }}</div>
                                @enderror
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="period_type" class="form-label">Tipe Periode</label>
                            <select class="form-select @error('period_type') is-invalid @enderror" id="period_type" name="period_type" required>
                                <option value="" selected disabled>Pilih Tipe Periode</option>
                                <option value="monthly" {{ old('period_type') == 'monthly' ? 'selected' : '' }}>Bulanan</option>
                                <option value="yearly" {{ old('period_type') == 'yearly' ? 'selected' : '' }}>Tahunan</option>
                            </select>
                            @error('period_type')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="start_date" class="form-label">Tanggal Mulai</label>
                                <input type="date" class="form-control @error('start_date') is-invalid @enderror" id="start_date" name="start_date" value="{{ old('start_date', date('Y-m-01')) }}" required>
                                @error('start_date')
                                    <div class="invalid-feedback">{{ $message }}</div>
                                @enderror
                            </div>
                            <div class="col-md-6">
                                <label for="end_date" class="form-label">Tanggal Berakhir</label>
                                <input type="date" class="form-control @error('end_date') is-invalid @enderror" id="end_date" name="end_date" value="{{ old('end_date', date('Y-m-t')) }}" required>
                                @error('end_date')
                                    <div class="invalid-feedback">{{ $message }}</div>
                                @enderror
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="description" class="form-label">Deskripsi (opsional)</label>
                            <textarea class="form-control @error('description') is-invalid @enderror" id="description" name="description" rows="3">{{ old('description') }}</textarea>
                            @error('description')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="d-flex justify-content-between">
                            <a href="{{ route('budgets.index') }}" class="btn btn-secondary">Kembali</a>
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
    // Set tanggal default berdasarkan tipe periode
    document.addEventListener('DOMContentLoaded', function() {
        const periodTypeSelect = document.getElementById('period_type');
        const startDateInput = document.getElementById('start_date');
        const endDateInput = document.getElementById('end_date');
        
        periodTypeSelect.addEventListener('change', function() {
            const now = new Date();
            const year = now.getFullYear();
            const month = (now.getMonth() + 1).toString().padStart(2, '0');
            
            if (this.value === 'monthly') {
                // Atur untuk bulanan (awal sampai akhir bulan ini)
                startDateInput.value = `${year}-${month}-01`;
                
                // Hitung hari terakhir bulan ini
                const lastDay = new Date(year, now.getMonth() + 1, 0).getDate();
                endDateInput.value = `${year}-${month}-${lastDay}`;
            } else if (this.value === 'yearly') {
                // Atur untuk tahunan (awal sampai akhir tahun ini)
                startDateInput.value = `${year}-01-01`;
                endDateInput.value = `${year}-12-31`;
            }
        });
    });
</script>
@endsection

{{-- resources/views/budgets/edit.blade.php --}}
@extends('layouts.app')

@section('title', 'Edit Anggaran')

@section('content')
<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Edit Anggaran</h5>
                </div>
                <div class="card-body">
                    <form action="{{ route('budgets.update', $budget) }}" method="POST">
                        @csrf
                        @method('PUT')
                        
                        <div class="mb-3">
                            <label for="category_id" class="form-label">Kategori</label>
                            <select class="form-select @error('category_id') is-invalid @enderror" id="category_id" name="category_id" required>
                                <option value="" disabled>Pilih Kategori</option>
                                @foreach($categories as $category)
                                    <option value="{{ $category->id }}" {{ old('category_id', $budget->category_id) == $category->id ? 'selected' : '' }}>{{ $category->name }}</option>
                                @endforeach
                            </select>
                            @error('category_id')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="mb-3">
                            <label for="amount" class="form-label">Jumlah Anggaran</label>
                            <div class="input-group">
                                <span class="input-group-text">Rp</span>
                                <input type="number" step="0.01" class="form-control @error('amount') is-invalid @enderror" id="amount" name="amount" value="{{ old('amount', $budget->amount) }}" required>
                                @error('amount')
                                    <div class="invalid-feedback">{{ $message }}</div>
                                @enderror
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="period_type" class="form-label">Tipe Periode</label>
                            <select class="form-select @error('period_type') is-invalid @enderror" id="period_type" name="period_type" required>
                                <option value="" disabled>Pilih Tipe Periode</option>
                                <option value="monthly" {{ old('period_type', $budget->period_type) == 'monthly' ? 'selected' : '' }}>Bulanan</option>
                                <option value="yearly" {{ old('period_type', $budget->period_type) == 'yearly' ? 'selected' : '' }}>Tahunan</option>
                            </select>
                            @error('period_type')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="start_date" class="form-label">Tanggal Mulai</label>
                                <input type="date" class="form-control @error('start_date') is-invalid @enderror" id="start_date" name="start_date" value="{{ old('start_date', $budget->start_date->format('Y-m-d')) }}" required>
                                @error('start_date')
                                    <div class="invalid-feedback">{{ $message }}</div>
                                @enderror
                            </div>
                            <div class="col-md-6">
                                <label for="end_date" class="form-label">Tanggal Berakhir</label>
                                <input type="date" class="form-control @error('end_date') is-invalid @enderror" id="end_date" name="end_date" value="{{ old('end_date', $budget->end_date->format('Y-m-d')) }}" required>
                                @error('end_date')
                                    <div class="invalid-feedback">{{ $message }}</div>
                                @enderror
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="description" class="form-label">Deskripsi (opsional)</label>
                            <textarea class="form-control @error('description') is-invalid @enderror" id="description" name="description" rows="3">{{ old('description', $budget->description) }}</textarea>
                            @error('description')
                                <div class="invalid-feedback">{{ $message }}</div>
                            @enderror
                        </div>
                        
                        <div class="d-flex justify-content-between">
                            <a href="{{ route('budgets.index') }}" class="btn btn-secondary">Kembali</a>
                            <button type="submit" class="btn btn-primary">Simpan Perubahan</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

{{-- resources/views/budgets/show.blade.php --}}
@extends('layouts.app')

@section('title', 'Detail Anggaran')

@section('content')
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Detail Anggaran: {{ $budget->category->name }}</h1>
        <div>
            <a href="{{ route('budgets.edit', $budget) }}" class="btn btn-warning me-2">Edit</a>
            <a href="{{ route('budgets.index') }}" class="btn btn-secondary">Kembali</a>
        </div>
    </div>
    
    <div class="row mb-4">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Informasi Anggaran</h5>
                </div>
                <div class="card-body">
                    <table class="table">
                        <tr>
                            <th>Kategori</th>
                            <td>{{ $budget->category->name }}</td>
                        </tr>
                        <tr>
                            <th>Tipe Periode</th>
                            <td>{{ $budget->period_type == 'monthly' ? 'Bulanan' : 'Tahunan' }}</td>
                        </tr>
                        <tr>
                            <th>Periode</th>
                            <td>{{ $budget->start_date->format('d M Y') }} - {{ $budget->end_date->format('d M Y') }}</td>
                        </tr>
                        <tr>
                            <th>Jumlah Anggaran</th>
                            <td>{{ number_format($budget->amount, 0, ',', '.') }}</td>
                        </tr>
                        <tr>
                            <th>Jumlah Terpakai</th>
                            <td>{{ number_format($spent, 0, ',', '.') }}</td>
                        </tr>
                        <tr>
                            <th>Sisa Anggaran</th>
                            <td>{{ number_format(max(0, $budget->amount - $spent), 0, ',', '.') }}</td>
                        </tr>
                        <tr>
                            <th>Persentase Terpakai</th>
                            <td>{{ number_format($percentage, 1) }}%</td>
                        </tr>
                        <tr>
                            <th>Deskripsi</th>
                            <td>{{ $budget->description ?: '-' }}</td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card h-100">
                <div class="card-header">
                    <h5 class="mb-0">Statistik Penggunaan</h5>
                </div>
                <div class="card-body">
                    <div class="mb-4">
                        <h5 class="text-center mb-3">Progress Anggaran</h5>
                        <div class="progress" style="height: 25px;">
                            @php
                                $progressClass = $percentage <= 50 ? 'bg-success' : ($percentage <= 85 ? 'bg-warning' : 'bg-danger');
                            @endphp
                            <div class="progress-bar {{ $progressClass }}" role="progressbar" style="width: {{ min($percentage, 100) }}%" aria-valuenow="{{ $percentage }}" aria-valuemin="0" aria-valuemax="100">
                                {{ number_format($percentage, 1) }}%
                            </div>
                        </div>
                    </div>
                    
                    <div class="text-center">
                        <div class="mb-3">
                            <canvas id="budgetPieChart" height="200"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <div class="card">
        <div class="card-header">
            <h5 class="mb-0">Riwayat Pengeluaran</h5>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Tanggal</th>
                            <th>Deskripsi</th>
                            <th>Akun</th>
                            <th>Jumlah</th>
                            <th>Aksi</th>
                        </tr>
                    </thead>
                    <tbody>
                        @forelse($transactions as $transaction)
                            <tr>
                                <td>{{ $transaction->transaction_date->format('d/m/Y') }}</td>
                                <td>{{ $transaction->description ?: '-' }}</td>
                                <td>{{ $transaction->account->name }}</td>
                                <td>{{ number_format($transaction->amount, 0, ',', '.') }}</td>
                                <td>
                                    <a href="{{ route('transactions.edit', $transaction) }}" class="btn btn-sm btn-warning">Edit</a>
                                </td>
                            </tr>
                        @empty
                            <tr>
                                <td colspan="5" class="text-center">Belum ada transaksi untuk anggaran ini</td>
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
    // Chart untuk statistik anggaran
    const ctxPie = document.getElementById('budgetPieChart').getContext('2d');
    
    const remaining = Math.max(0, {{ $budget->amount - $spent }});
    const spent = {{ $spent }};
    
    const budgetPieChart = new Chart(ctxPie, {
        type: 'pie',
        data: {
            labels: ['Terpakai', 'Tersisa'],
            datasets: [{
                data: [spent, remaining],
                backgroundColor: ['#dc3545', '#28a745'],
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

{{-- resources/views/budgets/report.blade.php --}}
@extends('layouts.app')

@section('title', 'Laporan Anggaran')

@section('content')
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Laporan Anggaran</h1>
        <div>
            <a href="{{ route('budgets.index') }}" class="btn btn-secondary">Kembali</a>
        </div>
    </div>
    
    <div class="card mb-4">
        <div class="card-header">
            <h5 class="mb-0">Periode</h5>
        </div>
        <div class="card-body">
            <form action="{{ route('budgets.report') }}" method="GET">
                <div class="row align-items-end">
                    <div class="col-md-4 mb-3">
                        <label for="month" class="form-label">Pilih Bulan</label>
                        <input type="month" class="form-control" id="month" name="month" value="{{ $month }}">
                    </div>
                    <div class="col-md-4 mb-3">
                        <button type="submit" class="btn btn-primary">Tampilkan</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
    
    <div class="row mb-4">
        <div class="col-md-4 mb-3">
            <div class="card bg-success text-white h-100">
                <div class="card-body">
                    <h5 class="card-title">Total Pendapatan</h5>
                    <h2 class="mt-3">{{ number_format($totalIncome, 0, ',', '.') }}</h2>
                </div>
            </div>
        </div>
        <div class="col-md-4 mb-3">
            <div class="card bg-danger text-white h-100">
                <div class="card-body">
                    <h5 class="card-title">Total Pengeluaran</h5>
                    <h2 class="mt-3">{{ number_format($totalExpense, 0, ',', '.') }}</h2>
                </div>
            </div>
        </div>
        <div class="col-md-4 mb-3">
            <div class="card @if($totalIncome - $totalExpense >= 0) bg-primary @else bg-warning @endif text-white h-100">
                <div class="card-body">
                    <h5 class="card-title">Saldo</h5>
                    <h2 class="mt-3">{{ number_format($totalIncome - $totalExpense, 0, ',', '.') }}</h2>
                </div>
            </div>
        </div>
    </div>
    
    <div class="row mb-4">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Pengeluaran Harian</h5>
                </div>
                <div class="card-body">
                    <canvas id="dailyExpenseChart" height="300"></canvas>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Pengeluaran per Kategori</h5>
                </div>
                <div class="card-body">
                    <canvas id="categoryPieChart" height="300"></canvas>
                </div>
            </div>
        </div>
    </div>
    
    <div class="card">
        <div class="card-header">
            <h5 class="mb-0">Daftar Anggaran Bulan Ini</h5>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Kategori</th>
                            <th>Anggaran</th>
                            <th>Terpakai</th>
                            <th>Tersisa</th>
                            <th>Progress</th>
                        </tr>
                    </thead>
                    <tbody>
                        @forelse($allBudgets as $budget)
                            <tr>
                                <td>{{ $budget->category->name }}</td>
                                <td>{{ number_format($budget->amount, 0, ',', '.') }}</td>
                                <td>{{ number_format($budget->spent, 0, ',', '.') }}</td>
                                <td>{{ number_format($budget->remaining, 0, ',', '.') }}</td>
                                <td>
                                    <div class="progress">
                                        @php
                                            $progressClass = $budget->percentage <= 50 ? 'bg-success' : ($budget->percentage <= 85 ? 'bg-warning' : 'bg-danger');
                                        @endphp
                                        <div class="progress-bar {{ $progressClass }}" role="progressbar" style="width: {{ min($budget->percentage, 100) }}%" aria-valuenow="{{ $budget->percentage }}" aria-valuemin="0" aria-valuemax="100">
                                            {{ number_format($budget->percentage, 1) }}%
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        @empty
                            <tr>
                                <td colspan="5" class="text-center">Belum ada anggaran untuk bulan ini</td>
                            </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    // Chart untuk pengeluaran harian
    const ctxDaily = document.getElementById('dailyExpenseChart').getContext('2d');
    
    const dailyExpenseChart = new Chart(ctxDaily, {
        type: 'bar',
        data: {
            labels: {!! json_encode($days) !!},
            datasets: [{
                label: 'Pengeluaran Harian',
                data: {!! json_encode($amounts) !!},
                backgroundColor: 'rgba(220, 53, 69, 0.7)',
                borderColor: 'rgba(220, 53, 69, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
    
    // Chart untuk kategori
    const ctxCategory = document.getElementById('categoryPieChart').getContext('2d');
    
    const categoryLabels = [];
    const categoryData = [];
    const categoryColors = [];
    
    @foreach($categoryBreakdown as $item)
        categoryLabels.push('{{ $item->category->name }}');
        categoryData.push({{ $item->total }});
        categoryColors.push('{{ $item->category->color ?? getRandomColorJS() }}');
    @endforeach
    
    const categoryPieChart = new Chart(ctxCategory, {
        type: 'pie',
        data: {
            labels: categoryLabels,
            datasets: [{
                data: categoryData,
                backgroundColor: categoryColors
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
    
    function getRandomColorJS() {
        const letters = '0123456789ABCDEF';
        let color = '#';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }
</script>
@endsection