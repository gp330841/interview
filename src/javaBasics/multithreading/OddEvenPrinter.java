package javaBasics.multithreading;

/**
 * Print odd and even numbers using two threads (alternating sequence).
 * 
 * Explanation:
 * We use a shared lock object and a shared counter.
 * - Thread 1 (Odd): Prints numbers when the counter is odd. Otherwise, it waits.
 * - Thread 2 (Even): Prints numbers when the counter is even. Otherwise, it waits.
 * We use wait() to put a thread to sleep and notify() to wake up the other thread
 * after updating the counter.
 */
public class OddEvenPrinter {

    private int max;
    private int counter = 1;

    public OddEvenPrinter(int max) {
        this.max = max;
    }

    public void printOdd() {
        synchronized (this) {
            while (counter <= max) {
                // If the counter is even, wait for the odd thread's turn
                if (counter % 2 == 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                
                if (counter <= max) {
                    System.out.println(Thread.currentThread().getName() + " : " + counter);
                    counter++;
                    notify(); // Wake up the even thread
                }
            }
        }
    }

    public void printEven() {
        synchronized (this) {
            while (counter <= max) {
                // If the counter is odd, wait for the even thread's turn
                if (counter % 2 != 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                
                if (counter <= max) {
                    System.out.println(Thread.currentThread().getName() + " : " + counter);
                    counter++;
                    notify(); // Wake up the odd thread
                }
            }
        }
    }

    public static void main(String[] args) {
        OddEvenPrinter printer = new OddEvenPrinter(10); // Print up to 10

        Thread oddThread = new Thread(printer::printOdd, "OddThread");
        Thread evenThread = new Thread(printer::printEven, "EvenThread");

        oddThread.start();
        evenThread.start();
    }
}
