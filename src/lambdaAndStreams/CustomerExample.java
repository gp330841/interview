package lambdaAndStreams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomerExample {
    public static void main(String[] args) {
        List<Customer> customers = List.of(
            new Customer("Alice", 25, "USA"),
            new Customer("Bob", 30, "UK"),
            new Customer("Charlie", 35, "USA"),
            new Customer("David", 40, "Canada")
        );

        Map<String, Long> customerCountByCountry = customers.stream()
            .collect(Collectors.groupingBy(Customer::getCountry, Collectors.counting()));

        System.out.println(customerCountByCountry); // Output: {USA=2, UK=1, Canada=1}

        Map<String, Double> customerRevenueByCountry = customers.stream()
                .collect(Collectors.groupingBy(
                        Customer::getCountry,
                        Collectors.averagingInt(Customer::getAge)
                        ));
        System.out.println(customerRevenueByCountry);

        Map<String, List<String>> countryWiseNames =
                customers.stream()
                        .collect(Collectors.groupingBy(
                                Customer::getCountry,
                                Collectors.mapping(
                                        Customer::getName,
                                        Collectors.toList()
                                )
                        ));

        System.out.println(countryWiseNames);

        Double averageAge = customers.stream()
                .map(Customer::getAge)
                .collect(Collectors.averagingDouble(Double::valueOf));
        System.out.println(averageAge);
    }
}

class Customer {
    private String name;
    private int age;
    private String country;

    public Customer(String name, int age, String country) {
        this.name = name;
        this.age = age;
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}