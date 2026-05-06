package javaBasics;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Question: Explain the new features introduced in Java 8.
 * 
 * Key Features:
 * 1. Lambda Expressions: Anonymous functions to implement functional interfaces.
 * 2. Stream API: Functional-style operations on streams of elements (map, filter, reduce).
 * 3. Optional Class: Container object used to contain not-null objects, preventing NullPointerException.
 * 4. Default Methods: Interfaces can now have default method implementations.
 * 5. Method References: Shorthand syntax for a lambda expression that contains just one method call.
 */
public class Java8Features {

    // Functional Interface
    @FunctionalInterface
    interface MathOperation {
        int operate(int a, int b);
    }

    interface Greeting {
        default void sayHello() {
            System.out.println("Hello from Default Method!");
        }
    }

    static class EnglishGreeting implements Greeting {
        // Can override or use the default method
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Lambda Expression Demo ---");
        MathOperation addition = (a, b) -> a + b;
        System.out.println("10 + 5 = " + addition.operate(10, 5));

        System.out.println("\n--- 2. Stream API Demo ---");
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        List<String> filteredNames = names.stream()
            .filter(name -> name.startsWith("C"))
            .collect(Collectors.toList());
        System.out.println("Names starting with 'C': " + filteredNames);

        System.out.println("\n--- 3. Optional Demo ---");
        Optional<String> optionalStr = Optional.ofNullable(null);
        System.out.println("Optional value: " + optionalStr.orElse("Default Value"));

        System.out.println("\n--- 4. Default Method Demo ---");
        Greeting greeting = new EnglishGreeting();
        greeting.sayHello(); // Calls the default method in interface

        System.out.println("\n--- 5. Method Reference Demo ---");
        List<String> fruits = Arrays.asList("Apple", "Banana", "Orange");
        // Instead of fruit -> System.out.println(fruit)
        fruits.forEach(System.out::println); 
    }
}
