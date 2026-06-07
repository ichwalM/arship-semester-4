<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Mahasiswa extends Model
{
    protected $fillable = ['nama', 'nim', 'email', 'prodi_id', 'fakultas_id'];

    public function prodi()
    {
        return $this->belongsTo(Prodi::class);
    }

    public function fakultas()
    {
        return $this->belongsTo(Fakultas::class);
    }
}
