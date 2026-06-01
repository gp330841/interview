package com.interview.javaconcepts.lambdas_streams.practice;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 🎓 Streams Practice Suite 2 (Advanced & Senior-Level Challenges)
 * 
 * This workbook contains Questions 16 to 30. Each question represents an advanced,
 * highly standard coding challenge targeting complex reductions, downstream collectors, 
 * infinite iteration, and robust edge-case handling.
 */
public class StreamsPracticeSuite2 {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("🔥 STREAMS PRACTICE SUITE 2 (Q16 - Q30)");
        System.out.println("==================================================");

        // Q16: Frequency of each character in a string
        System.out.println("Q16: " + q16CharFrequency("banana"));

        // Q17: Sum of squares of odd numbers
        System.out.println("Q17: " + q17SumSquaresOdds(Arrays.asList(1, 2, 3, 4, 5)));

        // Q18: Second highest in array (handling duplicates & nulls)
        System.out.println("Q18: " + q18SecondHighest(Arrays.asList(10, 8, 10, null, 7, 9, 9)));

        // Q19: Sort Custom Objects (Employees by Salary descending)
        List<Employee> emps = Arrays.asList(
            new Employee("Alice", "HR", 50000),
            new Employee("Bob", "IT", 80000),
            new Employee("Charlie", "IT", 95000),
            new Employee("David", "HR", 60000)
        );
        System.out.println("Q19: " + q19SortEmployees(emps));

        // Q20: FlatMap products from Orders
        List<Order> orders = Arrays.asList(
            new Order("O1", Arrays.asList("Laptop", "Mouse")),
            new Order("O2", Arrays.asList("Monitor", "Mouse", "Keyboard"))
        );
        System.out.println("Q20: " + q20FlatMapProducts(orders));

        // Q21: Top salaried employee in each department (groupingBy + maxBy)
        System.out.println("Q21: " + q21TopEmployeePerDept(emps));

        // Q22: Check if two strings are anagrams
        System.out.println("Q22 (Anagram): " + q22IsAnagram("listen", "silent"));
        System.out.println("Q22 (Not Anagram): " + q22IsAnagram("hello", "world"));

        // Q23: Merge maps with summed values
        Map<String, Integer> mapA = Map.of("X", 100, "Y", 200);
        Map<String, Integer> mapB = Map.of("Y", 50, "Z", 300);
        System.out.println("Q23: " + q23MergeMaps(mapA, mapB));

        // Q24: Convert Map to List of Custom KeyValue elements
        System.out.println("Q24: " + q24MapToList(Map.of("User1", 101, "User2", 102)));

        // Q25: Summary statistics on integers
        System.out.println("Q25: " + q25SummaryStatistics(Arrays.asList(10, 20, 30, 40, 50)));

        // Q26: First 10 Even Fibonacci numbers using Infinite Streams
        System.out.println("Q26: " + q26EvenFibonacci(10));

        // Q27: Group Products by Category and calculate average price
        List<Product> products = Arrays.asList(
            new Product("Bread", "Bakery", 2.5),
            new Product("Cake", "Bakery", 15.0),
            new Product("Milk", "Dairy", 3.0),
            new Product("Cheese", "Dairy", 7.5)
        );
        System.out.println("Q27: " + q27AveragePriceByCategory(products));

        // Q28: Comma-separated names filtered for null/empty
        System.out.println("Q28: " + q28FilterJoinNames(Arrays.asList("Alice", null, "", "  ", "Bob", "Charlie")));

        // Q29: First non-repeating character in a string
        System.out.println("Q29: " + q29FirstNonRepeatingChar("swiss"));

