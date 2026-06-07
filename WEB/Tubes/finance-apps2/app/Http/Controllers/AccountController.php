<?php

namespace App\Http\Controllers;

use App\Models\Account;
use Illuminate\Http\Request;

class AccountController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $accounts = auth()->user()->accounts;
        return view('accounts.index', compact('accounts'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('accounts.create');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'type' => 'required|string|in:cash,bank,e-wallet',
            'initial_balance' => 'required|numeric|min:0',
            'currency' => 'required|string|max:10',
            'description' => 'nullable|string',
        ]);

        $validated['user_id'] = auth()->id();
        $validated['current_balance'] = $validated['initial_balance'];

        Account::create($validated);

        return redirect()->route('accounts.index')
            ->with('success', 'Akun berhasil dibuat!');
    }

    /**
     * Display the specified resource.
     */
    public function show(Account $account)
    {
        // Check if the account belongs to authenticated user
        $this->authorize('view', $account);
        
        $transactions = $account->transactions()
            ->with('category')
            ->orderBy('transaction_date', 'desc')
            ->paginate(10);
            
        return view('accounts.show', compact('account', 'transactions'));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Account $account)
    {
        // Check if the account belongs to authenticated user
        $this->authorize('update', $account);
        
        return view('accounts.edit', compact('account'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Account $account)
    {
        // Check if the account belongs to authenticated user
        $this->authorize('update', $account);
        
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'type' => 'required|string|in:cash,bank,e-wallet',
            'currency' => 'required|string|max:10',
            'description' => 'nullable|string',
        ]);

        $account->update($validated);

        return redirect()->route('accounts.index')
            ->with('success', 'Akun berhasil diperbarui!');
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Account $account)
    {
        // Check if the account belongs to authenticated user
        $this->authorize('delete', $account);
        
        $account->delete();

        return redirect()->route('accounts.index')
            ->with('success', 'Akun berhasil dihapus!');
    }
}