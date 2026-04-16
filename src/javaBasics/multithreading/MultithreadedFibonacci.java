package javaBasics.multithreading;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Print Fibonacci series using multiple threads.
 * 
 * Explanation:
 * The most standard way to perform divide-and-conquer multithreading in Java
 * is using the ForkJoinPool (Introduced in Java 7).
 * For computing fibonacci(N), a thread spawns two child tasks (fib(n-1) and fib(n-2)),
 * forks them (adds to pool), and joins (waits for results).
 * 
 * NOTE: For large N, it's highly inefficient without memoization, but perfectly demonstrates
 * how Fork-Join frameworks handle massive recursive sub-tasking over available CPU cores.
 */
public class MultithreadedFibonacci {

    // Inherits RecursiveTask because returning a result. (Use RecursiveAction for no return).
    static class FibonacciTask extends RecursiveTask<Long> {
        private final long n;

        FibonacciTask(long n) {
            this.n = n;
        }

        @Override
        protected Long compute() {
            if (n <= 1) {
                return n;
            }
            
            // Forking the tasks pushes them into the ForkJoin threadpool queue
            FibonacciTask f1 = new FibonacciTask(n - 1);
            f1.fork(); // async execution

            FibonacciTask f2 = new FibonacciTask(n - 2);
            // compute f2 synchronously in current thread while f1 runs in parallel
            long result2 = f2.compute(); 
            
            // wait for f1 result
            long result1 = f1.join(); 

            return result1 + result2;
        }
    }

    public static void main(String[] args) {
        int n = 15; // To get the 15th fibonacci number
        
        System.out.println("Calculating Fibonacci of " + n + " using ForkJoinPool...");

        // Uses a pool with the number of available CPU cores by default
        ForkJoinPool pool = new ForkJoinPool();

        FibonacciTask rootTask = new FibonacciTask(n);
        long result = pool.invoke(rootTask); // Blocks until the entire tree of tasks is done

        System.out.println("Fibonacci(" + n + ") = " + result);
    }
}
