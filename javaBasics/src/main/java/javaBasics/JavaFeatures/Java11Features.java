package javaBasics;

import java.util.function.Consumer;

/**
 * Question: Explain the new features introduced in Java 11.
 * 
 * Key Features:
 * 1. String Methods: isBlank(), lines(), strip(), stripLeading(), stripTrailing(), repeat().
 * 2. Local-Variable Syntax for Lambda Parameters: using 'var' inside lambda parameters.
 * 3. File Methods: New utility methods like Files.readString() and Files.writeString().
 * 4. HTTP Client API: Standardized HttpClient (java.net.http) introduced in Java 11.
 */
public class Java11Features {

    public static void main(String[] args) {
        System.out.println("--- 1. New String Methods Demo ---");
        String multiline = "Hello\n \nWorld";
        System.out.println("Lines count (ignoring blank): " + multiline.lines().filter(line -> !line.isBlank()).count());
        
        String whitespaceStr = "   Java 11   ";
        System.out.println("Stripped: '" + whitespaceStr.strip() + "'"); // Better than trim() as it supports Unicode
        System.out.println("Repeated: " + "Na".repeat(4) + " Batman!");

        System.out.println("\n--- 2. Local-Variable Syntax for Lambda Demo ---");
        // Using 'var' in lambda helps if you need to apply annotations like @NonNull to the parameter
        Consumer<String> consumer = (var msg) -> System.out.println("Message: " + msg);
        consumer.accept("Hello with var!");
    }
}
