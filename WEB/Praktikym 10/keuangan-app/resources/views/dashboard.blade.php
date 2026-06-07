@extends('layouts.app')

@section('content')
    <h1>Dashboard</h1>
    <div class="row mb-4">
        <div class="col-md-4">
            <div class="card p-3">
                <h5>SALDO</h5>
                <h3>Rp {{ number_format($balance, 0) }}</h3>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card p-3">
                <h5>TOTAL PEMASUKAN</h5>
                <h3>Rp {{ number_format($income, 0) }}</h3>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card p-3">
                <h5>TOTAL PENGELUARAN</h5>
                <h3 class="text-danger">Rp {{ number_format($expense, 0) }}</h3>
            </div>
        </div>
    </div>

    <div class="row mb-4">
        <div class="col-md-8">
            <div class="card p-3">
                <h5>PEMASUKAN VS PENGELUARAN (2025)</h5>
                <canvas id="transactionChart" style="width: 100%; height: 400px;"></canvas>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card p-3">
                <h5>DISTRIBUSI KATEGORI</h5>
                @if (count($categoryLabels) > 0)
                    <canvas id="categoryChart" style="width: 100%; height: 200px;"></canvas>
                @else
                    <p>Belum ada data kategori</p>
                @endif
            </div>
        </div>
    </div>

    <div class="card p-3">
        <h5>TRANSAKSI TERBARU</h5>
        <table class="table">
            <thead>
                <tr>
                    <th>Tanggal</th>
                    <th>Deskripsi</th>
                    <th>Kategori</th>
                    <th>Tipe</th>
                    <th>Jumlah</th>
                </tr>
            </thead>
            <tbody>
                @forelse ($recentTransactions as $transaction)
                    <tr>
                        <td>{{ $transaction->date }}</td>
                        <td>{{ $transaction->description }}</td>
                        <td>{{ $transaction->category ?? '-' }}</td>
                        <td>{{ $transaction->type == 'income' ? 'Pemasukan' : 'Pengeluaran' }}</td>
                        <td>Rp {{ number_format($transaction->amount, 0) }}</td>
                    </tr>
                @empty
                    <tr>
                        <td colspan="5">Belum ada transaksi</td>
                    </tr>
                @endforelse
            </tbody>
        </table>
        <a href="{{ route('transactions.index') }}" class="btn btn-outline-primary">Lihat Semua Transaksi</a>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Grafik Pemasukan vs Pengeluaran
            const ctxTransaction = document.getElementById('transactionChart').getContext('2d');
            if (!ctxTransaction) {
                console.error('Canvas element for transaction chart not found');
                return;
            }
            new Chart(ctxTransaction, {
                type: 'line',
                data: {
                    labels: @json($labels),
                    datasets: [
                        {
                            label: 'Pemasukan',
                            data: @json($incomes),
                            borderColor: 'green',
                            fill: false,
                            tension: 0.1
                        },
                        {
                            label: 'Pengeluaran',
                            data: @json($expenses),
                            borderColor: 'red',
                            fill: false,
                            tension: 0.1
                        }
                    ]
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    return 'Rp ' + value.toLocaleString();
                                }
                            }
                        },
                        x: {
                            title: {
                                display: true,
                                text: 'Bulan'
                            }
                        }
                    }
                }
            });

            // Grafik Distribusi Kategori
            @if (count($categoryLabels) > 0)
                const ctxCategory = document.getElementById('categoryChart').getContext('2d');
                if (!ctxCategory) {
                    console.error('Canvas element for category chart not found');
                    return;
                }
                new Chart(ctxCategory, {
                    type: 'pie',
                    data: {
                        labels: @json($categoryLabels),
                        datasets: [{
                            data: @json($categoryTotals),
                            backgroundColor: @json($categoryColors),
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                position: 'bottom',
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        let label = context.label || '';
                                        if (label) {
                                            label += ': ';
                                        }
                                        label += 'Rp ' + context.raw.toLocaleString();
                                        return label;
                                    }
                                }
                            }
                        }
                    }
                });
            @endif
        });
    </script>
@endsection