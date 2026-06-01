package com.interview.javaconcepts.lambdas_streams;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Frequency Counter Demonstration
 * 
 * Demonstrates multiple ways to group and count element frequencies using Java Streams,
 * specifically showing how to handle the common interview obstacle where Collectors.counting()
 * returns a Long instead of an Integer.
 */
public class FrequencyCounter {
    private static final List<String> sourceData =
            new ArrayList<>(Arrays.asList("apple", "banana", "apple", "orange", "banana", "apple"));


    public static void main(String[] args) {

        Map<String, Integer> map = sourceData
                .stream()
                .collect(
                        Collectors.groupingBy(
                                x->x,
                                Collectors.reducing(
                                        x->x,
                                        Integer::sum
                                )
                        )
                );
        System.out.println(map);

        System.out.println("Source Data: " + sourceData);

        // 1. Using collectingAndThen to convert Long to Integer
        Map<String, Integer> freq1 = new HashMap<>();
        countFrequenciesCollectingAndThen(sourceData, freq1);
        System.out.println("Result 1 (collectingAndThen): " + freq1);

        // 2. Using summingInt
        Map<String, Integer> freq2 = new HashMap<>();
        countFrequenciesSummingInt(sourceData, freq2);
        System.out.println("Result 2 (summingInt): " + freq2);

        // 3. Using toMap
        Map<String, Integer> freq3 = new HashMap<>();
        countFrequenciesToMap(sourceData, freq3);
        System.out.println("Result 3 (toMap): " + freq3);

        // 4. Using Map.merge (clean side-effect / standard loop hybrid)
        Map<String, Integer> freq4 = new HashMap<>();
        countFrequenciesMerge(sourceData, freq4);
        System.out.println("Result 4 (Map.merge): " + freq4);
    }

    /**
     * SOLUTION A: Using Collectors.collectingAndThen
     * 
     * Collectors.counting() returns a Long. By wrapping it with Collectors.collectingAndThen,
     * we can transform the Long to an Integer using Long::intValue after counting completes.
     */
    private static void countFrequenciesCollectingAndThen(List<String> words, Map<String, Integer> frequencies) {
        Map<String, Integer> counted = words.stream()
                .collect(Collectors.groupingBy(
                        word -> word,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        frequencies.putAll(counted);
    }

    /**
     * SOLUTION B: Using Collectors.summingInt
     * 
     * Instead of using Collectors.counting(), we can use Collectors.summingInt(w -> 1).
     * Since summingInt aggregates elements into an Integer, the resulting map contains
     * Integer values directly, bypassing the type issue completely.
     */
    private static void countFrequenciesSummingInt(List<String> words, Map<String, Integer> frequencies) {
        Map<String, Integer> counted = words.stream()
                .collect(Collectors.groupingBy(
                        word -> word,
                        Collectors.summingInt(w -> 1)
                ));
        frequencies.putAll(counted);
    }

    /**
     * SOLUTION C: Using Collectors.toMap
     * 
     * Collectors.toMap allows you to map keys to value '1' and combine duplicates using 
     * an merge function like Integer::sum. This also returns a Map<String, Integer> directly.
     */
    private static void countFrequenciesToMap(List<String> words, Map<String, Integer> frequencies) {
        Map<String, Integer> counted = words.stream()
                .collect(Collectors.toMap(
                        word -> word,
                        word -> 1,
                        Integer::sum
                ));
        frequencies.putAll(counted);
    }

    /**
     * SOLUTION D: Using Map.merge
     * 
     * If the objective is simply to mutate the passed-in frequencies map directly 
     * without creating a intermediate Map, using Map.merge inside a stream forEach is 
     * highly performant and elegant.
     */
    private static void countFrequenciesMerge(List<String> words, Map<String, Integer> frequencies) {
        words.forEach(word -> frequencies.merge(word, 1, Integer::sum));
    }
}
