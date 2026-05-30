package com.interview.javaconcepts.multithreading;

public class OrderWorkerBroken {

    static boolean acceptingOrders = true;

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            int processed = 0;
            while (acceptingOrders) {
                processed++;
            }
            System.out.println("Worker stopped after " + processed + " orders");
        });

        worker.start();
        Thread.sleep(100);
        acceptingOrders = false;
        System.out.println("Main thread flipped the flag");

        worker.join(2000);
        if (worker.isAlive()) {
            System.out.println("Worker is STILL running");
        }
    }
}