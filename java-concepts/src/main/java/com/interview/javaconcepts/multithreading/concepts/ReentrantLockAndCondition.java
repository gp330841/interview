package com.interview.javaconcepts.multithreading.concepts;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Core Concept: ReentrantLock & Condition.
 * 
 * ReentrantLock provides explicit locking capabilities beyond synchronized methods/blocks:
 * 1. Fairness Policy: Can create a fair lock where thread acquisition order is FIFO (avoids starvation, but reduces throughput).
 * 2. tryLock(): Non-blocking lock acquisition with optional timeout.
 * 3. lockInterruptibly(): Allows thread to lock but still respond to interruption signals.
 * 4. Multiple Conditions: A single ReentrantLock can support multiple Condition objects, enabling finer coordination
 *    (e.g., a buffer that separates "buffer not full" and "buffer not empty" queues, avoiding unnecessary notify wakeups).
 */
public class ReentrantLockAndCondition {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Starting Bounded Buffer Demo using ReentrantLock and Condition ===");
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(3);

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    int val = buffer.take();
                    System.out.println("Consumer: Consumed -> " + val);
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    System.out.println("Producer: Attempting to put -> " + i);
                    buffer.put(i);
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();
        Thread.sleep(100);
        producer.start();

        consumer.join();
        producer.join();
    }

    /**
     * A thread-safe bounded buffer implemented using ReentrantLock and separate Conditions.
     */
    static class BoundedBuffer<T> {
        private final Lock lock = new ReentrantLock(true); // Fair lock for FIFO thread acquisition
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        private final Object[] items;
        private int putPtr, takePtr, count;

        public BoundedBuffer(int capacity) {
            this.items = new Object[capacity];
        }

        public void put(T x) throws InterruptedException {
            lock.lock(); // Explicitly acquire the lock
            try {
                // If full, await on the "notFull" condition.
                // When we await, we release the lock.
                while (count == items.length) {
                    System.out.println("   [Buffer FULL] Producer is waiting on 'notFull' condition...");
                    notFull.await(); 
                }
                
                items[putPtr] = x;
                if (++putPtr == items.length) putPtr = 0;
                count++;
                
                // Wake up threads waiting on "notEmpty" since we just put an item
                notEmpty.signal(); 
            } finally {
                // ALWAYS unlock in a finally block to prevent lock leaks if code throws exception
                lock.unlock(); 
            }
        }

        @SuppressWarnings("unchecked")
        public T take() throws InterruptedException {
            lock.lock();
            try {
                // If empty, await on the "notEmpty" condition
                while (count == 0) {
                    System.out.println("   [Buffer EMPTY] Consumer is waiting on 'notEmpty' condition...");
                    notEmpty.await();
                }
                
                T x = (T) items[takePtr];
                if (++takePtr == items.length) takePtr = 0;
                count--;
                
                // Wake up threads waiting on "notFull" since we just took an item
                notFull.signal();
                return x;
            } finally {
                lock.unlock();
            }
        }
    }
}
