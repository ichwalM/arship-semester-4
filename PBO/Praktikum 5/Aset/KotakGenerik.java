public class KotakGenerik<T> {
    private T isi;

    public KotakGenerik(T isi) {
        this.isi = isi;
    }

    public T getIsi() {
        return isi;
    }

    // Generic method untuk membandingkan isi dengan objek lain
    public <U> boolean samaDengan(KotakGenerik<U> kotakLain) {
        return this.isi.equals(kotakLain.getIsi());
    }
}