        // Q30: Partition integers into prime and composite (>1)
        System.out.println("Q30: " + q30PartitionPrimes(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
    }

    // ============================================================================
    // PRACTICE PROBLEMS IMPLEMENTATIONS
    // ============================================================================

    // Q16: Find the frequency of each character in a string.
    public static Map<Character, Long> q16CharFrequency(String str) {
        if (str == null) return Collections.emptyMap();
        return str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
    }

    // Q17: Calculate the sum of squares of all odd numbers in a list.
    public static int q17SumSquaresOdds(List<Integer> list) {
        return list.stream()
                .filter(n -> n % 2 != 0)
                .mapToInt(n -> n * n)
                .sum();
    }

    // Q18: Find the second highest number in an array (handling duplicates & nulls).
    public static int q18SecondHighest(List<Integer> list) {
        return list.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst()
                .orElse(-1);
    }

    // Q19: Sort a list of custom objects (Employees) by salary descending.
    public static List<String> q19SortEmployees(List<Employee> list) {
        return list.stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    // Q20: Extract distinct product categories/names from a list of orders (flatMapping collections).
    public static Set<String> q20FlatMapProducts(List<Order> list) {
        return list.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.toSet());
    }

    // Q21: Find the top-salaried employee in each department (nested collectors groupingBy + maxBy).
    public static Map<String, Optional<Employee>> q21TopEmployeePerDept(List<Employee> list) {
        return list.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))
                ));
    }

    // Q22: Check if two strings are anagrams using stream characters and grouping.
    public static boolean q22IsAnagram(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) return false;
        
        Map<Character, Long> f1 = s1.chars().mapToObj(c -> (char) c).collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        Map<Character, Long> f2 = s2.chars().mapToObj(c -> (char) c).collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        return f1.equals(f2);
    }

    // Q23: Merge two maps of string to integer, summing the values of duplicate keys.
    public static Map<String, Integer> q23MergeMaps(Map<String, Integer> m1, Map<String, Integer> m2) {
        return Stream.concat(m1.entrySet().stream(), m2.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::sum
                ));
    }

    // Q24: Convert Map to List of Custom KeyValue elements.
    public static List<KeyValue> q24MapToList(Map<String, Integer> map) {
        return map.entrySet().stream()
                .map(entry -> new KeyValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    // Q25: Calculate summary statistics (min, max, average, sum, count) for a list of integers.
    public static String q25SummaryStatistics(List<Integer> list) {
        IntSummaryStatistics stats = list.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();
        return String.format("Count: %d, Sum: %d, Min: %d, Max: %d, Avg: %.2f",
                stats.getCount(), stats.getSum(), stats.getMin(), stats.getMax(), stats.getAverage());
    }

    // Q26: Generate a list of the first N Even Fibonacci numbers using infinite streams (Stream.iterate).
    public static List<Integer> q26EvenFibonacci(int count) {
        return Stream.iterate(new int[]{0, 1}, f -> new int[]{f[1], f[0] + f[1]})
                .map(f -> f[0])
                .filter(n -> n % 2 == 0)
                .limit(count)
                .collect(Collectors.toList());
    }

    // Q27: Group products by category and calculate the average price of products in each category.
    public static Map<String, Double> q27AveragePriceByCategory(List<Product> list) {
        return list.stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.averagingDouble(Product::getPrice)
                ));
    }

    // Q28: Create a custom comma-separated list of strings but only for non-null, non-empty values.
    public static String q28FilterJoinNames(List<String> list) {
        return list.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "));
    }

    // Q29: Find the first non-repeating character in a string (LinkedHashMap collector).
    public static char q29FirstNonRepeatingChar(String str) {
        if (str == null || str.isEmpty()) return '\0';
        return str.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse('\0');
    }

    // Q30: Partition integers into prime and composite, filtering out everything below 2.
    public static Map<Boolean, List<Integer>> q30PartitionPrimes(List<Integer> list) {
        return list.stream()
                .filter(Objects::nonNull)
                .filter(n -> n >= 2)
                .collect(Collectors.partitioningBy(StreamsPracticeSuite2::isPrime));
    }

    private static boolean isPrime(int n) {
        return n > 1 && IntStream.rangeClosed(2, (int) Math.sqrt(n)).noneMatch(i -> n % i == 0);
    }

    // ============================================================================
    // DOMAIN MODELS USED BY THE QUESTIONS
    // ============================================================================

    public static class Employee {
        private final String name;
        private final String department;
        private final double salary;

        public Employee(String name, String department, double salary) {
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        public String getName() { return name; }
        public String getDepartment() { return department; }
        public double getSalary() { return salary; }

        @Override
        public String toString() {
            return name + " (" + department + ": $" + salary + ")";
        }
    }

    public static class Order {
        private final String id;
        private final List<String> items;

        public Order(String id, List<String> items) {
            this.id = id;
            this.items = items;
        }

        public String getId() { return id; }
        public List<String> getItems() { return items; }
    }

    public static class Product {
        private final String name;
        private final String category;
        private final double price;

        public Product(String name, String category, double price) {
            this.name = name;
            this.category = category;
            this.price = price;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
    }

    public static class KeyValue {
        private final String key;
        private final int value;

        public KeyValue(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "{" + key + " = " + value + "}";
        }
    }
}
