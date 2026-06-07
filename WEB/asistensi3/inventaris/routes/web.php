<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\TugasController;

use App\Http\Controllers\Controller;

Route::get('/', [TugasController::class, 'index'])->name('Tugas.index');

Route::prefix('tasks')->name('tasks.')->group(function () {
    Route::get('/', [TugasController::class, 'index'])->name('filter');
    Route::post('/', [TugasController::class, 'store'])->name('store');
    Route::get('/{task}', [TugasController::class, 'show'])->name('show'); // To get a single task for editing
    Route::put('/{task}', [TugasController::class, 'update'])->name('update');
    Route::delete('/{task}', [TugasController::class, 'destroy'])->name('destroy');
});

// Route::get('/', function () {
//     return view('welcome');
// });
