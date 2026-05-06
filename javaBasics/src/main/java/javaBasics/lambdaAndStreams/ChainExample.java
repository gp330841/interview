package lambdaAndStreams;

import java.util.List;
import java.util.stream.Collectors;

public class ChainExample {
    static void main() {
        List<String> names = List.of("Alice", "Bob", "Charlie", "David", "Eve");

        List<Integer> filteredLengths = names.stream()
            .filter(name -> name.length() > 3) // Filter names longer than 3 characters
            .map(String::length)                // Map to their lengths
            .collect(Collectors.toList());

        System.out.println(filteredLengths); // Output: [5, 7]
    }
}