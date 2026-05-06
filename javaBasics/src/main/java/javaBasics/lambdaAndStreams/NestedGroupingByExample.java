package lambdaAndStreams;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

public class NestedGroupingByExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David", "Ella", "Ea");

        Map<Integer, Map<Character, List<String>>> grouped = names.stream()
            .collect(Collectors.groupingBy(String::length, 
                Collectors.groupingBy(name -> name.charAt(0))));
        System.out.println(grouped);
        // Output: {3={B=[Bob]}, 4={E=[Ella]}, 5={A=[Alice], D=[David]}, 7={C=[Charlie]}}

        Map<String, Map<Character, Long>> group2 = names.stream()
                .collect(Collectors.groupingBy(name->name, Collectors.groupingBy(name->name.charAt(0), Collectors.counting())));
        System.out.println(group2);
    }
}