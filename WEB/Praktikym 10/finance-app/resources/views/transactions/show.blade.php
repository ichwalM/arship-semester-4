@extends('layouts.app')

@section('title', 'Detail Transaksi')

@section('actions')
    <a href="{{ route('transactions.index') }}" class="btn btn-sm btn-secondary">
        <i class="fas fa-arrow-left"></i> Kembali
    </a>
    <a href="{{ route('transactions.edit', $transaction) }}" class="btn btn-sm btn-warning">
        <i class="fas fa-edit"></i> Edit
    </a>
    <button type="button" class="btn btn-sm btn-danger" 
            onclick="if(confirm('Apakah Anda yakin ingin menghapus transaksi ini?')) { 
                document.getElementById('delete-form').submit(); 
            }">
        <i class="fas fa-trash"></i> Hapus
    </button>
    <form id="delete-form" action="{{ route('transactions.destroy', $transaction) }}" method="POST" style="display: none;">
        @csrf
        @method('DELETE')
    </form>
@endsection

@section('content')
<div class="row">
    <div class="col-lg-8 mx-auto">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Informasi Transaksi</h6>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-bordered">
                        <tr>
                            <th class="bg-light" width="30%">ID Transaksi</th>
                            <td>{{ $transaction->id }}</td>
                        </tr>
                        <tr>
                            <th class="bg-light">Deskripsi</th>
                            <td>{{ $transaction->description }}</td>
                        </tr>
                        <tr>
                            <th class="bg-light">Jumlah</th>
                            <td class="{{ $transaction->type == 'income' ? 'text-income' : 'text-expense' }} fw-bold">
                                Rp {{ number_format($transaction->amount, 0, ',', '.') }}
                            </td>
                        </tr>
                        <tr>
                            <th class="bg-light">Tipe</th>
                            <td>
                                @if($transaction->type == 'income')
                                    <span class="badge bg-success">Pemasukan</span>
                                @else
                                    <span class="badge bg-danger">Pengeluaran</span>
                                @endif
                            </td>
                        </tr>
                        <tr>
                            <th class="bg-light">Kategori</th>
                            <td>
                                <span class="badge bg-secondary">{{ ucfirst($transaction->category) }}</span>
                            </td>
                        </tr>
                        <tr>
                            <th class="bg-light">Tanggal Transaksi</th>
                            <td>{{ $transaction->transaction_date->format('d F Y') }}</td>
                        </tr>
                        <tr>
                            <th class="bg-light">Catatan</th>
                            <td>{{ $transaction->notes ?? '-' }}</td>
                        </tr>
                        <tr>
                            <th class="bg-light">Dibuat Pada</th>
                            <td>{{ $transaction->created_at->format('d F Y H:i') }}</td>
                        </tr>
                        <tr>
                            <th class="bg-light">Terakhir Diperbarui</th>
                            <td>{{ $transaction->updated_at->format('d F Y H:i') }}</td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
@endsection