package tugas_pendahuluan_4;

public interface Animal {
    void eat();
}

abstract class Mammal {
    abstract void sleep();
}

class Dog extends Mammal implements Animal {
    public void eat() {
        System.out.println("Dog is eating.");
    }
    
    public void sleep() {
        System.out.println("Dog is sleeping.");
    }
}

public class Main {
    public static void main(String[] args) {
        Dog dog = new Dog();
        dog.eat();
        dog.sleep();
    }
}