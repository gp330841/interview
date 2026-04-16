package javaBasics.multithreading;

/**
 * Print numbers from 1 to N using 3 threads 
 * (e.g., Thread1 -> 1,4,7... Thread2 -> 2,5,8... Thread3 -> 3,6,9...).
 * 
 * Explanation:
 * This problem generalizes alternating printing. We assign each thread a specific remainder
 * associated with `number % 3`.
 * - Thread 1 handles numbers where `counter % 3 == 1`
 * - Thread 2 handles numbers where `counter % 3 == 2`
 * - Thread 3 handles numbers where `counter % 3 == 0`
 */
public class Print1ToNUsing3Threads {

    private int max;
    private int counter = 1;

    public Print1ToNUsing3Threads(int max) {
        this.max = max;
    }

    public void print(int remainderAssignedToThread) {
        synchronized (this) {
            while (counter <= max) {
                // Determine the remainder required for this thread
                int remainder = counter % 3;
                
                // If it's not this thread's turn, wait
                if (remainder != remainderAssignedToThread) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    // It's this thread's turn
                    System.out.println(Thread.currentThread().getName() + " -> " + counter);
                    counter++;
                    notifyAll(); // Wake up all waiting threads
                }
            }
        }
    }

    public static void main(String[] args) {
        Print1ToNUsing3Threads printer = new Print1ToNUsing3Threads(15);

        // Thread 1 handles elements % 3 == 1
        Thread t1 = new Thread(() -> printer.print(1), "Thread-1");
        // Thread 2 handles elements % 3 == 2
        Thread t2 = new Thread(() -> printer.print(2), "Thread-2");
        // Thread 3 handles elements % 3 == 0
        Thread t3 = new Thread(() -> printer.print(0), "Thread-3");

        t1.start();
        t2.start();
        t3.start();
    }
}
