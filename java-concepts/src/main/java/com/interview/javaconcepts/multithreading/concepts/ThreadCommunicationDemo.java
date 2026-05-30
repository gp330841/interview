package com.interview.javaconcepts.multithreading.concepts;

import java.util.ArrayList;
import java.util.List;

/**
 * Core Concept: Thread Communication using wait(), notify(), and notifyAll().
 * 
 * wait() and notify()/notifyAll() are low-level primitives defined on the Object class.
 * They allow threads to coordinate on condition changes of a shared monitor object.
 * 
 * Essential Rules:
 * 1. Must be called within a synchronized block/method of the monitor object. (Throws IllegalMonitorStateException otherwise).
 * 2. Always wait inside a 'while' loop, never an 'if' block. This prevents "Spurious Wakeups" (when a thread wakes up without being notified).
 * 3. notify() wakes up a single arbitrary thread waiting on this monitor.
 * 4. notifyAll() wakes up all threads waiting on this monitor. It is safer to avoid "missed signals" or lost wakeups.
 */
public class ThreadCommunicationDemo {

    private final List<String> messageQueue = new ArrayList<>();
    private final int CAPACITY = 1;

    public static void main(String[] args) throws InterruptedException {
        ThreadCommunicationDemo demo = new ThreadCommunicationDemo();

        System.out.println("=== Starting Thread Communication (Producer-Consumer) ===");
        
        // Consumer thread
        Thread consumer = new Thread(demo::consume, "Consumer");
        
        // Producer thread
        Thread producer = new Thread(demo::produce, "Producer");

        consumer.start();
        Thread.sleep(100); // Give consumer time to run and enter wait state
        
        producer.start();

        consumer.join();
        producer.join();
    }

    /**
     * Producer puts a message into the queue when there is capacity.
     */
    public void produce() {
        String[] messages = {"Hello", "World", "Exit"};
        for (String msg : messages) {
            synchronized (this) {
                // RULE 2: Use while loop to check condition, avoiding spurious wakeups
                while (messageQueue.size() == CAPACITY) {
                    try {
                        System.out.println(Thread.currentThread().getName() + ": Queue is full. Waiting...");
                        // Releases the lock on 'this' monitor and pauses execution
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                System.out.println(Thread.currentThread().getName() + ": Producing message -> " + msg);
                messageQueue.add(msg);
                
                // Wake up the consumer waiting on 'this' monitor
                notifyAll(); 
            }
            
            // Artificial delay to make interleaving obvious
            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    /**
     * Consumer pulls message from the queue when it is not empty.
     */
    public void consume() {
        while (true) {
            synchronized (this) {
                // RULE 2: Use while loop to check condition
                while (messageQueue.isEmpty()) {
                    try {
                        System.out.println(Thread.currentThread().getName() + ": Queue is empty. Waiting...");
                        // Releases the lock on 'this' monitor and pauses execution
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                String msg = messageQueue.remove(0);
                System.out.println(Thread.currentThread().getName() + ": Consumed message -> " + msg);
                
                // Wake up the producer waiting on 'this' monitor
                notifyAll();

                if ("Exit".equals(msg)) {
                    System.out.println(Thread.currentThread().getName() + ": Exit message received. Terminating consumer.");
                    break;
                }
            }
        }
    }
}
