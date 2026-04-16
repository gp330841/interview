package javaBasics.multithreading;

/**
 * Demonstrates the concept of 'volatile' keyword in Multithreading.
 * 
 * Volatile:
 * - Guarantees visibility of changes to variables across threads.
 * - Prevents thread caching of variables in registers/L1 cache.
 * - Does NOT guarantee atomicity (use Atomic variables like AtomicInteger or synchronized locks for atomicity).
 */
public class VolatileKeyword {

    // If we don't use 'volatile' here, the writer thread might update 'flag'
    // but the reader thread might never see the change because it caches 'flag'
    // locally in its CPU cache.
    private static  boolean read = true;

    public static void main(String[] args) {
        // Reader thread: keeps spinning until flag becomes true
        Thread reader = new Thread(() -> {
            System.out.println("Reader started. Waiting for flag to turn false...");
            long count = 0;
            while (read) {
                // Spinning...
                // Without volatile, this loop might run forever because
                // the updated value of flag from the Writer thread never reaches here.
                count++;
                if(!read) {
                    System.out.println("Reader stopped");
                }
            }
            System.out.println("Reader detected flag changed to true!");
            System.out.println("Read count is " + count);
        });

        // Writer thread: sets flag to true after some delay
        Thread writer = new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate some work
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Writer is changing flag to true.");
            read = false;
        });

        reader.start();
        writer.start();
    }
}
