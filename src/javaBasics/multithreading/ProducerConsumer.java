package javaBasics.multithreading;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Producer–Consumer problem using wait/notify.
 * 
 * Explanation:
 * Represents the classic synchronization problem where a Producer thread produces data into a buffer,
 * and a Consumer thread consumes it. 
 * - If the buffer is full, the Producer must wait.
 * - If the buffer is empty, the Consumer must wait.
 */
public class ProducerConsumer {

    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer(5); // Buffer size of 5

        Thread producer = new Thread(() -> {
            int value = 0;
            while (true) {
                try {
                    buffer.produce(value++);
                    Thread.sleep(500); // Simulate time taken to produce
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            while (true) {
                try {
                    buffer.consume();
                    Thread.sleep(1000); // Simulate time taken to consume
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Consumer");

        producer.start();
        consumer.start();
    }

    static class SharedBuffer {
        private Queue<Integer> queue = new LinkedList<>();
        private int capacity;

        public SharedBuffer(int capacity) {
            this.capacity = capacity;
        }

        public synchronized void produce(int value) throws InterruptedException {
            // Wait if the buffer is full
            while (queue.size() == capacity) {
                System.out.println("Buffer is full, Producer is waiting...");
                wait();
            }

            queue.add(value);
            System.out.println("Produced: " + value);
            
            // Notify the consumer that there's an item to consume
            notifyAll();
        }

        public synchronized int consume() throws InterruptedException {
            // Wait if the buffer is empty
            while (queue.isEmpty()) {
                System.out.println("Buffer is empty, Consumer is waiting...");
                wait();
            }

            int value = queue.poll();
            System.out.println("Consumed: " + value);
            
            // Notify the producer that there's space available
            notifyAll();
            return value;
        }
    }
}
