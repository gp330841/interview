package javaBasics.multithreading;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Dining Philosophers Problem.
 * 
 * Explanation:
 * 5 philosophers sit at a round table. There's 1 chopstick between each of them (5 total).
 * A philosopher needs BOTH chopsticks (left and right) to eat.
 * 
 * Problem: If everyone picks up their left chopstick simultaneously, they will wait for the right
 * chopstick forever -> DEADLOCK!
 * 
 * Solution implemented here (Resource hierarchy):
 * Let the last philosopher pick up the right chopstick first, then the left chopstick.
 * This guarantees at least one philosopher can eat, preventing a deadlock cycle.
 */
public class DiningPhilosophers {

    public static void main(String[] args) {
        int numOfPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numOfPhilosophers];
        Lock[] chopsticks = new ReentrantLock[numOfPhilosophers];

        for (int i = 0; i < numOfPhilosophers; i++) {
            chopsticks[i] = new ReentrantLock();
        }

        for (int i = 0; i < numOfPhilosophers; i++) {
            Lock leftChopstick = chopsticks[i];
            Lock rightChopstick = chopsticks[(i + 1) % numOfPhilosophers];

            // Tie-breaker: The last philosopher picks up the right chopstick first
            if (i == numOfPhilosophers - 1) {
                philosophers[i] = new Philosopher("Philosopher " + (i + 1), rightChopstick, leftChopstick);
            } else {
                philosophers[i] = new Philosopher("Philosopher " + (i + 1), leftChopstick, rightChopstick);
            }

            Thread t = new Thread(philosophers[i]);
            t.start();
        }
    }
}

class Philosopher implements Runnable {
    private final String name;
    private final Lock leftChopstick;
    private final Lock rightChopstick;

    public Philosopher(String name, Lock leftChopstick, Lock rightChopstick) {
        this.name = name;
        this.leftChopstick = leftChopstick;
        this.rightChopstick = rightChopstick;
    }

    private void doAction(String action) throws InterruptedException {
        System.out.println(name + " " + action);
        Thread.sleep((long) (Math.random() * 100)); // Simulate time taking
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Thinking
                doAction(System.nanoTime() + ": Thinking");

                // Hungry, try to pick up chopsticks
                leftChopstick.lock();
                try {
                    doAction(System.nanoTime() + ": Picked up left chopstick");
                    rightChopstick.lock();
                    try {
                        // Eating
                        doAction(System.nanoTime() + ": Picked up right chopstick - EATING");
                        doAction(System.nanoTime() + ": Put down right chopstick");
                    } finally {
                        rightChopstick.unlock();
                    }
                    doAction(System.nanoTime() + ": Put down left chopstick (Back to thinking)");
                } finally {
                    leftChopstick.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
