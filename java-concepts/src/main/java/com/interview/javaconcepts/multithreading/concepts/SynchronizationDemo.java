package com.interview.javaconcepts.multithreading.concepts;

/**
 * Core Concept: Lock Synchronization in Java.
 * 
 * Synchronizing threads prevents race conditions on shared state.
 * Java locks are reentrant: if a thread holds a lock, it can re-acquire the same lock without deadlocking itself.
 * 
 * This class covers:
 * 1. Instance-level synchronized method (locks on 'this' instance).
 * 2. Instance-level synchronized block (locks on 'this' or a specific private final lock object).
 * 3. Class-level synchronized method/block (locks on the class's Class instance, e.g. SynchronizationDemo.class).
 * 4. Lock Reentrancy.
 */
public class SynchronizationDemo {

    private int counter = 0;
    private final Object customLock = new Object(); // Recommended practice: lock on private final dedicated monitor object

    public static void main(String[] args) throws InterruptedException {
        SynchronizationDemo demo = new SynchronizationDemo();

        System.out.println("=== 1. Instance-Level & Fine-Grained Blocks ===");
        demo.demonstrateInstanceAndBlockLocks();

        System.out.println("\n=== 2. Lock Reentrancy Demo ===");
        demo.demonstrateReentrancy();
    }

    /**
     * 1. Instance-level synchronized method.
     * Locks the entire method on the 'this' instance. Only one thread can execute this method on the same instance.
     */
    public synchronized void incrementSynchronizedMethod() {
        counter++;
    }

    /**
     * 2. Fine-grained synchronized block.
     * Locks only the critical section on a dedicated private monitor object.
     * Highly recommended over synchronized methods because it restricts the critical section size.
     */
    public void incrementSynchronizedBlock() {
        // Doing some non-critical work here...
        String threadName = Thread.currentThread().getName();
        
        synchronized (customLock) {
            // Critical section: modifying shared state
            counter++;
        }
    }

    /**
     * 3. Class-level synchronized method (static).
     * Locks on the class object (SynchronizationDemo.class). Protects static class-level variables.
     */
    public static synchronized void staticSynchronizedMethod() {
        // Locks on SynchronizationDemo.class
        System.out.println(Thread.currentThread().getName() + " inside static synchronized method.");
    }

    /**
     * Demonstrates instance and block synchronization using multiple threads.
     */
    private void demonstrateInstanceAndBlockLocks() throws InterruptedException {
        Runnable r1 = () -> {
            for (int i = 0; i < 1000; i++) {
                incrementSynchronizedMethod();
            }
        };

        Runnable r2 = () -> {
            for (int i = 0; i < 1000; i++) {
                incrementSynchronizedBlock();
            }
        };

        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r1);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("Counter value after synchronized methods (expected 2000): " + counter);

        counter = 0; // reset
        Thread t3 = new Thread(r2);
        Thread t4 = new Thread(r2);
        t3.start();
        t4.start();
        t3.join();
        t4.join();
        System.out.println("Counter value after synchronized blocks (expected 2000): " + counter);
    }

    /**
     * 4. Lock Reentrancy.
     * Demonstrates that when a thread acquires a lock, it can execute other methods synchronized
     * on that exact same lock object without blocking itself.
     */
    private void demonstrateReentrancy() {
        Thread reentrantThread = new Thread(this::outerSynchronizedMethod, "ReentrantThread");
        reentrantThread.start();
        try {
            reentrantThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private synchronized void outerSynchronizedMethod() {
        System.out.println("Inside outerSynchronizedMethod() - acquired 'this' monitor.");
        
        // Call another synchronized method on the same instance
        innerSynchronizedMethod();
        
        System.out.println("Exiting outerSynchronizedMethod() - releasing 'this' monitor.");
    }

    private synchronized void innerSynchronizedMethod() {
        System.out.println("Inside innerSynchronizedMethod() - successfully re-entered 'this' monitor without blocking!");
    }
}
