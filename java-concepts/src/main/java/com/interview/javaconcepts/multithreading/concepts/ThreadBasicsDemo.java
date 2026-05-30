package com.interview.javaconcepts.multithreading.concepts;

import java.util.concurrent.TimeUnit;

/**
 * Core Concept: Thread Basics & Lifecycle.
 * 
 * This class covers:
 * 1. Thread States: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED.
 * 2. Key Thread Methods: sleep(), yield(), join().
 * 3. Thread Priorities: MIN_PRIORITY, NORM_PRIORITY, MAX_PRIORITY.
 * 4. Cooperative Interruption: interrupt(), isInterrupted(), Thread.interrupted().
 */
public class ThreadBasicsDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 1. Thread States & Transition Demo ===");
        demonstrateThreadStates();

        System.out.println("\n=== 2. Thread join() Demo ===");
        demonstrateJoin();

        System.out.println("\n=== 3. Thread yield() & Priority Demo ===");
        demonstratePriorityAndYield();

        System.out.println("\n=== 4. Cooperative Interruption Demo ===");
        demonstrateInterruption();
    }

    /**
     * Demonstrates the various thread states and how threads transition between them.
     */
    private static void demonstrateThreadStates() throws InterruptedException {
        Object lock = new Object();

        Thread worker = new Thread(() -> {
            synchronized (lock) {
                try {
                    // State: TIMED_WAITING (due to sleep)
                    Thread.sleep(200);

                    // State: WAITING (due to wait)
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // 1. NEW - Created but not started
        System.out.println("State after creation: " + worker.getState()); // Should be NEW

        synchronized (lock) {
            worker.start();
            // 2. RUNNABLE - Thread has been started (may be executing or waiting for CPU dispatch)
            System.out.println("State after start(): " + worker.getState()); // Should be RUNNABLE

            // Sleep main thread to let the worker acquire lock and enter sleep
            Thread.sleep(50);
            // 3. TIMED_WAITING - Inside Thread.sleep(200)
            System.out.println("State during sleep(): " + worker.getState()); // Should be TIMED_WAITING
        }

        // Now main thread relinquishes 'lock' but worker is waking up from sleep and immediately tries to lock.
        // But main thread re-acquires the lock to block it.
        synchronized (lock) {
            Thread.sleep(200); // Wait for worker to finish its sleep and block on the synchronized monitor
            // 4. BLOCKED - Worker is waiting to acquire the lock held by main thread
            System.out.println("State when blocked on lock: " + worker.getState()); // Should be BLOCKED
        }

        // Sleep main thread to let the worker acquire lock and call lock.wait()
        Thread.sleep(50);
        // 5. WAITING - Worker is inside lock.wait() waiting to be notified
        System.out.println("State during lock.wait(): " + worker.getState()); // Should be WAITING

        synchronized (lock) {
            lock.notify(); // Wake up worker
        }

        worker.join();
        // 6. TERMINATED - Finished executing its run method
        System.out.println("State after termination: " + worker.getState()); // Should be TERMINATED
    }

    /**
     * Demonstrates join(), which pauses the calling thread (main) until the target thread (worker) completes.
     */
    private static void demonstrateJoin() throws InterruptedException {
        Thread worker = new Thread(() -> {
            try {
                System.out.println("Worker: Working...");
                Thread.sleep(300);
                System.out.println("Worker: Finished work.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        worker.start();
        System.out.println("Main: Waiting for worker to complete...");
        worker.join(); // Blocks main thread until worker terminates
        System.out.println("Main: Worker is done. Main resumes.");
    }

    /**
     * Demonstrates priority and yield().
     * Note: Priorities are recommendations to the OS scheduler.
     * Yielding is a hint to the scheduler that the current thread is willing to yield its current processor use.
     */
    private static void demonstratePriorityAndYield() {
        Runnable runnable = () -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println(Thread.currentThread().getName() + " executing step " + i);
                Thread.yield(); // Hint to CPU scheduler to switch to other threads
            }
        };

        Thread t1 = new Thread(runnable, "Low-Priority-Thread");
        Thread t2 = new Thread(runnable, "High-Priority-Thread");

        t1.setPriority(Thread.MIN_PRIORITY); // 1
        t2.setPriority(Thread.MAX_PRIORITY); // 10

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Demonstrates safe, cooperative thread interruption.
     */
    private static void demonstrateInterruption() throws InterruptedException {
        Thread worker = new Thread(() -> {
            System.out.println("Interruption worker: Starting long-running work loop...");
            long iteration = 0;
            // Check cooperative flag: Thread.currentThread().isInterrupted()
            while (!Thread.currentThread().isInterrupted()) {
                iteration++;
                
                // Simulate periodic sleep, which throws InterruptedException and clears the interrupt status flag!
                if (iteration % 100000 == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        System.out.println("Interruption worker: Interrupted during sleep. Status is reset to false.");
                        // CRITICAL SENIOR PRACTICE: Re-assert the interrupt flag because InterruptedException clears it!
                        Thread.currentThread().interrupt();
                    }
                }
            }
            System.out.println("Interruption worker: Loop exited safely. isInterrupted() = " + Thread.currentThread().isInterrupted());
        });

        worker.start();
        Thread.sleep(200); // Let it spin for a bit
        System.out.println("Main: Signaling interruption to worker...");
        worker.interrupt(); // Sets the interrupt flag in worker thread
        worker.join();
        System.out.println("Main: Interruption worker joined.");
    }
}
