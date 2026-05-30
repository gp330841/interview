package com.interview.javaconcepts.multithreading.concepts;

import java.util.concurrent.*;

/**
 * Core Concept: Concurrent Synchronizers (CountDownLatch, CyclicBarrier, Semaphore).
 * 
 * 1. CountDownLatch:
 *    - Allows one or more threads to wait until a set of operations being performed in other threads completes.
 *    - Cannot be reset. One-time use.
 * 
 * 2. CyclicBarrier:
 *    - Allows a set of threads to all wait for each other to reach a common barrier point.
 *    - Reusable. It resets automatically after threads are released.
 *    - Can run an optional "barrier action" once all threads arrive.
 * 
 * 3. Semaphore:
 *    - Maintains a set of permits. Threads acquire permits before entering a critical section and release them after.
 *    - Used to throttle access to bounded resources (like database connection pools or API rate limiting).
 */
public class SynchronizersDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 1. CountDownLatch Demo ===");
        runCountDownLatchDemo();

        System.out.println("\n=== 2. CyclicBarrier Demo ===");
        runCyclicBarrierDemo();

        System.out.println("\n=== 3. Semaphore Demo ===");
        runSemaphoreDemo();
    }

    /**
     * CountDownLatch Demo: A server waiting for 3 core services to initialize before starting.
     */
    private static void runCountDownLatchDemo() throws InterruptedException {
        int serviceCount = 3;
        CountDownLatch latch = new CountDownLatch(serviceCount);
        ExecutorService executor = Executors.newFixedThreadPool(serviceCount);

        for (int i = 1; i <= serviceCount; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    System.out.println("Service " + id + " is initializing...");
                    Thread.sleep(100 * id); // Simulate initialization time
                    System.out.println("Service " + id + " is READY.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown(); // Decrement latch count
                }
            });
        }

        System.out.println("Main Thread: Waiting for " + serviceCount + " services to initialize...");
        latch.await(); // Block main thread until count reaches 0
        System.out.println("Main Thread: All services ready! Booting up application server.");
        executor.shutdown();
    }

    /**
     * CyclicBarrier Demo: 3 tourists traveling together. They must all reach checkpoint A,
     * checkpoint B, and check-in together.
     */
    private static void runCyclicBarrierDemo() throws InterruptedException {
        int parties = 3;
        // Barrier action runs when all 3 threads reach the barrier
        CyclicBarrier barrier = new CyclicBarrier(parties, () -> {
            System.out.println("\n[Barrier Action] All tourists arrived! Group is moving to the next location.\n");
        });

        ExecutorService executor = Executors.newFixedThreadPool(parties);

        for (int i = 1; i <= parties; i++) {
            final int touristId = i;
            executor.submit(() -> {
                try {
                    System.out.println("Tourist " + touristId + ": Traveling to Checkpoint A...");
                    Thread.sleep(50 * touristId);
                    System.out.println("Tourist " + touristId + ": Arrived at Checkpoint A. Waiting for others...");
                    barrier.await(); // Block until all 3 tourists call await()

                    System.out.println("Tourist " + touristId + ": Traveling to Checkpoint B...");
                    Thread.sleep(30 * touristId);
                    System.out.println("Tourist " + touristId + ": Arrived at Checkpoint B. Waiting for others...");
                    barrier.await(); // Reusable: block again for Checkpoint B

                } catch (InterruptedException | BrokenBarrierException e) {
                    System.out.println("Barrier broken!");
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);
    }

    /**
     * Semaphore Demo: Limit concurrent access to a database connection pool to max 2 threads.
     */
    private static void runSemaphoreDemo() throws InterruptedException {
        int maxPermits = 2;
        Semaphore semaphore = new Semaphore(maxPermits);
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 1; i <= 4; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    System.out.println("Thread " + threadId + ": Requesting database connection...");
                    semaphore.acquire(); // Blocks if no permits available
                    System.out.println("Thread " + threadId + ": [CONNECTION ACQUIRED] Performing DB query...");
                    Thread.sleep(200); // Simulate database operation
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    System.out.println("Thread " + threadId + ": [RELEASING CONNECTION] releasing permit.");
                    semaphore.release(); // Return permit
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);
    }
}
