package javaBasics.multithreading;

/**
 * Print "ABCABC..." using 3 threads in order.
 * 
 * Explanation:
 * Similar to the logic of printing 1 to N using 3 threads.
 * We use an internal variable 'turn' to decide whose turn it is.
 * - Thread A executes when turn == 0
 * - Thread B executes when turn == 1
 * - Thread C executes when turn == 2
 */
public class PrintABCSequence {

    private int maxCount; // How many sequences we want
    private int turn = 0; // 0 for A, 1 for B, 2 for C
    private int printedCount = 0; // Keep track of the number of letters printed to terminate gracefully

    public PrintABCSequence(int maxCount) {
        this.maxCount = maxCount;
    }

    public void print(char toPrint, int myTurn, int nextTurn) {
        synchronized (this) {
            while (printedCount < maxCount * 3) {
                if (turn != myTurn) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                // Make sure we didn't exceed maximums while waiting
                if (printedCount < maxCount * 3) {
                    System.out.print(toPrint);
                    turn = nextTurn;
                    printedCount++;
                    notifyAll();
                }
            }
        }
    }

    public static void main(String[] args) {
        // Print the sequence 5 times
        PrintABCSequence printer = new PrintABCSequence(5);

        // A prints if turn == 0, next turn becomes 1
        Thread tA = new Thread(() -> printer.print('A', 0, 1), "Thread-A");
        // B prints if turn == 1, next turn becomes 2
        Thread tB = new Thread(() -> printer.print('B', 1, 2), "Thread-B");
        // C prints if turn == 2, next turn becomes 0
        Thread tC = new Thread(() -> printer.print('C', 2, 0), "Thread-C");

        tA.start();
        tB.start();
        tC.start();
        
        try {
            tA.join();
            tB.join();
            tC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\nDone.");
    }
}
