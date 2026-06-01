package com.interview.javaconcepts.lambdas_streams.practice;

import java.util.*;

/**
 * 🎓 Java Stream Reduce In-Depth Demonstration
 * 
 * This class demonstrates the inner workings of the Stream.reduce() terminal operation.
 * It covers all three overloaded signatures, explains how parallel reductions combine sub-results,
 * and demonstrates how counting and summing are built on top of reduce.
 */
public class ReduceInDepthDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("⚡ STREAM REDUCE IN-DEPTH MASTERCLASS");
        System.out.println("==================================================");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        // ----------------------------------------------------------------------
        // SIGNATURE 1: Optional<T> reduce(BinaryOperator<T> accumulator)
        // ----------------------------------------------------------------------
        // No identity value provided. Returns Optional because the stream might be empty.
        Optional<Integer> sumOpt = numbers.stream()
                .reduce((accumulator, element) -> accumulator + element);
        System.out.println("Signature 1 (Sum without Identity): " + sumOpt.orElse(0));

        // ----------------------------------------------------------------------
        // SIGNATURE 2: T reduce(T identity, BinaryOperator<T> accumulator)
        // ----------------------------------------------------------------------
        // Identity value (0) acts as initial value and fallback. Returns primitive/object directly.
        int sumWithIdentity = numbers.stream()
                .reduce(0, (accumulator, element) -> accumulator + element);
        System.out.println("Signature 2 (Sum with Identity 0): " + sumWithIdentity);

        // ----------------------------------------------------------------------
        // SIGNATURE 3: <U> U reduce(U identity, BiFunction<U,? super T,U> accumulator, BinaryOperator<U> combiner)
        // ----------------------------------------------------------------------
        // Used when the accumulated type (U) differs from the stream element type (T).
        // e.g. Stream of Strings (T), but we want to reduce to an Integer character count (U).
        List<String> words = Arrays.asList("apple", "pear", "banana");

        // Running Sequentially (Combiner is bypassed)
        int totalCharsSeq = words.stream()
                .reduce(
                        0, // Identity (Integer)
                        (accumulatedLength, word) -> {
                            // Accumulator: combines Integer and String
                            // System.out.println("Accumulator (Seq): accumulatedLength=" + accumulatedLength + ", word=" + word);
                            return accumulatedLength + word.length();
                        },
                        (length1, length2) -> {
                            // Combiner: combines Integer and Integer
                            // Bypassed in sequential stream execution
                            System.out.println("Combiner (Seq): combining " + length1 + " and " + length2);
                            return length1 + length2;
                        }
                );
        System.out.println("Signature 3 (Total characters - Sequential): " + totalCharsSeq);

        // Running in Parallel (Combiner is executed!)
        System.out.println("\n--- Starting Parallel Stream Reduction (Watch Combiner logs) ---");
        int totalCharsParallel = words.parallelStream()
                .reduce(
                        0,
                        (accumulatedLength, word) -> {
                            System.out.println("Accumulator [Thread " + Thread.currentThread().getName() + "]: word=" + word + " -> len=" + word.length());
                            return accumulatedLength + word.length();
                        },
                        (length1, length2) -> {
                            System.out.println("Combiner [Thread " + Thread.currentThread().getName() + "]: combining len1=" + length1 + " and len2=" + length2);
                            return length1 + length2;
                        }
                );
        System.out.println("Signature 3 (Total characters - Parallel): " + totalCharsParallel);
        System.out.println("------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------
        // SIMULATING COUNTING AND SUMMING PURELY USING REDUCE
        // ----------------------------------------------------------------------
        // 1. Simulating Collectors.counting()
        // Maps each element to 1L and sums them up.
        long simulatedCount = words.stream()
                .reduce(0L, (count, word) -> count + 1L, Long::sum);
        System.out.println("Simulated Collectors.counting() via reduce: " + simulatedCount);

        // 2. Simulating Collectors.summingInt(String::length)
        // Maps each word to its length and sums them up.
        int simulatedSum = words.stream()
                .reduce(0, (sum, word) -> sum + word.length(), Integer::sum);
        System.out.println("Simulated Collectors.summingInt() via reduce: " + simulatedSum);
    }
}
