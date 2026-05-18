package com.interview.javaconcepts.topic08_generics;

import java.util.ArrayList;
import java.util.List;

/**
 * Generics Basics and Wildcards
 * 
 * 1. Why Generics?
 *    - Type safety at compile time.
 *    - Elimination of casts.
 *    - Generic algorithms.
 * 
 * 2. Type Erasure:
 *    - Generics are a compile-time feature. At runtime, generic type information is erased.
 *    - `List<String>` and `List<Integer>` both become `List` at runtime.
 * 
 * 3. Wildcards:
 *    - `? extends T` (Upper Bound): Read-only. You can read items of type T, but cannot add (except null). Suitable for Producer. (PECS: Producer Extends, Consumer Super).
 *    - `? super T` (Lower Bound): Write-only. You can add items of type T or its subclasses. Suitable for Consumer.
 */
public class GenericsBasics {

    // PECS: Producer Extends. We can read from this list, but we cannot add anything except null.
    public static double sumOfNumbers(List<? extends Number> numbers) {
        double sum = 0.0;
        for (Number n : numbers) {
            sum += n.doubleValue();
        }
        // numbers.add(10); // COMPILE ERROR: cannot add to <? extends Number>
        return sum;
    }

    // PECS: Consumer Super. We can add integers to this list, but reading returns Object.
    public static void addNumbers(List<? super Integer> list) {
        list.add(1);
        list.add(2);
        // Integer val = list.get(0); // COMPILE ERROR: get() returns Object, not Integer
    }

    public static void main(String[] args) {
        List<Integer> ints = new ArrayList<>(List.of(1, 2, 3));
        List<Double> doubles = new ArrayList<>(List.of(1.1, 2.2, 3.3));

        System.out.println("Sum of ints: " + sumOfNumbers(ints));
        System.out.println("Sum of doubles: " + sumOfNumbers(doubles));

        List<Number> numList = new ArrayList<>();
        addNumbers(numList);
        System.out.println("numList after adding: " + numList);
    }
}
