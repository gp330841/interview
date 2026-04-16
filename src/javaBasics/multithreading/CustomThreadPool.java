package javaBasics.multithreading;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implement a custom ThreadPool from scratch.
 * 
 * Explanation:
 * A ThreadPool mainly consists of:
 * 1. A Task Queue (BlockingQueue) where submitted runnables wait.
 * 2. A predefined number of Worker Threads that constantly pop tasks from the queue and run them.
 */
public class CustomThreadPool {

    private final LinkedBlockingQueue<Runnable> taskQueue;
    private final WorkerThread[] workerThreads;
    private volatile boolean isStopped = false;

    public CustomThreadPool(int numThreads) {
        taskQueue = new LinkedBlockingQueue<>();
        workerThreads = new WorkerThread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            workerThreads[i] = new WorkerThread();
            workerThreads[i].start();
        }
    }

    // Submitting a new task to the queue
    public synchronized void execute(Runnable task) {
        if (isStopped) {
            throw new IllegalStateException("ThreadPool is stopped");
        }
        taskQueue.offer(task);
    }

    public synchronized void stop() {
        this.isStopped = true;
        for (WorkerThread thread : workerThreads) {
            thread.doStop();
        }
    }

    // The inner worker thread class
    private class WorkerThread extends Thread {
        private boolean isStoppedThread = false;

        public synchronized void doStop() {
            isStoppedThread = true;
            this.interrupt(); // Break pool threads out of waiting on the queue
        }

        @Override
        public void run() {
            while (!isStoppedThread) {
                try {
                    // take() blocks until a task becomes available
                    Runnable runnable = taskQueue.take();
                    runnable.run();
                } catch (InterruptedException e) {
                    // Check if we should stop
                    if (isStoppedThread) {
                        break;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        CustomThreadPool threadPool = new CustomThreadPool(3);

        // Submit 10 tasks to a 3-thread pool
        for (int i = 1; i <= 10; i++) {
            int taskNo = i;
            threadPool.execute(() -> {
                System.out.println("Task " + taskNo + " is running by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(500); // Simulate some work
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // Wait a bit before shutting down to allow tasks to be picked up
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadPool.stop();
        System.out.println("ThreadPool manually shut down.");
    }
}
