package accesDefault;
import AccessDefault2.User2;
class User {
    String nama;
    int umur;
    
}

public class Default extends User2 {
    public static void main(String[] args) {
        nama="Aan";
        User user1 = new User();
        user1.nama = "Aan Maulana";
        user1.umur = 17;
    }
}
