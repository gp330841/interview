package lambdaAndStreams;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaExpression {
    static void main() {
        Predicate<Integer> isEven = x -> x % 2 == 0;
        Consumer<Integer> print = System.out::println;
        Supplier<Integer> supplier = () -> 1;
        Function<Integer, Integer> function = x -> x * 2;

        print.accept(isEven.test(2) ?1:0);
        print.accept(supplier.get());
        print.accept(function.apply(2));

    }
}
