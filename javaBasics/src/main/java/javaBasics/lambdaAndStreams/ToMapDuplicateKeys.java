package lambdaAndStreams;

import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

public class ToMapDuplicateKeys {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Bob");

        Map<String, Integer> nameLengthMap = names.stream()
                                                  .collect(Collectors.toMap(
                                                          name -> name,
                                                          String::length,
                                                          (existing, replacement) -> existing));

        System.out.println(nameLengthMap);  // Output: {Alice=5, Bob=3, Charlie=7}
    }
}