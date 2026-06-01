package com.interview.javaconcepts.lambdas_streams.practice;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Tricky Java Streams Practice Challenges
 * 
 * Target: Senior Java Backend Engineer
 * This class provides highly standard, complex, and tricky coding puzzles solved purely
 * using Java Streams, handling real-world edge cases like nulls, duplicates, and empty lists.
 */
public class TrickyStreamChallenges {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("🚀 TRICKY JAVA STREAMS CHALLENGES");
        System.out.println("==================================================");

        // Challenge 1: Find Second Highest Number
        List<Integer> nums = Arrays.asList(3, 5, 9, 2, 9, 8, null, 1, 8, 10, 10);
        findSecondHighest(nums).ifPresentOrElse(
            val -> System.out.println("Challenge 1 (Second Highest): " + val),
            () -> System.out.println("Challenge 1: No second highest found")
        );

        // Challenge 2: Group strings by length and join them with commas
        List<String> words = Arrays.asList("apple", "pie", "banana", "pear", "kiwi", "fig", "apricot");
        System.out.println("Challenge 2 (Group & Join): " + groupAndJoin(words));

        // Challenge 3: Partition into Prime and Non-Prime
        List<Integer> range = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        System.out.println("Challenge 3 (Partition Primes): " + partitionPrimes(range));

        // Challenge 4: Merge Maps with Summed Values
        Map<String, Integer> map1 = Map.of("A", 10, "B", 20, "C", 30);
        Map<String, Integer> map2 = Map.of("B", 5, "C", 15, "D", 25);
        System.out.println("Challenge 4 (Merge Maps): " + mergeMaps(map1, map2));

        // Challenge 5: Find most frequent words in a list of sentences (FlatMap + Counting)
        List<String> sentences = Arrays.asList(
            "Java Streams are amazing",
            "Functional programming in Java is powerful",
            "Streams are awesome, Java is awesome too"
        );
        System.out.println("Challenge 5 (Most Frequent Word): " + findMostFrequentWord(sentences));
    }

    /**
     * CHALLENGE 1: Find the second highest number in a list of integers.
     * Edge Cases: Handles duplicates (e.g. 10, 10 are the top values, so second highest is 9),
     * null elements in the stream, and returning empty Optional if not enough distinct values exist.
     */
    public static Optional<Integer> findSecondHighest(List<Integer> numbers) {
        if (numbers == null) return Optional.empty();

        return numbers.stream()
                .filter(Objects::nonNull)              // 1. Remove nulls
                .distinct()                             // 2. Remove duplicates
                .sorted(Comparator.reverseOrder())     // 3. Sort descending
                .skip(1)                                // 4. Skip the highest element
                .findFirst();                           // 5. Get the second highest
    }

    /**
     * CHALLENGE 2: Group a list of strings by their length, and join the grouped strings 
     * in each group with commas (e.g., key=3, value="pie, fig").
     */
    public static Map<Integer, String> groupAndJoin(List<String> strings) {
        if (strings == null) return Collections.emptyMap();

        return strings.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        String::length,
                        Collectors.joining(", ") // Downstream collector to concatenate strings in each group
                ));
    }

    /**
     * CHALLENGE 3: Partition a list of numbers into Prime and Non-Prime.
     * Uses partitioningBy with a custom prime checker predicate.
     */
    public static Map<Boolean, List<Integer>> partitionPrimes(List<Integer> numbers) {
        if (numbers == null) return Collections.emptyMap();

        return numbers.stream()
                .filter(Objects::nonNull)
                .filter(n -> n >= 2)
                .collect(Collectors.partitioningBy(
                        TrickyStreamChallenges::isPrime
                ));
    }

    private static boolean isPrime(int number) {
        return number > 1 && IntStream.rangeClosed(2, (int) Math.sqrt(number))
                .noneMatch(i -> number % i == 0);
    }

    /**
     * CHALLENGE 4: Merge two maps of String -> Integer. If keys overlap, sum their values.
     * Uses stream concat and Collectors.toMap with a merge function.
     */
    public static Map<String, Integer> mergeMaps(Map<String, Integer> m1, Map<String, Integer> m2) {
        if (m1 == null || m2 == null) return Collections.emptyMap();

        return java.util.stream.Stream.concat(m1.entrySet().stream(), m2.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::sum // Merge function to sum values for identical keys
                ));
    }

    /**
     * CHALLENGE 5: Find the single most frequent word across all sentences.
     * Uses flatMap, normalizing case and punctuation, groupingBy, and a reduction to find max count.
     */
    public static String findMostFrequentWord(List<String> sentences) {
        if (sentences == null || sentences.isEmpty()) return "";

        return sentences.stream()
                .filter(Objects::nonNull)
                .map(s -> s.replaceAll("[^a-zA-Z ]", "").toLowerCase()) // Normalize punctuation & case
                .flatMap(s -> Arrays.stream(s.split("\\s+")))           // Flatten to Stream of words
                .filter(w -> !w.isBlank())
                .collect(Collectors.groupingBy(w -> w, Collectors.counting())) // Count frequencies
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())                       // Find Entry with max count
                .map(Map.Entry::getKey)
                .orElse("");
    }
}
