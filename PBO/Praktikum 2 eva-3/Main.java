class Hewan {
    public void suara() {
        System.out.println("Hewan ini bersuara.");
    }
}

class Kucing extends Hewan {
    @Override
    public void suara() {
        System.out.println("Kucing bersuara: Meow!");
    }
}


public class Main {
    public static void main(String[] args) {
        Hewan hewan = new Kucing();
        hewan.suara(); // Output: Kucing mengeong: Meow Meow!
    }
}