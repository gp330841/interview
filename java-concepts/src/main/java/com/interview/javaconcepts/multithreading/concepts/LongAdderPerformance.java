package com.interview.javaconcepts.multithreading.concepts;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Senior Concept: LongAdder vs AtomicLong under High Contention.
 * 
 * Under low contention, AtomicLong and LongAdder perform similarly.
 * However, under high contention (many threads writing to a single counter):
 * 
 * 1. AtomicLong (Global CAS Contention):
 *    - All threads execute CAS (`compareAndSet`) loops on a SINGLE shared memory address.
 *    - Only one thread succeeds in each iteration. The rest must spin-loop, consuming CPU cycles and
 *      causing massive L1/L2 cache-invalidation traffic across cores (cache coherence overhead).
 * 
 * 2. LongAdder (Striped Memory Cells):
 *    - Instead of a single address, LongAdder maintains a base value and a dynamic table of "Cell" objects.
 *    - Each thread is hashed to a specific cell in the table and updates that cell independently using CAS.
 *    - Since different threads write to different cache lines, contention is drastically reduced.
 *    - The total sum is computed only when `sum()` or `longValue()` is called, by adding the base to all cells.
 *    - Recommendation: Use LongAdder for high-frequency write counters (like metric trackers, telemetry, or request counters).
 *      Use AtomicLong only when you require absolute, atomic read-and-write consistency (like generating sequence IDs).
 */
public class LongAdderPerformance {

    private static final int NUM_THREADS = 8;
    private static final int INCREMENTS_PER_THREAD = 10_000_000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Starting Contention Benchmark (Total increments: " + (NUM_THREADS * INCREMENTS_PER_THREAD) + ") ===");

        // Run AtomicLong benchmark
        long atomicTime = runAtomicLongBenchmark();

        // Run LongAdder benchmark
        long adderTime = runLongAdderBenchmark();

        System.out.println("\n=== Performance Results ===");
        System.out.println("AtomicLong Total Time: " + atomicTime + " ms");
        System.out.println("LongAdder Total Time:  " + adderTime + " ms");
        
        double speedup = (double) atomicTime / adderTime;
        System.out.printf("LongAdder was %.2fx faster under high thread contention!\n", speedup);
    }

    private static long runAtomicLongBenchmark() throws InterruptedException {
        AtomicLong counter = new AtomicLong(0);
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    counter.incrementAndGet(); // Hotspot CAS contention
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        
        long endTime = System.currentTimeMillis();
        System.out.println("AtomicLong completed: counter = " + counter.get());
        return (endTime - startTime);
    }

    private static long runLongAdderBenchmark() throws InterruptedException {
        LongAdder counter = new LongAdder();
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    counter.increment(); // Low-contention striped write
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        
        long endTime = System.currentTimeMillis();
        System.out.println("LongAdder completed: counter = " + counter.sum());
        return (endTime - startTime);
    }
}
