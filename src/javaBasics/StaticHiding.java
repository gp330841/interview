package javaBasics;

/**
 * Question: Can we override a static method?
 * 
 * Critical Points:
 * - No. Method overriding requires dynamic binding (runtime based on object type). 
 *   Static methods use static binding (compile time based on reference type).
 * - If a subclass defines a static method with the same signature as the parent, 
 *   it "hides" the superclass method. It does NOT override it.
 */
public class StaticHiding {

    static class Parent {
        static void printMessage() {
            System.out.println("Message from Parent class");
        }
    }

    static class Child extends Parent {
        // This is Method Hiding, NOT Overriding
        static void printMessage() {
            System.out.println("Message from Child class");
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Static Method Call Demo ---");
        Parent.printMessage(); // Prints "Parent"
        Child.printMessage();  // Prints "Child"
        
        System.out.println("\n--- Polymorphism Test ---");
        Parent p = new Child();
        
        // Warning: Static member accessed via instance reference.
        // It uses the type of the reference variable (Parent) at compile time, 
        // NOT the object type at runtime!
        p.printMessage(); // Prints "Message from Parent class"
    }
}
