package com.interview.javaconcepts.topic09_concurrency_advanced;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Advanced Concurrency: CountDownLatch
 * 
 * A CountDownLatch allows one or more threads to wait until a set of operations 
 * being performed in other threads completes.
 * 
 * Key Methods:
 * - await(): Causes the current thread to wait until the latch has counted down to zero.
 * - countDown(): Decrements the count of the latch, releasing all waiting threads if the count reaches zero.
 */
public class CountDownLatchExample {

    public static void main(String[] args) throws InterruptedException {
        int numberOfServices = 3;
        CountDownLatch latch = new CountDownLatch(numberOfServices);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfServices);

        for (int i = 1; i <= numberOfServices; i++) {
            final int serviceId = i;
            executor.submit(() -> {
                try {
                    System.out.println("Service " + serviceId + " is starting...");
                    Thread.sleep((long) (Math.random() * 2000)); // Simulate startup time
                    System.out.println("Service " + serviceId + " is up.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown(); // Decrement the latch count
                }
            });
        }

        System.out.println("Main thread waiting for services to start...");
        latch.await(); // Main thread blocks until count reaches 0
        System.out.println("All services started. Main thread proceeding.");
        
        executor.shutdown();
    }
}
