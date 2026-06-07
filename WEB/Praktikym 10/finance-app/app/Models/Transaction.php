<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Transaction extends Model
{
    use HasFactory;

    protected $fillable = [
        'description',
        'amount',
        'type',
        'category',
        'transaction_date',
        'notes',
    ];

    protected $casts = [
        'transaction_date' => 'date',
        'amount' => 'decimal:2',
    ];

    // Scope untuk menyaring transaksi berdasarkan tipe (income atau expense)
    public function scopeOfType($query, $type)
    {
        return $query->where('type', $type);
    }

    // Scope untuk mendapatkan total berdasarkan tipe
    public function scopeTotal($query, $type = null)
    {
        if ($type) {
            return $query->where('type', $type)->sum('amount');
        }
        
        return $query->sum('amount');
    }
}