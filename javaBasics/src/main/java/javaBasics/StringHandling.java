package javaBasics;

/**
 * Question: Why is String immutable? What is the difference between String, StringBuilder, and StringBuffer?
 * 
 * Critical Points:
 * - String is immutable for security, thread-safety, and memory efficiency (String pool).
 * - StringBuilder is mutable and not thread-safe (fast).
 * - StringBuffer is mutable and thread-safe (slower).
 */
public class StringHandling {

    public static void main(String[] args) {
        System.out.println("--- String Immutability Demo ---");
        String s1 = "Hello";
        String s2 = s1;
        s1 = s1.concat(" World"); // Creates a new string, s2 still points to "Hello"
        
        System.out.println("s1: " + s1);
        System.out.println("s2: " + s2);
        
        System.out.println("\n--- StringBuilder vs StringBuffer Demo ---");
        StringBuilder sb = new StringBuilder("Java");
        sb.append(" Basics");
        System.out.println("StringBuilder: " + sb.toString());
        
        StringBuffer sbuf = new StringBuffer("Thread");
        sbuf.append("-Safe");
        System.out.println("StringBuffer: " + sbuf.toString());
    }
}
