package com.interview.javaconcepts.multithreading.concepts;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Core Concept: Fork/Join Framework & Work-Stealing.
 * 
 * The ForkJoinPool is designed to speed up parallelizable, divide-and-conquer tasks.
 * 
 * Work-Stealing Algorithm Internals:
 * 1. Each worker thread in the ForkJoinPool maintains its own double-ended queue (Deque) of tasks.
 * 2. Local Task Submission: When a thread forks a subtask, it pushes it onto the HEAD of its own Deque (LIFO).
 * 3. Local Execution: The thread pops tasks off the HEAD of its own Deque to execute them (LIFO - high cache locality).
 * 4. Stealing: When a thread is out of work, it acts as a "thief". It looks at another thread's Deque
 *    and steals a task from the TAIL (FIFO - reduces contention and steals large un-split chunks).
 * 
 * Class Hierarchy:
 * - ForkJoinTask<V>: Base class.
 *   - RecursiveAction: For tasks that return void.
 *   - RecursiveTask<V>: For tasks that return a value.
 */
public class ForkJoinPoolDemo {

    public static void main(String[] args) {
        int arraySize = 10_000_000;
        int[] array = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            array[i] = 1; // Sum should equal arraySize
        }

        // Use standard ForkJoinPool or commonPool()
        ForkJoinPool pool = ForkJoinPool.commonPool();

        System.out.println("Common Pool Parallelism Level: " + pool.getParallelism());

        long startTime = System.currentTimeMillis();
        
        // Submit root task
        ParallelSumTask rootTask = new ParallelSumTask(array, 0, array.length);
        long sum = pool.invoke(rootTask);

        long endTime = System.currentTimeMillis();

        System.out.println("Computed Sum: " + sum + " (Expected: " + arraySize + ")");
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }

    /**
     * Parallel task to compute the sum of an array slice using divide-and-conquer.
     */
    static class ParallelSumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 100_000; // Slice limit below which we calculate sequentially
        private final int[] array;
        private final int start;
        private final int end;

        public ParallelSumTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;

            // Sequential fallback threshold
            if (length <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            }

            // Divide-and-Conquer
            int mid = start + (length / 2);
            ParallelSumTask leftSubtask = new ParallelSumTask(array, start, mid);
            ParallelSumTask rightSubtask = new ParallelSumTask(array, mid, end);

            // Push left task onto local thread's Deque for parallel execution
            leftSubtask.fork(); 

            // Compute right task inline in the current thread (saves a thread dispatch)
            long rightResult = rightSubtask.compute();

            // Wait for left task to finish and join its results
            long leftResult = leftSubtask.join(); 

            return leftResult + rightResult;
        }
    }
}
