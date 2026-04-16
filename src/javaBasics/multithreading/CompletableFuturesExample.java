package javaBasics.multithreading;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates CompletableFuture (introduced in Java 8).
 * 
 * CompletableFuture is an extension to Future that allows you to build asynchronous,
 * non-blocking, and reactive pipelines.
 * 
 * Unlike standard Future where you have to call get() and block the thread,
 * CompletableFuture lets you attach callbacks (like thenApply, thenAccept) that
 * get executed automatically when the result is ready.
 */
public class CompletableFuturesExample {

    public static void main(String[] args) {
        
        System.out.println("Main thread: " + Thread.currentThread().getName());

        // 1. Run a simple async task that returns no result
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            sleep(500);
            System.out.println("Running async task in: " + Thread.currentThread().getName());
        });

        // 2. Run an async task that returns a result (supplyAsync)
        // and chain a callback (thenApply) to process the result
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            System.out.println("Fetching user data asynchronously...");
            return "User: John Doe";
        }).thenApply(user -> {
            // thenApply receives the result from the previous step, modifies it, and returns a new result
            System.out.println("Processing user data...");
            return user.toUpperCase();
        }).thenApply(userUpper -> {
            return userUpper + " - ACTIVE";
        });

        // 3. Consuming the result without returning anything using thenAccept
        future2.thenAccept(finalResult -> {
            System.out.println("Final Output: " + finalResult);
        });

        // 4. Exception Handling in CompletableFuture
        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
            int x = 10 / 0; // This will throw ArithmeticException
            return x;
        }).exceptionally(ex -> {
            System.err.println("Exception occurred: " + ex.getMessage());
            return 0; // Provide a fallback value
        });

        try {
            // Wait for all to finish so we can see the output before main thread exits
            // In a real application, you might not block, relying on callbacks instead.
            CompletableFuture.allOf(future1, future2, future3).join();
            
            System.out.println("Fallback result from future3: " + future3.get());
            
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        
        System.out.println("Main thread done.");
    }

    private static void sleep(int millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
