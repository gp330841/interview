package com.interview.javaconcepts.topic13_misc_tests;

import java.util.*;

public class JavaBasicsTest {
    static void main() {

        Map<Character, Integer> map = new HashMap<>();
        Queue<Map.Entry<Character, Integer>> pq = new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());
        pq.addAll(map.entrySet());

        List<String> ans = new ArrayList();
        ans.sort(Comparator.naturalOrder());

        String[] words = new String[0];
        Arrays.sort(words);

        System.out.printf("%s: %d%n", "count", 1);
    }
}
