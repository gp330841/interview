package com.interview.javaconcepts.lambdas_streams.practice;

import java.util.LinkedHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;



public class FirstNonRepeatingCharacter {
    static void main() {
        String str = "swisswp";

        str.chars()
                .mapToObj(ch-> (char)ch)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .filter(entry-> entry.getValue()==1)
                .map(entry->entry.getKey())
                .findFirst().ifPresent(System.out::println);

    }
}
