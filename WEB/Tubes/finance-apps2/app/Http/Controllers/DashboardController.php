<?php

namespace App\Http\Controllers;

use App\Models\Account;
use App\Models\Transaction;
use App\Models\Budget;
use App\Models\Category;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class DashboardController extends Controller
{
    /**
     * Display the dashboard.
     */
    public function index()
    {
        $user = auth()->user();
        
        // Get total balance
        $totalBalance = $user->accounts->sum('current_balance');
        
        // Get total income and expense this month
        $startOfMonth = Carbon::now()->startOfMonth();
        $endOfMonth = Carbon::now()->endOfMonth();
        
        $monthlyIncome = $user->transactions()
            ->where('type', 'income')
            ->whereBetween('transaction_date', [$startOfMonth, $endOfMonth])
            ->sum('amount');
            
        $monthlyExpense = $user->transactions()
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$startOfMonth, $endOfMonth])
            ->sum('amount');
        
        // Get recent transactions
        $recentTransactions = $user->transactions()
            ->with(['category', 'account'])
            ->orderBy('transaction_date', 'desc')
            ->limit(5)
            ->get();
        
        // Get category breakdown for expenses this month
        $categoryBreakdown = $user->transactions()
            ->select('category_id', DB::raw('SUM(amount) as total'))
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$startOfMonth, $endOfMonth])
            ->groupBy('category_id')
            ->with('category')
            ->get();
        
        // Get budget progress
        $budgets = $user->budgets()
            ->with('category')
            ->get();
            
        foreach ($budgets as $budget) {
            $spent = $user->transactions()
                ->where('category_id', $budget->category_id)
                ->where('type', 'expense')
                ->whereBetween('transaction_date', [$budget->start_date, $budget->end_date])
                ->sum('amount');
                
            $budget->spent = $spent;
            $budget->percentage = $budget->amount > 0 ? ($spent / $budget->amount * 100) : 0;
        }
        
        return view('dashboard', compact(
            'totalBalance',
            'monthlyIncome',
            'monthlyExpense',
            'recentTransactions',
            'categoryBreakdown',
            'budgets'
        ));
    }
}