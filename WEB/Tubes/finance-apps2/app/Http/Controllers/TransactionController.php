<?php

namespace App\Http\Controllers;

use App\Models\Transaction;
use App\Models\Category;
use App\Models\Account;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class TransactionController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $query = auth()->user()->transactions()->with(['category', 'account']);
        
        // Filter by type
        if ($request->has('type') && in_array($request->type, ['income', 'expense', 'transfer'])) {
            $query->where('type', $request->type);
        }
        
        // Filter by category
        if ($request->has('category_id') && $request->category_id) {
            $query->where('category_id', $request->category_id);
        }
        
        // Filter by account
        if ($request->has('account_id') && $request->account_id) {
            $query->where('account_id', $request->account_id);
        }
        
        // Filter by date range
        if ($request->has('start_date') && $request->start_date) {
            $query->where('transaction_date', '>=', $request->start_date);
        }
        
        if ($request->has('end_date') && $request->end_date) {
            $query->where('transaction_date', '<=', $request->end_date);
        }
        
        // Order by date
        $query->orderBy('transaction_date', 'desc');
        
        $transactions = $query->paginate(15);
        $categories = Category::all();
        $accounts = auth()->user()->accounts;
        
        return view('transactions.index', compact('transactions', 'categories', 'accounts'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        $categories = Category::all();
        $accounts = auth()->user()->accounts;
        
        return view('transactions.create', compact('categories', 'accounts'));
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'category_id' => 'required|exists:categories,id',
            'account_id' => 'required|exists:accounts,id',
            'amount' => 'required|numeric|min:0',
            'type' => 'required|string|in:income,expense,transfer',
            'transaction_date' => 'required|date',
            'description' => 'nullable|string',
            'attachment' => 'nullable|file|max:2048',
        ]);
        
        // Check if the account belongs to authenticated user
        $account = Account::findOrFail($validated['account_id']);
        $this->authorize('update', $account);
        
        $validated['user_id'] = auth()->id();
        
        // Handle file upload
        if ($request->hasFile('attachment')) {
            $path = $request->file('attachment')->store('attachments');
            $validated['attachment'] = $path;
        }
        
        // Create transaction
        $transaction = Transaction::create($validated);
        
        // Update account balance
        if ($validated['type'] == 'income') {
            $account->current_balance += $validated['amount'];
        } elseif ($validated['type'] == 'expense') {
            $account->current_balance -= $validated['amount'];
        }
        
        $account->save();

        return redirect()->route('transactions.index')
            ->with('success', 'Transaksi berhasil dibuat!');
    }

    /**
     * Display the specified resource.
     */
    public function show(Transaction $transaction)
    {
        // Check if the transaction belongs to authenticated user
        $this->authorize('view', $transaction);
        
        return view('transactions.show', compact('transaction'));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Transaction $transaction)
    {
        // Check if the transaction belongs to authenticated user
        $this->authorize('update', $transaction);
        
        $categories = Category::all();
        $accounts = auth()->user()->accounts;
        
        return view('transactions.edit', compact('transaction', 'categories', 'accounts'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Transaction $transaction)
    {
        // Check if the transaction belongs to authenticated user
        $this->authorize('update', $transaction);
        
        $validated = $request->validate([
            'category_id' => 'required|exists:categories,id',
            'account_id' => 'required|exists:accounts,id',
            'amount' => 'required|numeric|min:0',
            'type' => 'required|string|in:income,expense,transfer',
            'transaction_date' => 'required|date',
            'description' => 'nullable|string',
            'attachment' => 'nullable|file|max:2048',
        ]);
        
        // Check if the account belongs to authenticated user
        $newAccount = Account::findOrFail($validated['account_id']);
        $this->authorize('update', $newAccount);
        
        $oldAccount = $transaction->account;
        $oldAmount = $transaction->amount;
        $oldType = $transaction->type;
        
        // Handle file upload
        if ($request->hasFile('attachment')) {
            // Delete old file if exists
            if ($transaction->attachment) {
                Storage::delete($transaction->attachment);
            }
            
            $path = $request->file('attachment')->store('attachments');
            $validated['attachment'] = $path;
        }
        
        // Update transaction
        $transaction->update($validated);
        
        // Revert old account balance
        if ($oldType == 'income') {
            $oldAccount->current_balance -= $oldAmount;
        } elseif ($oldType == 'expense') {
            $oldAccount->current_balance += $oldAmount;
        }
        
        // Update new account balance
        if ($validated['type'] == 'income') {
            $newAccount->current_balance += $validated['amount'];
        } elseif ($validated['type'] == 'expense') {
            $newAccount->current_balance -= $validated['amount'];
        }
        
        // Save account changes
        $oldAccount->save();
        if ($oldAccount->id != $newAccount->id) {
            $newAccount->save();
        }

        return redirect()->route('transactions.index')
            ->with('success', 'Transaksi berhasil diperbarui!');
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Transaction $transaction)
    {
        // Check if the transaction belongs to authenticated user
        $this->authorize('delete', $transaction);
        
        $account = $transaction->account;
        
        // Revert account balance
        if ($transaction->type == 'income') {
            $account->current_balance -= $transaction->amount;
        } elseif ($transaction->type == 'expense') {
            $account->current_balance += $transaction->amount;
        }
        
        // Delete attachment if exists
        if ($transaction->attachment) {
            Storage::delete($transaction->attachment);
        }
        
        $transaction->delete();
        $account->save();

        return redirect()->route('transactions.index')
            ->with('success', 'Transaksi berhasil dihapus!');
    }
}