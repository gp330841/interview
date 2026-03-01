package lambdaAndStreams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamBasics {
    static void main() {

        IntStream.range(0, 10).sum();
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        int sum = numbers.parallelStream()
                .mapToInt(Integer::intValue)
                .sum();
        numbers.parallelStream().mapToInt(Integer::intValue).close();

        System.out.println("Parallel Sum: " + sum);
    }
}
