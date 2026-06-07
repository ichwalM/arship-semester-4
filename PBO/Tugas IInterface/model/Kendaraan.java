package model;
abstract class Kendaraan {
    String nama;
    
    Kendaraan(String nama) {
        this.nama = nama;
    }
    abstract void berjalan();
    void info() {
        System.out.println("Nama Kendaraan: " + nama);
    }
}