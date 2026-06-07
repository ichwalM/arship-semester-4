public class number4 {
    public static void main(String[] args) {
        System.out.println("for loop");
        for (int i = 0; i < 5; i++) {
            System.out.println("Iterasi ke-" + i);
        }
        System.out.println("while loop");
        int kondisi = 0;
        while (kondisi != 5 ) {
            System.out.println("kondisi ke-" + kondisi);
            kondisi+=1;
        }
        System.out.println("do while loop");
        do {
            kondisi-=1;
            System.out.println("kondisi ke-" + kondisi);
        }while(kondisi!=0);
        
    }
}