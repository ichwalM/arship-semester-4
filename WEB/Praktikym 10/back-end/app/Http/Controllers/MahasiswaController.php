<?php

namespace App\Http\Controllers;

use App\Models\Mahasiswa;
use Illuminate\Http\Request;

class MahasiswaController extends Controller
{
    public function index()
    {
        $mahasiswa = Mahasiswa::with(['fakultas', 'prodi'])->get();
        return response()->json($mahasiswa);
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'nama' => 'required|string|max:100',
            'email' => 'required|email|unique:mahasiswas,email',
            'nim' => 'required|string|max:20|unique:mahasiswas,nim',
            'fakultas_id' => 'required|exists:fakultas,id',
            'prodi_id' => 'required|exists:prodis,id',
        ]);

        $mahasiswa = Mahasiswa::create($validated);
        return response()->json($mahasiswa, 201);
    }

    public function show($id)
    {
        $mahasiswa = Mahasiswa::with(['fakultas', 'prodi'])->findOrFail($id);
        return response()->json($mahasiswa);
    }

    public function update(Request $request, $id)
    {
        $mahasiswa = Mahasiswa::findOrFail($id);

        $validated = $request->validate([
            'nama' => 'sometimes|required|string|max:100',
            'email' => 'sometimes|required|email|unique:mahasiswas,email,' . $id,
            'nim' => 'sometimes|required|string|max:20|unique:mahasiswas,nim,' . $id,
            'fakultas_id' => 'sometimes|required|exists:fakultas,id',
            'prodi_id' => 'sometimes|required|exists:prodis,id',
        ]);

        $mahasiswa->update($validated);
        return response()->json($mahasiswa);
    }

    public function destroy($id)
    {
        Mahasiswa::destroy($id);
        return response()->json(['message' => 'Data mahasiswa berhasil dihapus'], 204);
    }
}
