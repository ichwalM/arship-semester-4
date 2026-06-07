@extends('layouts.app')

@section('title', 'Dashboard')

@section('actions')
    <a href="{{ route('transactions.create') }}" class="btn btn-sm btn-primary">
        <i class="fas fa-plus"></i> Tambah Transaksi
    </a>
@endsection

@section('content')
<div class="row">
    <!-- Card Saldo -->
    <div class="col-md-4 mb-4">
        <div class="card border-left-primary shadow h-100">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                            Saldo</div>
                        <div class="h3 mb-0 font-weight-bold">
                            Rp {{ number_format($balance, 0, ',', '.') }}
                        </div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-wallet fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Card Pemasukan -->
    <div class="col-md-4 mb-4">
        <div class="card border-left-success shadow h-100">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                            Total Pemasukan</div>
                        <div class="h3 mb-0 font-weight-bold text-income">
                            Rp {{ number_format($totalIncome, 0, ',', '.') }}
                        </div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Card Pengeluaran -->
    <div class="col-md-4 mb-4">
        <div class="card border-left-danger shadow h-100">
            <div class="card-body">
                <div class="row no-gutters align-items-center">
                    <div class="col mr-2">
                        <div class="text-xs font-weight-bold text-danger text-uppercase mb-1">
                            Total Pengeluaran</div>
                        <div class="h3 mb-0 font-weight-bold text-expense">
                            Rp {{ number_format($totalExpense, 0, ',', '.') }}
                        </div>
                    </div>
                    <div class="col-auto">
                        <i class="fas fa-receipt fa-2x text-gray-300"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <!-- Chart Pemasukan vs Pengeluaran -->
    <div class="col-xl-8 col-lg-7">
        <div class="card shadow mb-4">
            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                <h6 class="m-0 font-weight-bold text-primary">Pemasukan vs Pengeluaran ({{ date('Y') }})</h6>
            </div>
            <div class="card-body">
                <div class="chart-area">
                    <canvas id="incomeExpenseChart" style="min-height: 300px;"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- Pie Chart Kategori Pengeluaran -->
    <div class="col-xl-4 col-lg-5">
        <div class="card shadow mb-4">
            <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                <h6 class="m-0 font-weight-bold text-primary">Distribusi Kategori</h6>
            </div>
            <div class="card-body">
                <div class="chart-pie">
                    <canvas id="categoryPieChart" style="min-height: 300px;"></canvas>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Transaksi Terbaru -->
<div class="card shadow mb-4">
    <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">Transaksi Terbaru</h6>
    </div>
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-bordered table-hover" width="100%" cellspacing="0">
                <thead class="bg-light">
                    <tr>
                        <th>Tanggal</th>
                        <th>Deskripsi</th>
                        <th>Kategori</th>
                        <th>Tipe</th>
                        <th>Jumlah</th>
                    </tr>
                </thead>
                <tbody>
                    @forelse($recentTransactions as $transaction)
                    <tr>
                        <td>{{ $transaction->transaction_date->format('d/m/Y') }}</td>
                        <td>{{ $transaction->description }}</td>
                        <td>
                            <span class="badge bg-secondary">{{ ucfirst($transaction->category) }}</span>
                        </td>
                        <td>
                            @if($transaction->type == 'income')
                                <span class="badge bg-success">Pemasukan</span>
                            @else
                                <span class="badge bg-danger">Pengeluaran</span>
                            @endif
                        </td>
                        <td class="{{ $transaction->type == 'income' ? 'text-income' : 'text-expense' }}">
                            Rp {{ number_format($transaction->amount, 0, ',', '.') }}
                        </td>
                    </tr>
                    @empty
                    <tr>
                        <td colspan="5" class="text-center">Belum ada transaksi</td>
                    </tr>
                    @endforelse
                </tbody>
            </table>
        </div>
        <div class="text-center mt-3">
            <a href="{{ route('transactions.index') }}" class="btn btn-outline-primary">
                Lihat Semua Transaksi
            </a>
        </div>
    </div>
</div>
@endsection

@push('scripts')
<script>
    // Chart Pemasukan vs Pengeluaran bulanan
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'Mei', 'Jun', 'Jul', 'Agt', 'Sep', 'Okt', 'Nov', 'Des'];
    const incomeData = {{ json_encode($incomeData) }};
    const expenseData = {{ json_encode($expenseData) }};
    
    const incomeExpenseChart = new Chart(
        document.getElementById('incomeExpenseChart').getContext('2d'), 
        {
            type: 'line',
            data: {
                labels: months,
                datasets: [
                    {
                        label: 'Pemasukan',
                        data: incomeData,
                        borderColor: '#28a745',
                        backgroundColor: 'rgba(40, 167, 69, 0.1)',
                        borderWidth: 2,
                        fill: true,
                        tension: 0.4
                    },
                    {
                        label: 'Pengeluaran',
                        data: expenseData,
                        borderColor: '#dc3545',
                        backgroundColor: 'rgba(220, 53, 69, 0.1)',
                        borderWidth: 2,
                        fill: true,
                        tension: 0.4
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'top',
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            drawBorder: false
                        },
                        ticks: {
                            callback: function(value) {
                                return 'Rp ' + value.toLocaleString('id-ID');
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        }
    );
    
    // Chart Distribusi Kategori
    const categoryData = @json($categoryStats);
    const labels = categoryData.map(item => item.category);
    const data = categoryData.map(item => item.total);
    
    const backgroundColors = [
        '#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b', 
        '#5a5c69', '#858796', '#6f42c1', '#20c9a6', '#e83e8c'
    ];
    
    const categoryPieChart = new Chart(
        document.getElementById('categoryPieChart').getContext('2d'),
        {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: backgroundColors.slice(0, labels.length),
                    hoverBackgroundColor: backgroundColors.slice(0, labels.length),
                    hoverBorderColor: "rgba(234, 236, 244, 1)",
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const label = context.label || '';
                                const value = context.raw;
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = Math.round((value / total) * 100);
                                return `${label}: Rp ${value.toLocaleString('id-ID')} (${percentage}%)`;
                            }
                        }
                    }
                },
                cutout: '70%'
            }
        }
    );
</script>
@endpush