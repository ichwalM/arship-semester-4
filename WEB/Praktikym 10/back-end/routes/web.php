<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\MahasiswaController;
use App\Http\Controllers\FakultasController;
use App\Http\Controllers\ProdisController;

// Mahasiswa routes
Route::get('/mahasiswa', [MahasiswaController::class, 'index']);
Route::post('/mahasiswa', [MahasiswaController::class, 'store']);
Route::get('/mahasiswa/{id}', [MahasiswaController::class, 'show']);
Route::put('/mahasiswa/{id}', [MahasiswaController::class, 'update']);
Route::delete('/mahasiswa/{id}', [MahasiswaController::class, 'destroy']);

// Fakultas routes
Route::get('/fakultas', [FakultasController::class, 'index']);
Route::post('/fakultas', [FakultasController::class, 'store']);
Route::get('/fakultas/{id}', [FakultasController::class, 'show']);
Route::put('/fakultas/{id}', [FakultasController::class, 'update']);
Route::delete('/fakultas/{id}', [FakultasController::class, 'destroy']);

// Prodi routes
Route::get('/prodi', [ProdisController::class, 'index']);
Route::post('/prodi', [ProdisController::class, 'store']);
Route::get('/prodi/{id}', [ProdisController::class, 'show']);
Route::put('/prodi/{id}', [ProdisController::class, 'update']);
Route::delete('/prodi/{id}', [ProdisController::class, 'destroy']);
