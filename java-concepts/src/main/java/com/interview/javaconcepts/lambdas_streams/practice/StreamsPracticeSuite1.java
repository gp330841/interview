package com.interview.javaconcepts.lambdas_streams.practice;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 🎓 Streams Practice Suite 1 (Foundational & Intermediate Challenges)
 * 
 * This workbook contains Questions 1 to 15. Each question has a dedicated method
 * demonstrating the declarative Stream solution. Run this class to view the verified outputs!
 */
public class StreamsPracticeSuite1 {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("🔥 STREAMS PRACTICE SUITE 1 (Q1 - Q15)");
        System.out.println("==================================================");

        // Q1: Even Numbers multiplied by 2
        System.out.println("Q1: " + q1EvenDoubled(Arrays.asList(1, 2, 3, 4, 5, 6)));

        // Q2: Strings to Uppercase
        System.out.println("Q2: " + q2ToUppercase(Arrays.asList("apple", "banana", "kiwi")));

        // Q3: Sum of all Integers
        System.out.println("Q3: " + q3SumList(Arrays.asList(10, 20, 30, 40)));

        // Q4: Filter starting with "A" and length > 3
        System.out.println("Q4: " + q4FilterStrings(Arrays.asList("Alex", "abc", "Andrew", "Amy", "Alabaster")));

        // Q5: Max and Min of a list
        System.out.println("Q5: " + q5MaxMin(Arrays.asList(5, 9, 1, 14, 3, 8)));

        // Q6: Count strings length > 5
        System.out.println("Q6: " + q6CountLongStrings(Arrays.asList("elephant", "dog", "giraffe", "cat")));

        // Q7: Join strings with hyphen
        System.out.println("Q7: " + q7JoinWithHyphen(Arrays.asList("Java", "is", "awesome")));

        // Q8: Deduplicate and sort descending
        System.out.println("Q8: " + q8DeduplicateSort(Arrays.asList(5, 2, 9, 2, 5, 1)));

        // Q9: Find first element with default
        System.out.println("Q9: " + q9FindFirstWithDefault(Arrays.asList("A", "B", "C")));
        System.out.println("Q9 (Empty): " + q9FindFirstWithDefault(Collections.emptyList()));

        // Q10: Flatten nested lists
        List<List<Integer>> nested = Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5));
        System.out.println("Q10: " + q10FlattenLists(nested));

        // Q11: Partition odd vs even
        System.out.println("Q11: " + q11PartitionOddEven(Arrays.asList(1, 2, 3, 4, 5, 6)));

        // Q12: Group strings by length
        System.out.println("Q12: " + q12GroupByLength(Arrays.asList("to", "the", "sky", "above")));

        // Q13: Average value of doubles
        System.out.println("Q13: " + q13AverageDoubles(Arrays.asList(1.5, 2.5, 3.5, 4.5)));

        // Q14: Find longest string
        System.out.println("Q14: " + q14FindLongestString(Arrays.asList("short", "extremelylongword", "tiny")));

        // Q15: Check if all are positive
        System.out.println("Q15 (All Pos): " + q15AreAllPositive(Arrays.asList(1, 2, 3)));
        System.out.println("Q15 (Has Neg): " + q15AreAllPositive(Arrays.asList(1, -2, 3)));
    }

    // Q1: Find even numbers in a list and multiply them by 2.
    public static List<Integer> q1EvenDoubled(List<Integer> list) {
        return list.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * 2)
                .collect(Collectors.toList());
    }

    // Q2: Convert all strings in a list to uppercase and collect to list.
    public static List<String> q2ToUppercase(List<String> list) {
        return list.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    // Q3: Find the sum of all elements in a list of integers.
    public static int q3SumList(List<Integer> list) {
        return list.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    // Q4: Filter strings starting with "A" (case-insensitive) and length > 3.
    public static List<String> q4FilterStrings(List<String> list) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith("a"))
                .filter(s -> s.length() > 3)
                .collect(Collectors.toList());
    }

    // Q5: Find the maximum and minimum elements in a list of numbers.
    public static String q5MaxMin(List<Integer> list) {
        int max = list.stream().max(Integer::compareTo).orElse(-1);
        int min = list.stream().min(Integer::compareTo).orElse(-1);
        return "Max: " + max + ", Min: " + min;
    }

    // Q6: Count the number of strings in a list whose length is greater than 5.
    public static long q6CountLongStrings(List<String> list) {
        return list.stream()
                .filter(s -> s.length() > 5)
                .count();
    }

    // Q7: Concatenate a list of strings with a hyphen (-) delimiter.
    public static String q7JoinWithHyphen(List<String> list) {
        return list.stream()
                .collect(Collectors.joining("-"));
    }

    // Q8: Remove duplicate elements from a list of integers and sort it descending.
    public static List<Integer> q8DeduplicateSort(List<Integer> list) {
        return list.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    // Q9: Find the first element of a stream, returning "Empty" if the stream is empty.
    public static String q9FindFirstWithDefault(List<String> list) {
        return list.stream()
                .findFirst()
                .orElse("Empty");
    }

    // Q10: Flatten a nested list of lists of integers into a single flat list.
    public static List<Integer> q10FlattenLists(List<List<Integer>> nestedList) {
        return nestedList.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    // Q11: Partition a list of integers into odd and even numbers.
    public static Map<Boolean, List<Integer>> q11PartitionOddEven(List<Integer> list) {
        return list.stream()
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
    }

    // Q12: Group a list of strings by their length.
    public static Map<Integer, List<String>> q12GroupByLength(List<String> list) {
        return list.stream()
                .collect(Collectors.groupingBy(String::length));
    }

    // Q13: Find the average value of a list of doubles.
    public static double q13AverageDoubles(List<Double> list) {
        return list.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    // Q14: Find the longest string in a list.
    public static String q14FindLongestString(List<String> list) {
        return list.stream()
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }

    // Q15: Check if all numbers in a list are positive.
    public static boolean q15AreAllPositive(List<Integer> list) {
        return list.stream()
                .allMatch(n -> n > 0);
    }
}
