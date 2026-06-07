<?php

namespace App\Http\Controllers;

use App\Models\Tugas;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class TugasController extends Controller
{
    public function index(Request $request)
    {
        $categoryFilter = $request->query('category', 'Semua');
        $searchTerm = $request->query('search', '');

        $tasksQuery = Tugas::query();

        if ($categoryFilter !== 'Semua') {
            $tasksQuery->where('category', $categoryFilter);
        }

        if (!empty($searchTerm)) {
            $tasksQuery->where('title', 'like', '%' . $searchTerm . '%');
        }

        $tugas = $tasksQuery->orderBy('date', 'desc')->get();

        if ($request->ajax()) {
            return response()->json(['tugas' => $tugas]);
        }

        return view('tugas.index', ['tugas' => $tugas, 'initialCategory' => $categoryFilter]);
    }

    public function store(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'title' => 'required|string|max:255',
            'category' => 'required|string|in:Kerja,Pribadi,Sekolah',
            'date' => 'required|date',
            'status' => 'required|string|in:Belum Selesai,Selesai',
        ]);

        if ($validator->fails()) {
            return response()->json(['errors' => $validator->errors()], 422);
        }

        $tugas = Tugas::create($request->all());
        return response()->json(['message' => 'Tugas berhasil ditambahkan!', 'tugas$tugas' => $tugas], 201);
    }

    public function show(Tugas $tugas) // Menggunakan Route Model Binding
    {
        return response()->json($tugas);
    }


    public function update(Request $request, Tugas $tugas)
    {
        $validator = Validator::make($request->all(), [
            'title' => 'required|string|max:255',
            'category' => 'required|string|in:Kerja,Pribadi,Sekolah',
            'date' => 'required|date',
            'status' => 'required|string|in:Belum Selesai,Selesai',
        ]);

        if ($validator->fails()) {
            return response()->json(['errors' => $validator->errors()], 422);
        }

        $tugas->update($request->all());
        return response()->json([
            'message' => 'Tugas berhasil diupdate!',
            'tugas' => $tugas // jika ingin mengirim data tugas yang baru
        ]);
    }

    public function destroy(Tugas $tugas)
    {
        $tugas->delete();
        return response()->json(['message' => 'Tugas berhasil dihapus!']);
    }
}
