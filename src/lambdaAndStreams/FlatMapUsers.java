package lambdaAndStreams;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

class User {
    private String name;
    private List<String> favoriteBooks;

    public User(String name, List<String> favoriteBooks) {
        this.name = name;
        this.favoriteBooks = favoriteBooks;
    }

    public List<String> getFavoriteBooks() {
        return favoriteBooks;
    }

    public String getName() {
        return name;
    }
}

public class FlatMapUsers {
    static void main() {
        List<User> users = Arrays.asList(
            new User("Alice", Arrays.asList("Book1", "Book2")),
            new User("Bob", Arrays.asList("Book3", "Book4"))
        );

//        users.stream().flatMap(user-> user.getFavoriteBooks().stream()).sorted(Comparator.reverseOrder()).forEach(System.out::println);

        // Flatten all favorite books into a single list
        List<String> allBooks = users.stream().peek(user-> System.out.println(user.getName()))
                                      .flatMap(user -> user.getFavoriteBooks().stream())
                                      .toList();

        System.out.println("All Favorite Books: " + allBooks); // Output: [Book1, Book2, Book3, Book4]
    }
}