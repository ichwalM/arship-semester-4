<?php

namespace App\Http\Controllers;

use App\Models\Transaction;
use App\Models\Category;
use App\Models\Account;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class ReportController extends Controller
{
    /**
     * Display financial report
     */
    public function index(Request $request)
    {
        $user = auth()->user();
        $period = $request->input('period', 'monthly');
        $date = $request->input('date', Carbon::now()->format('Y-m'));
        
        if ($period == 'monthly') {
            list($year, $month) = explode('-', $date);
            $startDate = Carbon::createFromDate($year, $month, 1)->startOfMonth();
            $endDate = Carbon::createFromDate($year, $month, 1)->endOfMonth();
            $previousPeriod = Carbon::createFromDate($year, $month, 1)->subMonth()->format('Y-m');
            $nextPeriod = Carbon::createFromDate($year, $month, 1)->addMonth()->format('Y-m');
            $periodLabel = Carbon::createFromDate($year, $month, 1)->format('F Y');
        } else { // yearly
            $year = $date;
            $startDate = Carbon::createFromDate($year, 1, 1)->startOfYear();
            $endDate = Carbon::createFromDate($year, 12, 31)->endOfYear();
            $previousPeriod = (int)$year - 1;
            $nextPeriod = (int)$year + 1;
            $periodLabel = $year;
        }
        
        // Get total income, expense, and balance
        $totalIncome = $user->transactions()
            ->where('type', 'income')
            ->whereBetween('transaction_date', [$startDate, $endDate])
            ->sum('amount');
            
        $totalExpense = $user->transactions()
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$startDate, $endDate])
            ->sum('amount');
            
        $balance = $totalIncome - $totalExpense;
        
        // Get income by category
        $incomeByCategory = $user->transactions()
            ->select('category_id', DB::raw('SUM(amount) as total'))
            ->where('type', 'income')
            ->whereBetween('transaction_date', [$startDate, $endDate])
            ->groupBy('category_id')
            ->with('category')
            ->get();
        
        // Get expense by category
        $expenseByCategory = $user->transactions()
            ->select('category_id', DB::raw('SUM(amount) as total'))
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$startDate, $endDate])
            ->groupBy('category_id')
            ->with('category')
            ->get();
        
        // Get income and expense by account
        $byAccount = $user->transactions()
            ->select('account_id', 'type', DB::raw('SUM(amount) as total'))
            ->whereBetween('transaction_date', [$startDate, $endDate])
            ->groupBy('account_id', 'type')
            ->with('account')
            ->get()
            ->groupBy('account_id');
        
        $accountSummary = [];
        foreach ($byAccount as $accountId => $transactions) {
            $account = Account::find($accountId);
            $income = $transactions->where('type', 'income')->sum('total');
            $expense = $transactions->where('type', 'expense')->sum('total');
            $accountBalance = $income - $expense;
            
            $accountSummary[] = [
                'account' => $account,
                'income' => $income,
                'expense' => $expense,
                'balance' => $accountBalance
            ];
        }
        
        // For monthly report, get daily data
        // For yearly report, get monthly data
        if ($period == 'monthly') {
            $timeSeries = $user->transactions()
                ->select(
                    DB::raw('DATE(transaction_date) as date'),
                    'type',
                    DB::raw('SUM(amount) as total')
                )
                ->whereBetween('transaction_date', [$startDate, $endDate])
                ->groupBy('date', 'type')
                ->orderBy('date')
                ->get();
                
            $labels = [];
            $incomeData = [];
            $expenseData = [];
            
            for ($day = 1; $day <= $endDate->daysInMonth; $day++) {
                $currentDate = Carbon::createFromDate($year, $month, $day)->format('Y-m-d');
                $labels[] = $day;
                
                $dayIncome = $timeSeries->where('date', $currentDate)
                    ->where('type', 'income')
                    ->first();
                $incomeData[] = $dayIncome ? $dayIncome->total : 0;
                
                $dayExpense = $timeSeries->where('date', $currentDate)
                    ->where('type', 'expense')
                    ->first();
                $expenseData[] = $dayExpense ? $dayExpense->total : 0;
            }
        } else { // yearly
            $timeSeries = $user->transactions()
                ->select(
                    DB::raw('MONTH(transaction_date) as month'),
                    'type',
                    DB::raw('SUM(amount) as total')
                )
                ->whereBetween('transaction_date', [$startDate, $endDate])
                ->groupBy('month', 'type')
                ->orderBy('month')
                ->get();
                
            $labels = [];
            $incomeData = [];
            $expenseData = [];
            
            for ($month = 1; $month <= 12; $month++) {
                $monthName = Carbon::createFromDate($year, $month, 1)->format('M');
                $labels[] = $monthName;
                
                $monthIncome = $timeSeries->where('month', $month)
                    ->where('type', 'income')
                    ->first();
                $incomeData[] = $monthIncome ? $monthIncome->total : 0;
                
                $monthExpense = $timeSeries->where('month', $month)
                    ->where('type', 'expense')
                    ->first();
                $expenseData[] = $monthExpense ? $monthExpense->total : 0;
            }
        }
        
        return view('reports.index', compact(
            'period',
            'date',
            'periodLabel',
            'previousPeriod',
            'nextPeriod',
            'totalIncome',
            'totalExpense',
            'balance',
            'incomeByCategory',
            'expenseByCategory',
            'accountSummary',
            'labels',
            'incomeData',
            'expenseData'
        ));
    }

    /**
     * Show cash flow report.
     */
    public function cashFlow(Request $request)
    {
        $user = auth()->user();
        $startDate = $request->input('start_date', Carbon::now()->subMonths(5)->startOfMonth()->format('Y-m-d'));
        $endDate = $request->input('end_date', Carbon::now()->format('Y-m-d'));
        
        $startDateObj = Carbon::parse($startDate);
        $endDateObj = Carbon::parse($endDate);
        
        // Get monthly income and expense
        $monthlyData = $user->transactions()
            ->select(
                DB::raw('YEAR(transaction_date) as year'),
                DB::raw('MONTH(transaction_date) as month'),
                'type',
                DB::raw('SUM(amount) as total')
            )
            ->whereBetween('transaction_date', [$startDateObj, $endDateObj])
            ->groupBy('year', 'month', 'type')
            ->orderBy('year')
            ->orderBy('month')
            ->get();
        
        $months = [];
        $incomeData = [];
        $expenseData = [];
        $balanceData = [];
        
        $current = $startDateObj->copy()->startOfMonth();
        $end = $endDateObj->copy()->endOfMonth();
        
        while ($current->lte($end)) {
            $yearMonth = $current->format('Y-m');
            $monthLabel = $current->format('M Y');
            $months[] = $monthLabel;
            
            $income = $monthlyData->where('year', $current->year)
                ->where('month', $current->month)
                ->where('type', 'income')
                ->first();
            $incomeAmount = $income ? $income->total : 0;
            $incomeData[] = $incomeAmount;
            
            $expense = $monthlyData->where('year', $current->year)
                ->where('month', $current->month)
                ->where('type', 'expense')
                ->first();
            $expenseAmount = $expense ? $expense->total : 0;
            $expenseData[] = $expenseAmount;
            
            $balanceData[] = $incomeAmount - $expenseAmount;
            
            $current->addMonth();
        }
        
        // Get net worth over time
        $accounts = $user->accounts;
        $totalNetWorth = $accounts->sum('current_balance');
        
        // Get account balances
        $accountBalances = [];
        foreach ($accounts as $account) {
            $accountBalances[] = [
                'name' => $account->name,
                'balance' => $account->current_balance
            ];
        }
        
        return view('reports.cash-flow', compact(
            'startDate',
            'endDate',
            'months',
            'incomeData',
            'expenseData',
            'balanceData',
            'totalNetWorth',
            'accountBalances'
        ));
    }

    /**
     * Export report as PDF
     */
    public function exportPdf(Request $request)
    {
        // Implementasi PDF export
        // Gunakan library seperti dompdf atau barryvdh/laravel-dompdf
        
        // Contoh implementasi sederhana
        $period = $request->input('period', 'monthly');
        $date = $request->input('date', Carbon::now()->format('Y-m'));
        
        // Generate report data (sama seperti method index)
        // ...
        
        // Kemudian export ke PDF
        // return PDF::loadView('reports.pdf', compact('data'))->download('report.pdf');
        
        // Untuk sementara, redirect kembali ke halaman report
        return redirect()->route('reports.index', ['period' => $period, 'date' => $date])
            ->with('info', 'Export PDF akan diimplementasikan nanti.');
    }
}