package lambdaAndStreams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static lambdaAndStreams.Util.println;

public class MethodReference {
    static void main() {
        List<String> words = Arrays.asList("hello", "world", "java");

        println.accept("conver to uppercase words");
        List<String> uppercasedWords = words.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        uppercasedWords.forEach(println);


        println.accept("print lengths of words");
        List<Integer> lengths = words.stream()
                .map(String::length) // Method reference
                .collect(Collectors.toList());
        lengths.stream().map(String::valueOf).forEach(println);

//        sorting
//        List<Integer> list2 = Arrays.asList(1, 3, 5, 2, 4);
//        list2.sort((a,b)-> a-b);
//        list2.sort((a,b)-> a.compareTo(b));
//        list2.sort(Integer::compareTo);
//        list2.forEach(System.out::print);




    }
}
