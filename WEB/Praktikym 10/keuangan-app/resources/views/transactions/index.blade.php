@extends('layouts.app')

@section('content')
    <h1>Daftar Transaksi</h1>
    @if (session('success'))
        <div class="alert alert-success">{{ session('success') }}</div>
    @endif
    <div class="mb-3">
        <a href="{{ route('transactions.create') }}" class="btn btn-primary">Tambah Transaksi</a>
    </div>
    <div class="card p-3">
        <table class="table">
            <thead>
                <tr>
                    <th>Tanggal</th>
                    <th>Deskripsi</th>
                    <th>Kategori</th>
                    <th>Tipe</th>
                    <th>Jumlah</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
                @forelse ($transactions as $transaction)
                    <tr>
                        <td>{{ $transaction->date }}</td>
                        <td>{{ $transaction->description }}</td>
                        <td>{{ $transaction->category ?? '-' }}</td>
                        <td>{{ $transaction->type == 'income' ? 'Pemasukan' : 'Pengeluaran' }}</td>
                        <td>Rp {{ number_format($transaction->amount, 0) }}</td>
                        <td>
                            <a href="{{ route('transactions.edit', $transaction) }}" class="btn">Edit</a>
                            <form action="{{ route('transactions.destroy', $transaction) }}" method="POST" style="display:inline;">
                                @csrf
                                @method('DELETE')
                                <button type="submit" class="btn" onclick="return confirm('Apakah Anda yakin ingin menghapus transaksi ini?')">Hapus</button>
                            </form>
                        </td>
                    </tr>
                @empty
                    <tr>
                        <td colspan="6">Belum ada transaksi</td>
                    </tr>
                @endforelse
            </tbody>
        </table>
    </div>
@endsection