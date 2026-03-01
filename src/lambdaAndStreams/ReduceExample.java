package lambdaAndStreams;

import java.util.List;
import java.util.Optional;

public class ReduceExample {
    public static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        Optional<Integer> sum = numbers.stream()
            .reduce(Integer::min);

        System.out.println(sum); // Output: 15
    }
}