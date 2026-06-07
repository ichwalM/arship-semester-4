
package main;

import accessPublic.Public;
import accessModifiier.AccessPrivate;



public class Main {
    public static void main(String[] args) {
        
//        access Private
        AccessPrivate mahasiswa = new AccessPrivate("Ichwal", 2023);
        System.out.println("Nama     : "+mahasiswa.getNama());
        System.out.println("Angkatan : "+mahasiswa.getAngkatan());
        
//        access Public
        Public identitas = new Public();
        identitas.nama="aksan";
        identitas.Umur=58;
        System.out.println("Nama    : "+identitas.nama);
        System.out.println("umur    : "+identitas.Umur);
        
        
        // acces protect
        Ahsan ahs = new Ahsan();
//        ahs.tesuara();
    }
    
}
