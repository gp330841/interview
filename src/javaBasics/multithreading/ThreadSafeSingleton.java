package javaBasics.multithreading;

/**
 * Implement a thread-safe Singleton (Double-Checked Locking).
 * 
 * Explanation:
 * The Singleton pattern ensures roughly one instance of a class is created.
 * In a multithreaded environment, traditional lazy initialization is not thread-safe.
 * Double-Checked Locking is used to minimize the performance overhead of synchronization.
 * 
 * Key points:
 * 1. The constructor must be private.
 * 2. The variable holding the instance must be `volatile`.
 *    (Without `volatile`, a thread might see a partially initialized object due to instruction reordering).
 * 3. We check for null, synchronize if necessary, and check for null again.
 */
public class ThreadSafeSingleton {

    // Requirement 2: Volatile reference to prevent instruction reordering
    private static volatile ThreadSafeSingleton instance;

    // Requirement 1: Private constructor
    private ThreadSafeSingleton() {
        System.out.println("Singleton instance created! By: " + Thread.currentThread().getName());
    }

    public static ThreadSafeSingleton getInstance() {
        // First check (Lock-free): Avoid synchronization once instance is initialized
        if (instance == null) {
            
            // Synchronize only on the first few threads attempting creation concurrently
            synchronized (ThreadSafeSingleton.class) {
                
                // Second check (Double-Checked Lock): Ensures only the very first thread inside the block creates it
                if (instance == null) {
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        // Create multiple threads trying to get the instance simultaneously
        Runnable task = () -> {
            ThreadSafeSingleton s = ThreadSafeSingleton.getInstance();
            System.out.println("Got instance: " + s.hashCode() + " in thread " + Thread.currentThread().getName());
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");
        Thread t3 = new Thread(task, "Thread-3");

        t1.start();
        t2.start();
        t3.start();
    }
}
