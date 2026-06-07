@extends('layouts.app')

@section('title', 'ASISTENSI 2 - Task Manager')

@section('content')
<div class="row">
  <div class="col-4 col-lg-3 mb-4">
    <h5>Kategori</h5>
    <div class="list-group mt-3">
      <a href="#" class="list-group-item list-group-item-action {{ $initialCategory === 'Semua' ? 'active' : '' }}" id="category-semua">Semua</a> <a href="#" class="list-group-item list-group-item-action {{ $initialCategory === 'Kerja' ? 'active' : '' }}" id="category-kerja">Kerja</a> <a href="#" class="list-group-item list-group-item-action {{ $initialCategory === 'Pribadi' ? 'active' : '' }}" id="category-pribadi">Pribadi</a> <a href="#" class="list-group-item list-group-item-action {{ $initialCategory === 'Sekolah' ? 'active' : '' }}" id="category-sekolah">Sekolah</a> </div>
  </div>

  <div class="col-md-8 col-lg-9">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <div class="col-6">
        <input type="text" id="searchInput" class="form-control" placeholder="Cari tugas"> </div>
      <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#taskModal">
        + Tambah Tugas
      </button>
    </div>

    <div class="table-responsive">
      <table class="table table-bordered table-hover">
        <thead> <tr>
            <th>Judul</th> <th>Kategori</th> <th>Tanggal</th> <th>Status</th> <th>Aksi</th> </tr>
        </thead>
        <tbody id="taskTableBody">
          {{-- Tasks will be loaded here by JavaScript --}}
        </tbody>
      </table>
    </div>
  </div>
</div>

<div class="modal fade" id="taskModal" tabindex="-1" aria-labelledby="taskModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content"> <div class="modal-header">
        <h5 class="modal-title" id="taskModalLabel">Tambah Tugas</h5> <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <form id="taskForm">
          <input type="hidden" id="taskId">
          <div class="mb-3">
            <label for="taskTitle" class="form-label">Judul Tugas</label> <input type="text" class="form-control" id="taskTitle" placeholder="Masukkan judul tugas" required> </div>
          <div class="mb-3">
            <label for="taskCategory" class="form-label">Kategori</label>
            <select class="form-select" id="taskCategory" required>
              <option value="">Pilih Kategori</option> <option value="Kerja">Kerja</option> <option value="Pribadi">Pribadi</option> <option value="Sekolah">Sekolah</option> </select>
          </div>
          <div class="mb-3">
            <label for="taskDate" class="form-label">Tanggal</label> <input type="date" class="form-control" id="taskDate" required> </div>
          <div class="mb-3">
            <label for="taskStatus" class="form-label">Status</label>
            <select class="form-select" id="taskStatus" required>
              <option value="Belum Selesai">Belum Selesai</option> <option value="Selesai">Selesai</option> </select>
          </div>
        </form>
      </div>
      <div class="modal-footer"> <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
        <button type="button" class="btn btn-primary" id="saveTaskBtn">Simpan</button> </div>
    </div>
  </div>
</div>
@endsection

@push('scripts')
<script>
    let currentGlobalCategory = "{{ $initialCategory }}";
</script>
@endpush
