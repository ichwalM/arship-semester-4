<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    
    public function up(): void
    {
        Schema::create('tugas', function (Blueprint $table) {
            $table->id();
            $table->string('title');
            $table->string('category');
            $table->date('date');
            $table->string('status')->default('Belum Selesai');
            $table->timestamps();
        });
    }

    
    public function down(): void
    {
        Schema::dropIfExists('tugas');
    }
};
