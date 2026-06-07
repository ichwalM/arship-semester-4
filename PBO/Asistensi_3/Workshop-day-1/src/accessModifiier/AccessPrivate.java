package accessModifiier;

interface Manusia{
    public void makan();
}

public class AccessPrivate implements Manusia{
    
    public void makan(){
        System.out.println("Makan");
    }
    
    private String nama;
    private  int angkatan;
    
    public AccessPrivate(String nama, int angkatan) {
        this.nama = nama;
        this.angkatan = angkatan;
    }
    
    public AccessPrivate(){};

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setAngkatan(int angkatan) {
        this.angkatan = angkatan;
    }

    public String getNama() {
        return nama;
    }

    public int getAngkatan() {
        return angkatan;
    }
    public void tampil(){
        System.out.println("Nama" + nama);
    }
}

class Test{
    public static void main(String[] args) {
        AccessPrivate mhs1 = new AccessPrivate("ichwal",2023);
//        System.out.println("Nama : "+mhs1.getNama());
        AccessPrivate mhs2 = new AccessPrivate();
        mhs2.setNama("Ichwal");
        
    }
}
class test2 extends AccessPrivate{
    AccessPrivate mhs2 = new AccessPrivate();
    public void tampil1(){
        mhs2.nama = "ichwal";
    }
}