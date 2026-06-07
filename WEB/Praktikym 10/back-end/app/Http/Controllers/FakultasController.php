<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Fakultas;

class FakultasController extends Controller
{
    public function index()
    {
        return response()->json(Fakultas::all());
    }

    public function store(Request $request)
    {
        $request->validate(['nama' => 'required|string']);
        $fakultas = Fakultas::create($request->all());
        return response()->json($fakultas, 201);
    }

    public function show($id)
    {
        return response()->json(Fakultas::findOrFail($id));
    }

    public function update(Request $request, $id)
    {
        $fakultas = Fakultas::findOrFail($id);
        $fakultas->update($request->all());
        return response()->json($fakultas);
    }

    public function destroy($id)
    {
        Fakultas::destroy($id);
        return response()->json(null, 204);
    }
}
