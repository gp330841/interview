package javaBasics;

/**
 * Question: Explain basic OOPs concepts (Encapsulation, Inheritance, Polymorphism, Abstraction)
 * 
 * Abstract Class vs Interface:
 * - Abstract Class: Used for partial abstraction, can hold state (instance variables).
 * - Interface: Full abstraction (traditionally), defines a strict capability contract.
 */
public class OOPsConcepts {

    // 1. Encapsulation: Data hiding and abstraction via private fields and public accessors
    static class Account {
        private double balance; // Hidden data

        public void deposit(double amount) {
            if (amount > 0) balance += amount;
        }

        public double getBalance() {
            return balance;
        }
    }

    // 2. Abstraction & 3. Inheritance
    abstract static class Animal {
        abstract void makeSound(); // Abstract method
        void sleep() {
            System.out.println("Zzz...");
        }
    }

    // Inheritance & 4. Polymorphism (Overriding)
    static class Dog extends Animal {
        @Override
        void makeSound() {
            System.out.println("Woof Woof");
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Encapsulation ---");
        Account acc = new Account();
        acc.deposit(100);
        System.out.println("Balance: " + acc.getBalance());

        System.out.println("\n--- Abstraction, Inheritance & Polymorphism ---");
        Animal myDog = new Dog(); // Upcasting (Polymorphism)
        myDog.makeSound();
        myDog.sleep();
    }
}
