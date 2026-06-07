<?php
namespace App\Http\Controllers;

use App\Models\Transaction;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class DashboardController extends Controller
{
    public function index()
    {
        // Hitung saldo, pemasukan, dan pengeluaran
        $income = Transaction::where('type', 'income')->sum('amount');
        $expense = Transaction::where('type', 'expense')->sum('amount');
        $balance = $income - $expense;

        // Data untuk grafik (pemasukan vs pengeluaran per bulan)
        $monthlyData = Transaction::select(
            DB::raw('MONTH(date) as month'),
            DB::raw("SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) as income"),
            DB::raw("SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END) as expense")
        )
        ->groupBy(DB::raw('MONTH(date)'))
        ->orderBy('month')
        ->get();

        $labels = [];
        $incomes = [];
        $expenses = [];
        $months = ['Jan', 'Feb', 'Mar', 'Apr', 'Mei', 'Jun', 'Jul', 'Agt', 'Sep', 'Okt', 'Nov', 'Des'];
        for ($i = 1; $i <= 12; $i++) {
            $labels[] = $months[$i - 1];
            $monthData = $monthlyData->firstWhere('month', $i);
            $incomes[] = $monthData ? floatval($monthData->income) : 0;
            $expenses[] = $monthData ? floatval($monthData->expense) : 0;
        }

        // Data untuk distribusi kategori
        $categoryData = Transaction::select('category', DB::raw('SUM(amount) as total_amount'))
            ->whereNotNull('category')
            ->groupBy('category')
            ->get();

        $categoryLabels = $categoryData->pluck('category')->toArray();
        $categoryTotals = $categoryData->pluck('total_amount')->map(fn($value) => floatval($value))->toArray();
        $categoryColors = array_map(fn() => sprintf('#%06X', rand(0, 0xFFFFFF)), range(1, count($categoryLabels)));

        // Transaksi terbaru
        $recentTransactions = Transaction::orderBy('date', 'desc')->take(5)->get();

        return view('dashboard', compact('balance', 'income', 'expense', 'labels', 'incomes', 'expenses', 'categoryLabels', 'categoryTotals', 'categoryColors', 'recentTransactions'));
    }
}