package javaBasics;

import java.io.IOException;

/**
 * Question: Checked vs Unchecked Exceptions. What is final, finally, and finalize?
 * 
 * Critical Points:
 * - Checked Exceptions: Checked at compile time (must be caught or thrown). They extend Exception.
 * - Unchecked Exceptions: Runtime exceptions (e.g., NullPointerException). They extend RuntimeException.
 * - final: Keyword to prevent inheritance (class), overriding (method), or modification (variable).
 * - finally: Block that ALWAYS executes after try-catch (used for resource cleanup).
 * - finalize: Method called by GC before object destruction (deprecated).
 */
public class ExceptionHandlingBasics {

    public static void doSomething() throws IOException {
        // Checked exception example
        throw new IOException("Failed to load file");
    }

    public static void main(String[] args) {
        System.out.println("--- Exception Handling Demo ---");
        try {
            doSomething();
        } catch (IOException e) {
            System.out.println("Caught checked exception: " + e.getMessage());
        } finally {
            System.out.println("This 'finally' block always executes.");
        }

        System.out.println("\n--- Unchecked Exception Demo ---");
        try {
            int result = 10 / 0; // Throws ArithmeticException
        } catch (ArithmeticException e) {
            System.out.println("Caught unchecked exception: " + e.getMessage());
        }

        System.out.println("\n--- Final Keyword Demo ---");
        final int MAX_USERS = 100;
        // MAX_USERS = 200; // This would cause a compilation error!
        System.out.println("Final variable (can't be updated): " + MAX_USERS);
    }
}
