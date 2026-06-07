package tgs_pendahuluan4;
interface Kendaraan {
    void nyalakanMesin();
}

// Abstract Class
abstract class Mobil implements Kendaraan {
    abstract void bukaPintu();

    @Override
    public void nyalakanMesin() {
        System.out.println("Mesin mobil dinyalakan.");
    }
}

// Class turunan
class Sedang extends Mobil {
    @Override
    void bukaPintu() {
        System.out.println("Pintu mobil sedan dibuka.");
    }
}

public class Main {
    public static void main(String[] args) {
        Sedang sedanKu = new Sedang();
        sedanKu.nyalakanMesin();
        sedanKu.bukaPintu();
    }
}
