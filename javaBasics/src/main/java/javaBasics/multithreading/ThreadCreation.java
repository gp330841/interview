
package javaBasics.multithreading;

/**
 * Demonstrates the basic ways to create and run threads in Java:
 * 1. Extending the Thread class
 * 2. Implementing the Runnable interface (Preferred)
 */
public class ThreadCreation {

    public static void main(String[] args) {
        // 1. Extending Thread class
        System.out.println("Main thread running: " + Thread.currentThread().getName());
        
        MyThread t1 = new MyThread();
        t1.setName("MyCustomThread");
        t1.start(); // Always call start(), not run(). Calling run() executes sequentially like a normal method call.
        
        // 2. Implementing Runnable interface (Better approach)
        // Runnable is a functional interface type, so we can use a lambda expression!
        Runnable myRunnable = () -> {
            System.out.println("Thread running from Runnable: " + Thread.currentThread().getName());
            try {
                Thread.sleep(500); // Pause for 500ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Runnable thread finished.");
        };

        Thread t2 = new Thread(myRunnable, "MyRunnableThread");
        t2.start();
        
        try {
            // Join makes the main thread wait for t1 and t2 to finish before continuing
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main thread finished.");
    }
}

class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running from Thread subclass: " + Thread.currentThread().getName());
    }
}
