<?php

namespace App\Http\Controllers;

use App\Models\prodis;
use Illuminate\Http\Request;

class ProdisController extends Controller
{
    public function index()
    {
        return response()->json(Prodis::with('fakultas')->get());
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'kode' => 'required|string|unique:prodis,kode',
            'nama' => 'required|string',
            'fakultas_id' => 'required|exists:fakultas,id',
        ]);

        $prodi = Prodis::create($validated);
        return response()->json($prodi, 201);
    }

    public function show($id)
    {
        return response()->json(Prodis::with('fakultas')->findOrFail($id));
    }

    public function update(Request $request, $id)
    {
        $prodi = Prodis::findOrFail($id);

        $validated = $request->validate([
            'kode' => 'sometimes|required|string|unique:prodis,kode,' . $id,
            'nama' => 'sometimes|required|string',
            'fakultas_id' => 'sometimes|required|exists:fakultas,id',
        ]);

        $prodi->update($validated);
        return response()->json($prodi);
    }

    public function destroy($id)
    {
        Prodis::destroy($id);
        return response()->json(null, 204);
    }
}
