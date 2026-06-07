{{-- resources/views/dashboard.blade.php --}}
@extends('layouts.app')

@section('title', 'Dashboard')

@section('content')
<div class="container py-4">
    <h1 class="mb-4">Dashboard</h1>
    
    <div class="row mb-4">
        <div class="col-md-4 mb-3">
            <div class="card bg-primary text-white h-100">
                <div class="card-body">
                    <h5 class="card-title">Total Saldo</h5>
                    <h2 class="mt-3">{{ number_format($totalBalance, 0, ',', '.') }}</h2>
                </div>
            </div>
        </div>
        <div class="col-md-4 mb-3">
            <div class="card bg-success text-white h-100">
                <div class="card-body">
                    <h5 class="card-title">Pendapatan Bulan Ini</h5>
                    <h2 class="mt-3">{{ number_format($monthlyIncome, 0, ',', '.') }}</h2>
                </div>
            </div>
        </div>
        <div class="col-md-4 mb-3">
            <div class="card bg-danger text-white h-100">
                <div class="card-body">
                    <h5 class="card-title">Pengeluaran Bulan Ini</h5>
                    <h2 class="mt-3">{{ number_format($monthlyExpense, 0, ',', '.') }}</h2>
                </div>
            </div>
        </div>
    </div>
    
    <div class="row mb-4">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Transaksi Terbaru</h5>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Tanggal</th>
                                    <th>Kategori</th>
                                    <th>Jumlah</th>
                                    <th>Tipe</th>
                                    <th>Akun</th>
                                </tr>
                            </thead>
                            <tbody>
                                @foreach($recentTransactions as $transaction)
                                <tr>
                                    <td>{{ $transaction->transaction_date->format('d/m/Y') }}</td>
                                    <td>{{ $transaction->category->name }}</td>
                                    <td>{{ number_format($transaction->amount, 0, ',', '.') }}</td>
                                    <td>
                                        @if($transaction->type == 'income')
                                            <span class="badge bg-success">Pendapatan</span>
                                        @elseif($transaction->type == 'expense')
                                            <span class="badge bg-danger">Pengeluaran</span>
                                        @else
                                            <span class="badge bg-info">Transfer</span>
                                        @endif
                                    </td>
                                    <td>{{ $transaction->account->name }}</td>
                                </tr>
                                @endforeach
                            </tbody>
                        </table>
                    </div>
                    <a href="{{ route('transactions.index') }}" class="btn btn-sm btn-primary">Lihat Semua Transaksi</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Pengeluaran per Kategori</h5>
                </div>
                <div class="card-body">
                    <canvas id="expenseChart"></canvas>
                </div>
            </div>
        </div>
    </div>
    
    <div class="row">
        <div class="col-md-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Progress Anggaran</h5>
                </div>
                <div class="card-body">
                    @if(count($budgets) > 0)
                        @foreach($budgets as $budget)
                        <div class="mb-3">
                            <div class="d-flex justify-content-between mb-1">
                                <span>{{ $budget->category->name }}</span>
                                <span>{{ number_format($budget->spent, 0, ',', '.') }} / {{ number_format($budget->amount, 0, ',', '.') }}</span>
                            </div>
                            <div class="progress">
                                @php
                                    $progressClass = $budget->percentage <= 50 ? 'bg-success' : ($budget->percentage <= 85 ? 'bg-warning' : 'bg-danger');
                                @endphp
                                <div class="progress-bar {{ $progressClass }}" role="progressbar" style="width: {{ min($budget->percentage, 100) }}%" aria-valuenow="{{ $budget->percentage }}" aria-valuemin="0" aria-valuemax="100"></div>
                            </div>
                        </div>
                        @endforeach
                        <a href="{{ route('budgets.index') }}" class="btn btn-sm btn-primary">Kelola Anggaran</a>
                    @else
                        <p class="text-center">Anda belum membuat anggaran.</p>
                        <div class="text-center">
                            <a href="{{ route('budgets.create') }}" class="btn btn-sm btn-primary">Buat Anggaran</a>
                        </div>
                    @endif
                </div>
            </div>
        </div>
    </div>
</div>
@endsection

@section('scripts')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    // Pie chart for expense by category
    const ctxExpense = document.getElementById('expenseChart').getContext('2d');
    const expenseChart = new Chart(ctxExpense, {
        type: 'pie',
        data: {
            labels: [
                @foreach($categoryBreakdown as $item)
                    '{{ $item->category->name }}',
                @endforeach
            ],
            datasets: [{
                data: [
                    @foreach($categoryBreakdown as $item)
                        {{ $item->total }},
                    @endforeach
                ],
                backgroundColor: [
                    @foreach($categoryBreakdown as $item)
                        '{{ $item->category->color ?? getRandomColor() }}',
                    @endforeach
                ],
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
    
    function getRandomColor() {
        const letters = '0123456789ABCDEF';
        let color = '#';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }
</script>
@endsection