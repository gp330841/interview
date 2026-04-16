package javaBasics.multithreading;

import java.util.concurrent.*;

/**
 * Demonstrates ExecutorService, Callable, and Future.
 * 
 * ExecutorService: A framework for executing, scheduling, managing, and controlling threads. 
 * Replaces the traditional "new Thread(Runnable).start()" approach with Thread Pools.
 * 
 * Callable: Unlike Runnable (which returns void), Callable can return a value and throw a checked Exception.
 * 
 * Future: Represents the result of an asynchronous computation. It provides methods to check if
 * the computation is complete, wait for its completion, and retrieve the result.
 */
public class ExecutorsAndFutures {

    public static void main(String[] args) {
        // Create an ExecutorService with a fixed thread pool of 2 threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Submitting a Runnable (Returns Future<?> which evaluates to null when done)
        Runnable runnable = () -> {
            System.out.println("Executing Runnable task inside thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Future<?> runnableFuture = executor.submit(runnable);

        // Submitting a Callable (Returns Future<T>)
        Callable<Integer> myCallable = () -> {
            System.out.println("Executing Callable task inside thread: " + Thread.currentThread().getName());
            Thread.sleep(1000);
            return 42; // Returns a value
        };
        
        Future<Integer> callableFuture = executor.submit(myCallable);

        System.out.println("Tasks submitted. Doing other work in main thread...");

        try {
            // Future.get() blocks the current thread until the result is available
            Integer result = callableFuture.get(); 
            System.out.println("Result from Callable: " + result);
            
            // Checking if Runnable is done
            if (runnableFuture.isDone()) {
                System.out.println("Runnable task completed successfully.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // Always shut down the ExecutorService to release resources and allow the JVM to exit
            executor.shutdown(); 
            try {
                // Wait a while for existing tasks to terminate
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    executor.shutdownNow(); // Force shutdown if tasks are taking too long
                }
            } catch (InterruptedException ie) {
                // Preserve interrupt status
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            System.out.println("Executor shut down.");
        }
    }
}
