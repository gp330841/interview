package com.interview.javaconcepts.multithreading.concepts;

import java.util.concurrent.locks.StampedLock;

/**
 * Senior Concept: StampedLock & Optimistic Locking.
 * 
 * StampedLock was introduced in Java 8 as a highly performant alternative to ReentrantReadWriteLock.
 * 
 * Advantages over ReentrantReadWriteLock:
 * 1. Optimistic Reading: Allows reading without blocking writers. You acquire an optimistic "stamp",
 *    read variables locally, and then validate if a write occurred during your read.
 *    If no write occurred, you got a zero-cost lock-free concurrent read! If a write occurred, you fallback to a pessimistic read lock.
 * 2. Avoids Write-Starvation: Readers do not block writers. Writers can always break in.
 * 
 * CRITICAL CAVEATS:
 * 1. Non-Reentrant: StampedLock is NOT reentrant. A thread holding a lock will deadlock if it attempts to lock it again!
 * 2. Does not support Conditions: Use ReentrantLock if you need Condition synchronization.
 */
public class StampedLockExample {

    private final StampedLock lock = new StampedLock();
    private double x;
    private double y;

    public static void main(String[] args) throws InterruptedException {
        StampedLockExample example = new StampedLockExample();

        System.out.println("=== 1. Starting StampedLock Demo ===");
        
        Thread reader = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                double distance = example.distanceFromOrigin();
                System.out.println("Reader: Computed distance -> " + distance);
                try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }, "OptimisticReader");

        Thread writer = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("Writer: Updating coordinates to (" + i + ", " + i + ")");
                example.move(i, i);
                try { Thread.sleep(80); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }, "PessimisticWriter");

        reader.start();
        writer.start();

        reader.join();
        writer.join();
    }

    /**
     * Exclusive Write Lock (Pessimistic).
     * Works similarly to a ReentrantLock write lock.
     */
    public void move(double deltaX, double deltaY) {
        long stamp = lock.writeLock(); // Exclusive write lock
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            lock.unlockWrite(stamp); // Release using matching stamp
        }
    }

    /**
     * Optimistic Read Lock.
     * Extremely efficient under high contention.
     */
    public double distanceFromOrigin() {
        // Step 1: Try to acquire an optimistic read stamp (this is lock-free and does not block writers!)
        long stamp = lock.tryOptimisticRead();
        
        // Step 2: Read fields into local variables
        double currentX = x;
        double currentY = y;

        // Step 3: Validate stamp to ensure no write lock was acquired since we obtained the stamp
        if (!lock.validate(stamp)) {
            System.out.println("   [Optimistic Read FAILED] Stamp invalidated by concurrent write! Falling back to Pessimistic Read...");
            
            // Step 4: Fallback to standard pessimistic shared read lock (blocks until current write completes)
            stamp = lock.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                lock.unlockRead(stamp); // Release pessimistic read lock
            }
        } else {
            System.out.println("   [Optimistic Read SUCCESS] Read values successfully without blocking or lock contention!");
        }

        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}
