// SampleProblem.java
// Example DSA problem solution in Java

public class SampleProblem {
    /**
     * Example: Find the sum of an integer array
     */
    public static int sum(int[] arr) {
        int total = 0;
        for(int n : arr) {
            total += n;
        }
        return total;
    }
    
    // Example main method
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5};
        System.out.println("Sum is: " + sum(arr)); // Output: Sum is: 15
    }
}
