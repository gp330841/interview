package com.interview.javaconcepts.multithreading.concepts;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

/**
 * Core Concept: Deadlock & Prevention.
 * 
 * A Deadlock occurs when two or more threads are blocked forever, each waiting for a lock held by the other.
 * 
 * Coffman conditions required for Deadlock:
 * 1. Mutual Exclusion: At least one resource must be held in non-shareable mode.
 * 2. Hold and Wait: A thread must hold at least one resource and wait for another.
 * 3. No Preemption: Resources cannot be forcibly taken from a thread.
 * 4. Circular Wait: Thread 1 waits for Thread 2, which waits for Thread 1.
 * 
 * This class covers:
 * 1. Code causing a deliberate deadlock.
 * 2. Resolution 1: Lock Ordering (preventing Circular Wait).
 * 3. Resolution 2: Timed Locks using ReentrantLock.tryLock() (breaking Hold and Wait / allowing Preemption).
 */
public class DeadlockDemo {

    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    private final Lock reentrantLock1 = new ReentrantLock();
    private final Lock reentrantLock2 = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        DeadlockDemo demo = new DeadlockDemo();

        System.out.println("=== 1. Lock Ordering Prevention Demo ===");
        demo.runLockOrderingDemo();

        System.out.println("\n=== 2. Timed Locks Prevention Demo ===");
        demo.runTimedLocksDemo();

        System.out.println("\n=== 3. Deliberate Deadlock Demo ===");
        System.out.println("Starting threads that will deadlock. Note that the program would hang here if we didn't terminate...");
        demo.runDeliberateDeadlockDemo();
    }

    /**
     * Demonstrates a deliberate Deadlock.
     * Thread A locks 1, wants 2.
     * Thread B locks 2, wants 1.
     */
    private void runDeliberateDeadlockDemo() throws InterruptedException {
        Thread threadA = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("Thread A: Acquired lock1. Sleeping to let Thread B run...");
                try { Thread.sleep(50); } catch (InterruptedException e) {}
                System.out.println("Thread A: Attempting to acquire lock2...");
                synchronized (lock2) {
                    System.out.println("Thread A: Acquired lock2!");
                }
            }
        }, "Thread-A");

        Thread threadB = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("Thread B: Acquired lock2. Sleeping to let Thread A run...");
                try { Thread.sleep(50); } catch (InterruptedException e) {}
                System.out.println("Thread B: Attempting to acquire lock1...");
                synchronized (lock1) {
                    System.out.println("Thread B: Acquired lock1!");
                }
            }
        }, "Thread-B");

        threadA.start();
        threadB.start();

        // Wait a short time to let deadlock happen, then let main proceed.
        // In a real application, these threads would remain blocked forever.
        threadA.join(1000); 
        threadB.join(1000);
        if (threadA.isAlive() && threadB.isAlive()) {
            System.out.println("[DEADLOCK CONFIRMED] Thread A and Thread B are locked forever in a circular wait.");
            // We cannot easily terminate them as stop() is deprecated, but we proceed to show prevention next.
        }
    }

    /**
     * Prevention 1: Lock Ordering.
     * By enforcing that all threads acquire locks in the exact same global order (lock1 first, then lock2),
     * we break the Circular Wait condition.
     */
    private void runLockOrderingDemo() throws InterruptedException {
        Runnable safeRunnable = () -> {
            String name = Thread.currentThread().getName();
            // Both threads acquire lock1 FIRST, then lock2.
            synchronized (lock1) {
                System.out.println(name + ": Acquired lock1.");
                try { Thread.sleep(50); } catch (InterruptedException e) {}
                System.out.println(name + ": Attempting to acquire lock2...");
                synchronized (lock2) {
                    System.out.println(name + ": Acquired lock2. Successfully processed!");
                }
            }
        };

        Thread t1 = new Thread(safeRunnable, "SafeThread-1");
        Thread t2 = new Thread(safeRunnable, "SafeThread-2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("Lock ordering resolved deadlock successfully.");
    }

    /**
     * Prevention 2: Timed Lock Acquisition using ReentrantLock.tryLock().
     * If a thread cannot acquire all required locks within a timeout, it releases the locks it holds (backs off).
     * This breaks the Hold and Wait condition.
     */
    private void runTimedLocksDemo() throws InterruptedException {
        Thread t1 = new Thread(() -> acquireLocksTimed(reentrantLock1, reentrantLock2), "TimedThread-1");
        Thread t2 = new Thread(() -> acquireLocksTimed(reentrantLock2, reentrantLock1), "TimedThread-2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("Timed locks backed off and successfully avoided deadlock.");
    }

    private void acquireLocksTimed(Lock primary, Lock secondary) {
        String name = Thread.currentThread().getName();
        boolean primaryAcquired = false;
        boolean secondaryAcquired = false;

        while (true) {
            try {
                // Attempt to acquire primary lock with a 100ms timeout
                primaryAcquired = primary.tryLock(100, TimeUnit.MILLISECONDS);
                if (primaryAcquired) {
                    System.out.println(name + ": Acquired primary lock. Trying secondary lock...");
                    Thread.sleep(50); // Simulate work, creating contention

                    // Try to acquire secondary lock
                    secondaryAcquired = secondary.tryLock(100, TimeUnit.MILLISECONDS);
                    if (secondaryAcquired) {
                        System.out.println(name + ": Acquired both locks! Success.");
                        break; // Successfully acquired both locks, exit loop
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } finally {
                // If we failed to get both, release what we did manage to acquire so other threads can proceed (back off)
                if (primaryAcquired && !secondaryAcquired) {
                    System.out.println(name + ": Failed to acquire secondary. Releasing primary to back off...");
                    primary.unlock();
                    primaryAcquired = false;
                } else if (primaryAcquired && secondaryAcquired) {
                    primary.unlock();
                    secondary.unlock();
                    break;
                }
            }

            // Sleep briefly before retrying to allow other thread to finish
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }
    }
}
