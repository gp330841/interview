package javaBasics.multithreading;

/**
 * Implement read-write lock (multiple readers, single writer).
 * 
 * Explanation:
 * A ReadWriteLock allows multiple threads to read data at the same time,
 * but only one thread can write data, and no threads can read while writing is happening.
 * 
 * Core Logic:
 * - Read Lock request: Allowed if `writersCount == 0` and `writeRequests == 0`
 *   (writeRequests prevents writer starvation where infinite readers block a writer forever).
 * - Read Unlock: Decrement `readersCount` and notifyAll().
 * - Write Lock request: Allowed if `readersCount == 0` and `writersCount == 0`.
 * - Write Unlock: Decrement `writersCount` and notifyAll().
 */
public class CustomReadWriteLock {

    private int readersCount = 0;
    private int writersCount = 0;
    private int writeRequests = 0; // Prevent writer starvation

    public synchronized void lockRead() throws InterruptedException {
        // While there is an active writer or an awaiting writer, block new readers
        while (writersCount > 0 || writeRequests > 0) {
            wait();
        }
        readersCount++;
    }

    public synchronized void unlockRead() {
        readersCount--;
        notifyAll(); // Wake up any waiting writers
    }

    public synchronized void lockWrite() throws InterruptedException {
        writeRequests++;
        
        // Wait until all readers and writers are finished
        while (readersCount > 0 || writersCount > 0) {
            wait();
        }
        
        writeRequests--;
        writersCount++;
    }

    public synchronized void unlockWrite() {
        writersCount--;
        notifyAll(); // Wake up readers and other writers
    }

    public static void main(String[] args) {
        CustomReadWriteLock rwLock = new CustomReadWriteLock();

        // Runnable simulating a Read operation
        Runnable readTask = () -> {
            try {
                rwLock.lockRead();
                System.out.println(Thread.currentThread().getName() + " is READING.");
                Thread.sleep(1000); // Simulate reading time
                System.out.println(Thread.currentThread().getName() + " finished READING.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                rwLock.unlockRead();
            }
        };

        // Runnable simulating a Write operation
        Runnable writeTask = () -> {
            try {
                rwLock.lockWrite();
                System.out.println(Thread.currentThread().getName() + " is WRITING.");
                Thread.sleep(1500); // Simulate writing time
                System.out.println(Thread.currentThread().getName() + " finished WRITING.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                rwLock.unlockWrite();
            }
        };

        // Two readers can read simultaneously
        new Thread(readTask, "Reader-1").start();
        new Thread(readTask, "Reader-2").start();

        // Writer must wait for Reader-1 and Reader-2 to finish
        new Thread(writeTask, "Writer-1").start();

        // Reader-3 must wait for Writer-1 to finish because writers block readers
        new Thread(readTask, "Reader-3").start();
    }
}
