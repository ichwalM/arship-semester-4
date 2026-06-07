<?php

namespace App\Http\Controllers;

use App\Models\Transaction;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class TransactionController extends Controller
{
    /**
     * Display a listing of the transactions.
     */
    public function index(Request $request)
    {
        $query = Transaction::query();
        
        // Filter by type if specified
        if ($request->filled('type')) {
            $query->where('type', $request->type);
        }
        
        // Filter by category if specified
        if ($request->filled('category')) {
            $query->where('category', $request->category);
        }
        
        // Filter by date range if specified
        if ($request->filled('start_date') && $request->filled('end_date')) {
            $query->whereBetween('transaction_date', [$request->start_date, $request->end_date]);
        }
        
        $transactions = $query->orderBy('transaction_date', 'desc')->paginate(10);
        
        // Get summary statistics
        $totalIncome = Transaction::ofType('income')->sum('amount');
        $totalExpense = Transaction::ofType('expense')->sum('amount');
        $balance = $totalIncome - $totalExpense;
        
        // Get categories for filter dropdown
        $categories = [
            'income' => ['salary', 'business', 'investment'],
            'expense' => ['food', 'transportation', 'utilities', 'rent', 'entertainment', 'others']
        ];
        
        return view('transactions.index', compact('transactions', 'totalIncome', 'totalExpense', 'balance', 'categories'));
    }

    /**
     * Show the form for creating a new transaction.
     */
    public function create()
    {
        $categories = [
            'income' => ['salary', 'business', 'investment'],
            'expense' => ['food', 'transportation', 'utilities', 'rent', 'entertainment', 'others']
        ];
        
        return view('transactions.create', compact('categories'));
    }

    /**
     * Store a newly created transaction in storage.
     */
    public function store(Request $request)
    {
        $request->validate([
            'description' => 'required|string|max:255',
            'amount' => 'required|numeric|min:0',
            'type' => 'required|in:income,expense',
            'category' => 'required|string',
            'transaction_date' => 'required|date',
            'notes' => 'nullable|string',
        ]);
        
        Transaction::create($request->all());
        
        return redirect()->route('transactions.index')
            ->with('success', 'Transaksi berhasil ditambahkan.');
    }

    /**
     * Display the specified transaction.
     */
    public function show(Transaction $transaction)
    {
        return view('transactions.show', compact('transaction'));
    }

    /**
     * Show the form for editing the specified transaction.
     */
    public function edit(Transaction $transaction)
    {
        $categories = [
            'income' => ['salary', 'business', 'investment'],
            'expense' => ['food', 'transportation', 'utilities', 'rent', 'entertainment', 'others']
        ];
        
        return view('transactions.edit', compact('transaction', 'categories'));
    }

    /**
     * Update the specified transaction in storage.
     */
    public function update(Request $request, Transaction $transaction)
    {
        $request->validate([
            'description' => 'required|string|max:255',
            'amount' => 'required|numeric|min:0',
            'type' => 'required|in:income,expense',
            'category' => 'required|string',
            'transaction_date' => 'required|date',
            'notes' => 'nullable|string',
        ]);
        
        $transaction->update($request->all());
        
        return redirect()->route('transactions.index')
            ->with('success', 'Transaksi berhasil diperbarui.');
    }

    /**
     * Remove the specified transaction from storage.
     */
    public function destroy(Transaction $transaction)
    {
        $transaction->delete();
        
        return redirect()->route('transactions.index')
            ->with('success', 'Transaksi berhasil dihapus.');
    }

    /**
     * Display a dashboard with statistics.
     */
    public function dashboard()
    {
        // Get total income and expenses
        $totalIncome = Transaction::ofType('income')->sum('amount');
        $totalExpense = Transaction::ofType('expense')->sum('amount');
        $balance = $totalIncome - $totalExpense;
        
        // Get recent transactions
        $recentTransactions = Transaction::orderBy('transaction_date', 'desc')
                                       ->limit(5)
                                       ->get();
        
        // Get monthly stats for the current year
        $monthlyStats = DB::table('transactions')
            ->selectRaw('MONTH(transaction_date) as month, type, SUM(amount) as total')
            ->whereYear('transaction_date', date('Y'))
            ->groupBy('month', 'type')
            ->orderBy('month')
            ->get();
        
        // Prepare data for charts
        $months = [];
        $incomeData = array_fill(0, 12, 0);
        $expenseData = array_fill(0, 12, 0);
        
        foreach ($monthlyStats as $stat) {
            $monthIndex = $stat->month - 1; // Convert to 0-based index
            if ($stat->type === 'income') {
                $incomeData[$monthIndex] = $stat->total;
            } else {
                $expenseData[$monthIndex] = $stat->total;
            }
        }
        
        // Get category distribution
        $categoryStats = DB::table('transactions')
            ->select('category', DB::raw('SUM(amount) as total'))
            ->groupBy('category')
            ->orderBy('total', 'desc')
            ->get();
        
        return view('dashboard', compact(
            'totalIncome', 
            'totalExpense', 
            'balance', 
            'recentTransactions',
            'incomeData',
            'expenseData',
            'categoryStats'
        ));
    }
}