package javaBasics.multithreading;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Implement a blocking queue using wait/notify.
 * 
 * Explanation:
 * Similar to the Producer-Consumer problem. A blocking queue blocks the calling thread
 * in `put()` if the queue is full, and blocks in `take()` if the queue is empty.
 * 
 * We use `synchronized`, `wait()`, and `notifyAll()` to manage thread coordination natively.
 */
public class CustomBlockingQueue<E> {

    private final Queue<E> queue;
    private final int capacity;

    public CustomBlockingQueue(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
    }

    public synchronized void put(E item) throws InterruptedException {
        // Must use 'while' loop to protect against spurious wake-ups
        while (queue.size() == capacity) {
            wait();
        }
        queue.add(item);
        
        // Notify any waiting threads indicating that the queue is no longer empty
        notifyAll(); 
    }

    public synchronized E take() throws InterruptedException {
        // While empty, wait for a producer to put something
        while (queue.isEmpty()) {
            wait();
        }
        E item = queue.poll();
        
        // Notify any waiting threads indicating that the queue is no longer full
        notifyAll();
        return item;
    }

    public static void main(String[] args) {
        CustomBlockingQueue<Integer> myBlockingQueue = new CustomBlockingQueue<>(3);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    myBlockingQueue.put(i);
                    System.out.println("Put: " + i);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    Integer val = myBlockingQueue.take();
                    System.out.println("Taken: " + val);
                    Thread.sleep(1000); 
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}
