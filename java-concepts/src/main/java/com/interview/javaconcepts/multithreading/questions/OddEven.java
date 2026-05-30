package com.interview.javaconcepts.multithreading.questions;

public class OddEven {
    int max;
    int counter = 1;

    OddEven(int max) {
        this.max = max;
    }

    void printEven() {
        synchronized (this) {
            while (counter <= max) {
                if(counter%2==0) {
                    System.out.println(Thread.currentThread().getName() + " : " + counter);
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //notify
                if(counter<=max) {
                    counter++;
                    notify();
                }
            }
        }

    }

    void printOdd() {
        synchronized (this) {
            while(counter<=max) {
                if(counter%2==1) {
                    System.out.println(Thread.currentThread().getName() + " : " + counter);
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //notify
                if(counter<=max) {
                    counter++;
                    notify();
                }
            }
        }
    }

    static void main() {
        OddEven oddEven = new OddEven(10);
        Thread oddThread = new Thread(oddEven::printOdd, "oddThread");
        Thread evenThread = new Thread(()-> oddEven.printEven(), "evenThread");
        oddThread.start();
        evenThread.start();
        try {
            oddThread.join();
            evenThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
