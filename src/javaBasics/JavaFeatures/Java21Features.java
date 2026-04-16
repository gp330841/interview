package javaBasics;

import java.util.LinkedHashMap;
import java.util.SequencedMap;
import java.util.concurrent.Executors;

/**
 * Question: Explain the new standard features introduced in Java 21 (LTS release).
 * 
 * Key Features:
 * 1. Virtual Threads (Project Loom): Lightweight threads that dramatically reduce the effort of writing, 
 *    maintaining, and observing high-throughput concurrent applications.
 * 2. Sequenced Collections: New interfaces (`SequencedCollection`, `SequencedSet`, `SequencedMap`) 
 *    representing collections with a defined encounter order, making it easier to fetch the first/last elements.
 * 3. Pattern Matching for switch: Allows switch expressions and statements to test against 
 *    multiple patterns flexibly, significantly simplifying complex data queries.
 * 4. Record Patterns: Deconstruct record values directly within pattern matching statements.
 */
public class Java21Features {

    record Point(int x, int y) {}

    public static void main(String[] args) {
        System.out.println("--- 1. Virtual Threads Demo ---");
        // Lightweight threads allowing you to run millions of concurrent tasks cheaply
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> System.out.println("Running in a virtual thread: " + Thread.currentThread()));
        }

        System.out.println("\n--- 2. Sequenced Collections Demo ---");
        SequencedMap<String, String> seqMap = new LinkedHashMap<>();
        seqMap.put("first", "A");
        seqMap.put("second", "B");
        System.out.println("First Entry natively fetched: " + seqMap.firstEntry()); 
        System.out.println("Reversed Map access natively: " + seqMap.reversed());

        System.out.println("\n--- 3. Pattern Matching for switch & Record Patterns Demo ---");
        Object obj = new Point(10, 20);
        
        // Deconstructing the record right inside the switch statement
        String result = switch (obj) {
            case Point(int x, int y) -> "It's a Point with coordinates: " + x + ", " + y;
            case String s -> "It's a String of length " + s.length();
            case null, default -> "Unknown Object";
        };
        System.out.println("Switch Result: " + result);
    }
}
