package com.interview.javaconcepts.multithreading.concepts;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Senior Concept: Project Loom Virtual Threads & Carrier Thread Pinning.
 * 
 * Virtual threads (M:N scheduling) run on a pool of underlying OS "Carrier Threads" (usually ForkJoinPool).
 * When a virtual thread makes a blocking call (like socket reading, sleep, JDBC query):
 * - The JVM intercepts this call, suspends the virtual thread, UNMOUNTS it from the carrier thread,
 *   and registers a callback. The carrier thread is free to execute other virtual threads.
 * 
 * THE PINNING PROBLEM:
 * A virtual thread gets "pinned" to its carrier thread and CANNOT be unmounted if:
 * 1. It executes a blocking operation inside a 'synchronized' block or method.
 * 2. It executes inside a native method or foreign function.
 * 
 * If a thread is pinned, its carrier thread is blocked. Under high volume, this can lead to severe
 * exhaustion of the carrier pool and system stalls (reducing Loom's massive scalability back to platform levels!).
 * 
 * THE SOLUTION:
 * Replace 'synchronized' blocks/methods with explicit 'ReentrantLock' instances for sections performing blocking IO.
 */
public class VirtualThreadPinningDemo {

    private final Object syncLock = new Object();
    private final Lock explicitLock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        VirtualThreadPinningDemo demo = new VirtualThreadPinningDemo();

        System.out.println("=== 1. Demonstrating Virtual Thread Behavior ===");
        
        // We will run virtual threads to execute blocking actions.
        // We use a custom Virtual Thread executor to see Loom in action.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            // Task 1: Runs using ReentrantLock (Clean unmounting!)
            executor.submit(() -> demo.processWithReentrantLock("VT-Task-Lock-1"));
            executor.submit(() -> demo.processWithReentrantLock("VT-Task-Lock-2"));

            Thread.sleep(500); // Allow lock tasks to finish

            System.out.println("\n=== 2. Explaining Carrier Thread Pinning ===");
            System.out.println("When executing a task inside 'synchronized' blocks, the virtual thread pins the carrier thread.");
            System.out.println("The JVM cannot unmount it during Thread.sleep or Socket blocking.");
            
            executor.submit(() -> demo.processWithSynchronized("VT-Task-Sync-1"));
            executor.submit(() -> demo.processWithSynchronized("VT-Task-Sync-2"));
        }
        
        System.out.println("\nDemo completed. Always refactor critical blocking sync blocks to ReentrantLock for Loom scalability!");
    }

    /**
     * Bad Practice in Loom: Synchronization block with blocking IO.
     * Pins the virtual thread's carrier OS thread!
     */
    public void processWithSynchronized(String taskName) {
        synchronized (syncLock) {
            System.out.println(taskName + ": Entered synchronized block. [PINNED to carrier thread]");
            try {
                // Blocking operation inside synchronized section
                Thread.sleep(200); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println(taskName + ": Exiting synchronized block.");
        }
    }

    /**
     * Good Practice in Loom: ReentrantLock with blocking IO.
     * Loom can cleanly unmount the virtual thread from the carrier thread!
     */
    public void processWithReentrantLock(String taskName) {
        explicitLock.lock();
        try {
            System.out.println(taskName + ": Acquired ReentrantLock. [UNMOUNTS cleanly on block]");
            try {
                // Blocking operation: Loom intercepts this, unmounts the virtual thread, 
                // and lets the carrier thread execute other tasks!
                Thread.sleep(200); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println(taskName + ": Exiting ReentrantLock.");
        } finally {
            explicitLock.unlock();
        }
    }
}
