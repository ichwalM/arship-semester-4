{{-- resources/views/reports/index.blade.php --}}
@extends('layouts.app')

@section('title', 'Laporan Keuangan')

@section('content')
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Laporan Keuangan</h1>
        <div class="btn-group">
            <a href="{{ route('reports.cash-flow') }}" class="btn btn-info me-2">
                <i class="fas fa-chart-line me-1"></i> Arus Kas
            </a>
            <a href="{{ route('reports.export-pdf', ['period' => $period, 'date' => $date]) }}" class="btn btn-warning">
                <i class="fas fa-file-pdf me-1"></i> Export PDF
            </a>
        </div>
    </div>
    
    <div class="card mb-4">
        <div class="card-header">
            <h5 class="mb-0">Filter Laporan</h5>
        </div>
        <div class="card-body">
            <form action="{{ route('reports.index') }}" method="GET">
                <div class="row align-items-end">
                    <div class="col-md-3 mb-3">
                        <label for="period" class="form-label">Tipe Periode</label>
                        <select class="form-select" id="period" name="period">
                            <option value="monthly" {{ $period == 'monthly' ? 'selected' : '' }}>Bulanan</option>
                            <option value="yearly" {{ $period == 'yearly' ? 'selected' : '' }}>Tahunan</option>
                        </select>
                    </div>
                    <div class="col-md-3 mb-3">
                        <label for="date" class="form-label" id="date-label">Bulan</label>
                        <input type="{{ $period == 'monthly' ? 'month' : 'number' }}" class="form-control" id="date" name="date" value="{{ $date }}">
                    </div>
                    <div class="col-md-2 mb-3">
                        <button type="submit" class="btn btn-primary">Tampilkan</button>
                    </div>
                    <div class="col-md-4 mb-3 text-md-end">
                        <div class="btn-group">
                            <a href="{{ route('reports.index', ['period' => $period, 'date' => $previousPeriod]) }}" class="btn btn-outline-secondary">
                                <i class="fas fa-chevron-left"></i>
                            </a>
                            <button type="button" class="btn btn-outline-secondary disabled">
                                {{ $periodLabel }}
                            </button>
                            <a href="{{ route('reports.index', ['period' => $period, 'date' => $nextPeriod]) }}" class="btn btn-outline-secondary">
                                <i class="fas fa-chevron-right"></i>
                            </a>
                        </div>
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
            <div class="card @if($balance >= 0) bg-primary @else bg-warning @endif text-white h-100">
                <div class="card-body">
                    <h5 class="card-title">Saldo</h5>
                    <h2 class="mt-3">{{ number_format($balance, 0, ',', '.') }}</h2>
                </div>
            </div>
        </div>
    </div>
    
    <div class="row mb-4">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Grafik Pendapatan dan Pengeluaran</h5>
                </div>
                <div class="card-body">
                    <canvas id="incomeExpenseChart" height="300"></canvas>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Pendapatan per Kategori</h5>
                </div>
                <div class="card-body">
                    <canvas id="incomePieChart" height="200"></canvas>
                </div>
            </div>
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Pengeluaran per Kategori</h5>
                </div>
                <div class="card-body">
                    <canvas id="expensePieChart" height="200"></canvas>
                </div>
            </div>
        </div>
    </div>
    
    <div class="row mb-4">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Ringkasan per Akun</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Akun</th>
                                    <th>Tipe</th>
                                    <th>Pendapatan</th>
                                    <th>Pengeluaran</th>
                                    <th>Saldo</th>
                                </tr>
                            </thead>
                            <tbody>
                                @forelse($accountSummary as $summary)
                                    <tr>
                                        <td>{{ $summary['account']->name }}</td>
                                        <td>
                                            @if($summary['account']->type == 'cash')
                                                <span class="badge bg-secondary">Kas</span>
                                            @elseif($summary['account']->type == 'bank')
                                                <span class="badge bg-primary">Bank</span>
                                            @else
                                                <span class="badge bg-info">E-Wallet</span>
                                            @endif
                                        </td>
                                        <td class="text-success">{{ number_format($summary['income'], 0, ',', '.') }}</td>
                                        <td class="text-danger">{{ number_format($summary['expense'], 0, ',', '.') }}</td>
                                        <td class="{{ $summary['balance'] >= 0 ? 'text-success' : 'text-danger' }}">
                                            {{ number_format($summary['balance'], 0, ',', '.') }}
                                        </td>
                                    </tr>
                                @empty
                                    <tr>
                                        <td colspan="5" class="text-center">Tidak ada data transaksi untuk periode ini</td>
                                    </tr>
                                @endforelse
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <div class="row">
        <div class="col-md-6 mb-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Detail Pendapatan</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Kategori</th>
                                    <th>Jumlah</th>
                                    <th>Persentase</th>
                                </tr>
                            </thead>
                            <tbody>
                                @forelse($incomeByCategory as $income)
                                    <tr>
                                        <td>{{ $income->category->name }}</td>
                                        <td>{{ number_format($income->total, 0, ',', '.') }}</td>
                                        <td>{{ number_format(($income->total / $totalIncome) * 100, 1) }}%</td>
                                    </tr>
                                @empty
                                    <tr>
                                        <td colspan="3" class="text-center">Tidak ada data pendapatan untuk periode ini</td>
                                    </tr>
                                @endforelse
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6 mb-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Detail Pengeluaran</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Kategori</th>
                                    <th>Jumlah</th>
                                    <th>Persentase</th>
                                </tr>
                            </thead>
                            <tbody>
                                @forelse($expenseByCategory as $expense)
                                    <tr>
                                        <td>{{ $expense->category->name }}</td>
                                        <td>{{ number_format($expense->total, 0, ',', '.') }}</td>
                                        <td>{{ number_format(($expense->total / $totalExpense) * 100, 1) }}%</td>
                                    </tr>
                                @empty
                                    <tr>
                                        <td colspan="3" class="text-center">Tidak ada data pengeluaran untuk periode ini</td>
                                    </tr>
                                @endforelse
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    // Update label berdasarkan tipe periode
    document.addEventListener('DOMContentLoaded', function() {
        const periodSelect = document.getElementById('period');
        const dateInput = document.getElementById('date');
        const dateLabel = document.getElementById('date-label');
        
        periodSelect.addEventListener('change', function() {
            if (this.value === 'monthly') {
                dateLabel.textContent = 'Bulan';
                dateInput.type = 'month';
                
                // Set nilai default untuk bulan
                const now = new Date();
                const year = now.getFullYear();
                const month = (now.getMonth() + 1).toString().padStart(2, '0');
                dateInput.value = `${year}-${month}`;
            } else {
                dateLabel.textContent = 'Tahun';
                dateInput.type = 'number';
                
                // Set nilai default untuk tahun
                dateInput.value = new Date().getFullYear();
            }
        });
    });
    
    // Chart Pendapatan dan Pengeluaran
    const ctxIncomeExpense = document.getElementById('incomeExpenseChart').getContext('2d');
    
    const incomeExpenseChart = new Chart(ctxIncomeExpense, {
        type: 'bar',
        data: {
            labels: {!! json_encode($labels) !!},
            datasets: [
                {
                    label: 'Pendapatan',
                    data: {!! json_encode($incomeData) !!},
                    backgroundColor: 'rgba(40, 167, 69, 0.7)',
                    borderColor: 'rgba(40, 167, 69, 1)',
                    borderWidth: 1
                },
                {
                    label: 'Pengeluaran',
                    data: {!! json_encode($expenseData) !!},
                    backgroundColor: 'rgba(220, 53, 69, 0.7)',
                    borderColor: 'rgba(220, 53, 69, 1)',
                    borderWidth: 1
                }
            ]
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
    
    // Chart Pie untuk Pendapatan per Kategori
    const ctxIncomePie = document.getElementById('incomePieChart').getContext('2d');
    
    const incomeLabels = [];
    const incomeData = [];
    const incomeColors = [];
    
    @foreach($incomeByCategory as $income)
        incomeLabels.push('{{ $income->category->name }}');
        incomeData.push({{ $income->total }});
        incomeColors.push('{{ $income->category->color ?? getRandomColorJS() }}');
    @endforeach
    
    const incomePieChart = new Chart(ctxIncomePie, {
        type: 'pie',
        data: {
            labels: incomeLabels,
            datasets: [{
                data: incomeData,
                backgroundColor: incomeColors
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    display: false
                }
            }
        }
    });
    
    // Chart Pie untuk Pengeluaran per Kategori
    const ctxExpensePie = document.getElementById('expensePieChart').getContext('2d');
    
    const expenseLabels = [];
    const expenseData = [];
    const expenseColors = [];
    
    @foreach($expenseByCategory as $expense)
        expenseLabels.push('{{ $expense->category->name }}');
        expenseData.push({{ $expense->total }});
        expenseColors.push('{{ $expense->category->color ?? getRandomColorJS() }}');
    @endforeach
    
    const expensePieChart = new Chart(ctxExpensePie, {
        type: 'pie',
        data: {
            labels: expenseLabels,
            datasets: [{
                data: expenseData,
                backgroundColor: expenseColors
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    display: false
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

{{-- resources/views/reports/cash-flow.blade.php --}}
@extends('layouts.app')

@section('title', 'Laporan Arus Kas')

@section('content')
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Laporan Arus Kas</h1>
        <div>
            <a href="{{ route('reports.index') }}" class="btn btn-secondary">Kembali</a>
        </div>
    </div>
    
    <div class="card mb-4">
        <div class="card-header">
            <h5 class="mb-0">Filter Periode</h5>
        </div>
        <div class="card-body">
            <form action="{{ route('reports.cash-flow') }}" method="GET">
                <div class="row align-items-end">
                    <div class="col-md-4 mb-3">
                        <label for="start_date" class="form-label">Dari Tanggal</label>
                        <input type="date" class="form-control" id="start_date" name="start_date" value="{{ $startDate }}">
                    </div>
                    <div class="col-md-4 mb-3">
                        <label for="end_date" class="form-label">Sampai Tanggal</label>
                        <input type="date" class="form-control" id="end_date" name="end_date" value="{{ $endDate }}">
                    </div>
                    <div class="col-md-4 mb-3">
                        <button type="submit" class="btn btn-primary">Tampilkan</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
    
    <div class="row mb-4">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Tren Arus Kas</h5>
                </div>
                <div class="card-body">
                    <canvas id="cashFlowChart" height="300"></canvas>
                </div>
            </div>
        </div>
    </div>
    
    <div class="row mb-4">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Neraca Keuangan Bulanan</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Bulan</th>
                                    <th>Pendapatan</th>
                                    <th>Pengeluaran</th>
                                    <th>Saldo</th>
                                </tr>
                            </thead>
                            <tbody>
                                @for($i = 0; $i < count($months); $i++)
                                    <tr>
                                        <td>{{ $months[$i] }}</td>
                                        <td class="text-success">{{ number_format($incomeData[$i], 0, ',', '.') }}</td>
                                        <td class="text-danger">{{ number_format($expenseData[$i], 0, ',', '.') }}</td>
                                        <td class="{{ $balanceData[$i] >= 0 ? 'text-success' : 'text-danger' }}">
                                            {{ number_format($balanceData[$i], 0, ',', '.') }}
                                        </td>
                                    </tr>
                                @endfor
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Kekayaan Bersih</h5>
                </div>
                <div class="card-body">
                    <div class="text-center mb-4">
                        <h1>{{ number_format($totalNetWorth, 0, ',', '.') }}</h1>
                        <p class="lead">Total Kekayaan Bersih</p>
                    </div>
                    
                    <h5 class="border-bottom pb-2 mb-3">Saldo per Akun</h5>
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Akun</th>
                                    <th>Saldo</th>
                                </tr>
                            </thead>
                            <tbody>
                                @foreach($accountBalances as $account)
                                    <tr>
                                        <td>{{ $account['name'] }}</td>
                                        <td>{{ number_format($account['balance'], 0, ',', '.') }}</td>
                                    </tr>
                                @endforeach
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    // Chart untuk tren arus kas
    const ctxCashFlow = document.getElementById('cashFlowChart').getContext('2d');
    
    const cashFlowChart = new Chart(ctxCashFlow, {
        type: 'line',
        data: {
            labels: {!! json_encode($months) !!},
            datasets: [
                {
                    label: 'Pendapatan',
                    data: {!! json_encode($incomeData) !!},
                    borderColor: 'rgba(40, 167, 69, 1)',
                    backgroundColor: 'rgba(40, 167, 69, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4
                },
                {
                    label: 'Pengeluaran',
                    data: {!! json_encode($expenseData) !!},
                    borderColor: 'rgba(220, 53, 69, 1)',
                    backgroundColor: 'rgba(220, 53, 69, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4
                },
                {
                    label: 'Saldo',
                    data: {!! json_encode($balanceData) !!},
                    borderColor: 'rgba(0, 123, 255, 1)',
                    backgroundColor: 'rgba(0, 123, 255, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4
                }
            ]
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
</script>
@endsection