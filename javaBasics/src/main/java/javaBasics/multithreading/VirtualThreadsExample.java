package javaBasics.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Demonstrates Virtual Threads (Introduced as a preview in Java 19, stable in Java 21).
 * 
 * Traditional OS Threads (Platform Threads):
 * - 1:1 mapping with OS threads.
 * - Expensive to create and consume a lot of memory (usually ~1MB per thread).
 * - Context switching is costly.
 * - Blocking an OS thread (e.g., waiting for I/O) wastes resources.
 * 
 * Virtual Threads:
 * - M:N mapping. Millions of virtual threads run on a small pool of OS carrier threads.
 * - Extremely lightweight. You can create millions of them without OutOfMemoryError.
 * - Perfect for I/O bound tasks (web requests, database calls). When a virtual thread blocks on I/O,
 *   the JVM simply parks it and the underlying OS carrier thread executes another virtual thread.
 * - NOT meant for CPU-bound tasks (complex calculations). For those, still use parallel streams or traditional thread pools.
 */
public class VirtualThreadsExample {

    public static void main(String[] args) {
        
        System.out.println("Starting high throughput task...");
        long startTime = System.currentTimeMillis();

        // Let's launch 10,000 tasks that each sleep for 1 second simulating an I/O operation
        // If we used Executors.newFixedThreadPool(100), this would take 100 seconds!
        // But with Virtual Threads, it will take roughly 1 second!
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            IntStream.range(0, 10_000).forEach(i -> {
                executor.submit(() -> {
                    try {
                        Thread.sleep(1000); // Simulate network call or DB query
                        // The thread name will have "VirtualThread" in it
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
            
        } // The try-with-resources automatically shuts down the executor and waits for tasks to finish

        long endTime = System.currentTimeMillis();
        System.out.println("10,000 tasks completed in " + (endTime - startTime) + " ms");
        
        // Alternative way to create a single virtual thread
        Thread vt = Thread.ofVirtual().unstarted(() -> {
            System.out.println("Running in a standalone virtual thread: " + Thread.currentThread());
        });
        vt.start();
        
        try {
            vt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
