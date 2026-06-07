<?php

namespace App\Http\Controllers;

use App\Models\Task;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class TaskController extends Controller
{
    public function index(Request $request)
    {
        $categoryFilter = $request->query('category', 'Semua');
        $searchTerm = $request->query('search', '');

        $tasksQuery = Task::query();

        if ($categoryFilter !== 'Semua') {
            $tasksQuery->where('category', $categoryFilter);
        }

        if (!empty($searchTerm)) {
            $tasksQuery->where('title', 'like', '%' . $searchTerm . '%');
        }

        $tasks = $tasksQuery->orderBy('date', 'desc')->get();

        if ($request->ajax()) {
            return response()->json(['tasks' => $tasks]);
        }

        return view('tasks.index', ['tasks' => $tasks, 'initialCategory' => $categoryFilter]);
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

        $task = Task::create($request->all());
        return response()->json(['message' => 'Tugas berhasil ditambahkan!', 'task' => $task], 201);
    }
    public function show(Task $task)
    {
        return response()->json($task);
    }

    public function update(Request $request, Task $task)
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

        $task->update($request->all());
        return response()->json(['message' => 'Tugas berhasil diperbarui!', 'task' => $task]);
    }

    public function destroy(Task $task)
    {
        $task->delete();
        return response()->json(['message' => 'Tugas berhasil dihapus!']);
    }
}
