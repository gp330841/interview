package com.interview.javaconcepts.multithreading.concepts;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Senior Concept: Lock-Free Stack (Treiber Stack) & The ABA Problem.
 * 
 * Lock-free data structures achieve thread safety without traditional mutual exclusion (locks).
 * They rely on Atomic CAS loop retries, avoiding context switching and thread blocking.
 * 
 * The Treiber Stack:
 * - A LIFO stack implemented with a lock-free singly-linked list.
 * - Uses an AtomicReference to the head node.
 * - Push: Creates a new node, points its next reference to the current head, and CAS-updates head.
 * - Pop: Reads current head, reads head.next, and CAS-updates head to head.next. Spins on contention.
 * 
 * THE ABA PROBLEM:
 * A classic issue in lock-free algorithms (especially in languages with manual memory management like C/C++):
 * 1. Thread 1 reads top of stack node 'A'. Node 'A' points to 'B'.
 * 2. Thread 1 is suspended right before doing CAS(A, B).
 * 3. Thread 2 pops 'A', then pops 'B' (stack is now empty).
 * 4. Thread 2 pushes 'C', and then pushes 'A' back onto the stack.
 * 5. Thread 1 resumes and executes CAS(A, B). The head is indeed 'A', so the CAS succeeds!
 * 6. However, top is set to 'B', which was already popped and might be garbage collected or recycled!
 * 7. In Java, Garbage Collection prevents immediate recycling bugs (reference comparison prevents ABA recycled node issues),
 *    but ABA can still occur if logical node state changes without reference updates.
 * 
 * Mitigation:
 * Use a stamped reference like AtomicStampedReference which updates both the reference AND an integer stamp (generation count) atomically.
 */
public class LockFreeStack<T> {

    // Atomic head pointer of the singly-linked list
    private final AtomicReference<Node<T>> head = new AtomicReference<>();

    private static class Node<T> {
        final T value;
        Node<T> next;

        Node(T value) {
            this.value = value;
        }
    }

    /**
     * Push value onto the top of stack.
     * Lock-free CAS retry loop.
     */
    public void push(T value) {
        Node<T> newHead = new Node<>(value);
        Node<T> oldHead;
        
        do {
            oldHead = head.get();       // Get current head
            newHead.next = oldHead;     // Link new node to current head
            
            // CAS update: replace oldHead with newHead.
            // If another thread updated head in the meantime, compareAndSet returns false, and we loop again!
        } while (!head.compareAndSet(oldHead, newHead));
    }

    /**
     * Pop value off the top of stack.
     * Lock-free CAS retry loop. Returns null if empty.
     */
    public T pop() {
        Node<T> oldHead;
        Node<T> newHead;
        
        do {
            oldHead = head.get();
            if (oldHead == null) {
                return null; // Stack is empty
            }
            newHead = oldHead.next; // Read the next pointer
            
            // CAS update: replace oldHead with newHead.
            // Loop retries if head changed under us.
        } while (!head.compareAndSet(oldHead, newHead));

        return oldHead.value;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Starting Lock-Free Treiber Stack Demo ===");
        LockFreeStack<Integer> stack = new LockFreeStack<>();

        Runnable producer = () -> {
            for (int i = 0; i < 1000; i++) {
                stack.push(i);
            }
        };

        Runnable consumer = () -> {
            int poppedCount = 0;
            for (int i = 0; i < 1000; i++) {
                if (stack.pop() != null) {
                    poppedCount++;
                }
            }
            System.out.println(Thread.currentThread().getName() + " popped " + poppedCount + " elements.");
        };

        Thread t1 = new Thread(producer, "Producer-1");
        Thread t2 = new Thread(producer, "Producer-2");
        Thread t3 = new Thread(consumer, "Consumer-1");
        Thread t4 = new Thread(consumer, "Consumer-2");

        t1.start();
        t2.start();
        Thread.sleep(10); // Let producers start pushing
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        // Drain remaining
        int remaining = 0;
        while (stack.pop() != null) {
            remaining++;
        }
        System.out.println("Driver completed. Remaining items in stack = " + remaining);
    }
}
