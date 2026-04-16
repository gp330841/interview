package javaBasics;

/**
 * Question: Explain the new features introduced in Java 14.
 * 
 * Key Features:
 * 1. Switch Expressions: Standardized in Java 14. Allows switch to return a value, uses '->' syntax, 
 *    and removes the need for 'break' statements.
 * 2. Helpful NullPointerExceptions: Describes precisely which variable was null.
 */
public class Java14Features {

    public static void main(String[] args) {
        System.out.println("--- 1. Switch Expression Demo ---");
        String day = "MONDAY";
        
        // New Switch Expression returning a value
        String typeOfDay = switch (day) {
            case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> "Weekday";
            case "SATURDAY", "SUNDAY" -> "Weekend";
            default -> "Unknown";
        };
        
        System.out.println(day + " is a " + typeOfDay);

        System.out.println("\n--- 2. Helpful NullPointerException Demo ---");
        System.out.println("If you try `a.b.c.getName()` and `c` is null, Java 14+ explicitly tells you that `c` was null!");
    }
}
