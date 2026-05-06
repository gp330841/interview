package javaBasics;

/**
 * Question: Explain the new features introduced in Java 17.
 * 
 * Key Features:
 * 1. Records: Immutable data carrier classes which reduce boilerplate code for POJOs 
 *    (No need to write getters, equals, hashCode, or toString).
 * 2. Text Blocks: Multiline string literals avoiding escape sequences (preview in 13/14, standard in 15+).
 * 3. Pattern Matching for instanceof: Replaces the awkward check-and-cast idiom (standard in 16+).
 * 4. Sealed Classes: Restricts which other classes or interfaces may extend or implement them.
 */
public class Java17Features {

    // 1. Record Declaration
    public record User(String username, int age) {}

    // 4. Sealed Classes Declaration
    public sealed interface Shape permits Circle, Square {}
    public final class Circle implements Shape {}
    public final class Square implements Shape {}

    public static void main(String[] args) {
        System.out.println("--- 1. Records Demo ---");
        User user = new User("admin", 30);
        System.out.println("Record toString: " + user);
        System.out.println("Record accessor: " + user.username());

        System.out.println("\n--- 2. Text Blocks Demo ---");
        String json = """
                {
                    "name": "Alice",
                    "age": 25,
                    "city": "New York"
                }
                """;
        System.out.println("Text Block JSON:\n" + json);

        System.out.println("--- 3. Pattern Matching for instanceof Demo ---");
        Object obj = "Hello Pattern Matching";
        // Old way: if (obj instanceof String) { String s = (String) obj; ... }
        // New way:
        if (obj instanceof String s) {
            System.out.println("Length of string: " + s.length());
        }
    }
}
