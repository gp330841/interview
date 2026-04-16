package javaBasics;

import java.util.List;

/**
 * Question: Explain the new features introduced targeting Java 25 (LTS).
 * 
 * Key Features (Standardized after preview cycles in Java 22-24):
 * 1. Unnamed Variables & Patterns (`_`): Variables that can be legally initialized but intentionally not used. 
 *    Particularly helpful in try-catch blocks or heavily looped iterators.
 * 2. String Templates: Simplifies format-style string interpolation natively in Java using `STR."..."`.
 * 3. Statements before super(): Constructors can now have statements before making a `super(...)` call, 
 *    which helps with fail-fast validation before allocating memory to the parent.
 * 4. Scoped Values and Structured Concurrency: Modern, thread-safe alternatives to ThreadLocals representing 
 *    stronger fault-tolerant concurrent code structures.
 */
public class Java25Features {

    public static void main(String[] args) {
        System.out.println("--- 1. Unnamed Variables Demo ---");
        try {
            int num = Integer.parseInt("Not A Number");
        } catch (NumberFormatException _) {
            // Replaced traditional 'e' with '_' to intentionally ignore the exception variable safely without lint warnings
            System.out.println("Handled formatting exception, ignored the exception variable explicitly utilizing '_'.");
        }

        List<String> items = List.of("One", "Two", "Three");
        int count = 0;
        for (String _ : items) {
            // Using '_' as we just want to count iterations without processing the string
            count++;
        }
        System.out.println("Total items counted using loop with '_': " + count);

        System.out.println("\n--- 2. String Templates Demo ---");
        String name = "Java Developer";
        int experience = 5;
        // Native string templating approach replacing string concatenation
        // String result = STR."Welcome, \\{name}! You have \\{experience} years of experience.";
        System.out.println("String templates enable concise interpolation directly using: STR.\"Welcome \\{name}!\"");
        
        System.out.println("\n--- 3. Statements before super() ---");
        System.out.println("Constructors are now legally allowed to perform logic before calling super(), making input validation feasible immediately!");
    }
}
