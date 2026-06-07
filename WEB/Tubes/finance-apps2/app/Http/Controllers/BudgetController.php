<?php

namespace App\Http\Controllers;

use App\Models\Budget;
use App\Models\Category;
use App\Models\Transaction;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class BudgetController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $budgets = auth()->user()->budgets()->with('category')->get();
        
        foreach ($budgets as $budget) {
            $spent = auth()->user()->transactions()
                ->where('category_id', $budget->category_id)
                ->where('type', 'expense')
                ->whereBetween('transaction_date', [$budget->start_date, $budget->end_date])
                ->sum('amount');
                
            $budget->spent = $spent;
            $budget->percentage = $budget->amount > 0 ? ($spent / $budget->amount * 100) : 0;
        }
        
        return view('budgets.index', compact('budgets'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        $categories = Category::where('type', 'expense')->get();
        return view('budgets.create', compact('categories'));
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'category_id' => 'required|exists:categories,id',
            'amount' => 'required|numeric|min:0',
            'period_type' => 'required|string|in:monthly,yearly',
            'start_date' => 'required|date',
            'end_date' => 'required|date|after:start_date',
            'description' => 'nullable|string',
        ]);
        
        $validated['user_id'] = auth()->id();
        
        // Ensure the category is for expense
        $category = Category::findOrFail($validated['category_id']);
        if ($category->type != 'expense') {
            return back()->withErrors(['category_id' => 'Kategori harus bertipe pengeluaran.'])->withInput();
        }
        
        Budget::create($validated);

        return redirect()->route('budgets.index')
            ->with('success', 'Anggaran berhasil dibuat!');
    }

    /**
     * Display the specified resource.
     */
    public function show(Budget $budget)
    {
        // Check if the budget belongs to authenticated user
        $this->authorize('view', $budget);
        
        $transactions = auth()->user()->transactions()
            ->where('category_id', $budget->category_id)
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$budget->start_date, $budget->end_date])
            ->with('account')
            ->orderBy('transaction_date', 'desc')
            ->paginate(10);
            
        $spent = $transactions->sum('amount');
        $percentage = $budget->amount > 0 ? ($spent / $budget->amount * 100) : 0;
        
        return view('budgets.show', compact('budget', 'transactions', 'spent', 'percentage'));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Budget $budget)
    {
        // Check if the budget belongs to authenticated user
        $this->authorize('update', $budget);
        
        $categories = Category::where('type', 'expense')->get();
        return view('budgets.edit', compact('budget', 'categories'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Budget $budget)
    {
        // Check if the budget belongs to authenticated user
        $this->authorize('update', $budget);
        
        $validated = $request->validate([
            'category_id' => 'required|exists:categories,id',
            'amount' => 'required|numeric|min:0',
            'period_type' => 'required|string|in:monthly,yearly',
            'start_date' => 'required|date',
            'end_date' => 'required|date|after:start_date',
            'description' => 'nullable|string',
        ]);
        
        // Ensure the category is for expense
        $category = Category::findOrFail($validated['category_id']);
        if ($category->type != 'expense') {
            return back()->withErrors(['category_id' => 'Kategori harus bertipe pengeluaran.'])->withInput();
        }
        
        $budget->update($validated);

        return redirect()->route('budgets.index')
            ->with('success', 'Anggaran berhasil diperbarui!');
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Budget $budget)
    {
        // Check if the budget belongs to authenticated user
        $this->authorize('delete', $budget);
        
        $budget->delete();

        return redirect()->route('budgets.index')
            ->with('success', 'Anggaran berhasil dihapus!');
    }

    /**
     * Show budget report.
     */
    public function report(Request $request)
    {
        $user = auth()->user();
        $currentMonth = Carbon::now()->format('Y-m');
        $month = $request->input('month', $currentMonth);
        
        list($year, $monthNum) = explode('-', $month);
        
        $startDate = Carbon::createFromDate($year, $monthNum, 1)->startOfMonth();
        $endDate = Carbon::createFromDate($year, $monthNum, 1)->endOfMonth();
        
        // Get monthly budgets
        $budgets = $user->budgets()
            ->with('category')
            ->where('period_type', 'monthly')
            ->where(function($query) use ($startDate, $endDate) {
                $query->whereBetween('start_date', [$startDate, $endDate])
                      ->orWhereBetween('end_date', [$startDate, $endDate]);
            })
            ->get();
            
        // Get yearly budgets (prorated for current month)
        $yearlyBudgets = $user->budgets()
            ->with('category')
            ->where('period_type', 'yearly')
            ->where(function($query) use ($startDate, $endDate) {
                $query->whereBetween('start_date', [$startDate->copy()->startOfYear(), $endDate])
                      ->orWhereBetween('end_date', [$startDate, $endDate->copy()->endOfYear()]);
            })
            ->get();
            
        foreach ($yearlyBudgets as $budget) {
            // Calculate prorated monthly amount
            $totalDays = Carbon::parse($budget->start_date)->diffInDays(Carbon::parse($budget->end_date)) + 1;
            $daysInMonth = $startDate->daysInMonth;
            
            $budget->monthly_amount = $budget->amount / ($totalDays / $daysInMonth);
            $budget->original_amount = $budget->amount;
            $budget->amount = $budget->monthly_amount;
        }
        
        // Merge budgets
        $allBudgets = $budgets->concat($yearlyBudgets);
        
        // Get expenses for each budget
        foreach ($allBudgets as $budget) {
            $spent = $user->transactions()
                ->where('category_id', $budget->category_id)
                ->where('type', 'expense')
                ->whereBetween('transaction_date', [$startDate, $endDate])
                ->sum('amount');
                
            $budget->spent = $spent;
            $budget->percentage = $budget->amount > 0 ? ($spent / $budget->amount * 100) : 0;
            $budget->remaining = max(0, $budget->amount - $spent);
        }
        
        // Get total income and expense for the month
        $totalIncome = $user->transactions()
            ->where('type', 'income')
            ->whereBetween('transaction_date', [$startDate, $endDate])
            ->sum('amount');
            
        $totalExpense = $user->transactions()
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$startDate, $endDate])
            ->sum('amount');
            
        // Get category breakdown
        $categoryBreakdown = $user->transactions()
            ->select('category_id', DB::raw('SUM(amount) as total'))
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$startDate, $endDate])
            ->groupBy('category_id')
            ->with('category')
            ->get();
            
        // Get daily expenses
        $dailyExpenses = $user->transactions()
            ->select('transaction_date', DB::raw('SUM(amount) as total'))
            ->where('type', 'expense')
            ->whereBetween('transaction_date', [$startDate, $endDate])
            ->groupBy('transaction_date')
            ->orderBy('transaction_date')
            ->get()
            ->keyBy(function ($item) {
                return Carbon::parse($item->transaction_date)->format('d');
            });
            
        $days = [];
        $amounts = [];
        
        for ($day = 1; $day <= $endDate->day; $day++) {
            $days[] = $day;
            $dayStr = str_pad($day, 2, '0', STR_PAD_LEFT);
            $amounts[] = isset($dailyExpenses[$dayStr]) ? $dailyExpenses[$dayStr]->total : 0;
        }
        
        return view('budgets.report', compact(
            'allBudgets',
            'totalIncome',
            'totalExpense',
            'categoryBreakdown',
            'month',
            'days',
            'amounts'
        ));
    }
}