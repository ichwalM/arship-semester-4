public class Main {
    public static void main(String[] args) {
        Mobil mobilBiasa = new Mobil();
        Mobil mobilRoad = new MobilRoad();
        Mobil mobilSport = new MobilSport();        
        System.out.println("Perilaku berbagai jenis mobil:");
        mobilBiasa.bergerak();
        mobilRoad.bergerak();
        mobilSport.bergerak();
    }
} 
