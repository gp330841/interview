# Advanced Multithreading & Concurrency in Java

This module covers concurrent programming in Java, ranging from low-level thread mechanics and the Java Memory Model (JMM) to high-performance concurrent synchronizers, non-blocking lock-free structures, and Project Loom's Virtual Threads.

---

## 📂 Package Reorganization

The package has been structured to cleanly isolate **Core Concepts & Tutorials** from **Classical Concurrency Interview Coding Questions**:

*   **`com.interview.javaconcepts.multithreading.concepts`**: Dedicated to in-depth conceptual code demos, API breakdowns, and advanced concurrency primitives.
*   **`com.interview.javaconcepts.multithreading.questions`**: Dedicated to implementing classical concurrent interview challenges (e.g., custom queues, coordination patterns, synchronizers).

---

## 🌟 Core Concepts & Code Demos

Below is a breakdown of the comprehensive concept files implemented in the `concepts/` directory:

| Topic / Primitives | Source File | Key Architectural Takeaway |
| :--- | :--- | :--- |
| **Thread Basics & Lifecycle** | [`ThreadBasicsDemo.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/ThreadBasicsDemo.java) | Deep-dive into JVM thread states (`NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`, `TERMINATED`), `join()` blocking, priority scheduler hints, and **Cooperative Interruption** practices (re-asserting interrupt flags). |
| **Thread Creation** | [`ThreadCreation.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/ThreadCreation.java) | Contrast `Thread` inheritance vs `Runnable` composition vs `Callable<V>` futures. |
| **Lock Synchronization** | [`SynchronizationDemo.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/SynchronizationDemo.java) | Instance locks (`this`) vs Class-level locks (`Class`), fine-grained synchronized blocks, and CPU monitor lock reentrancy. |
| **Memory Visibility** | [`VolatileKeyword.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/VolatileKeyword.java) & [`OrderWorkerBroken.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/OrderWorkerBroken.java) | Visualizes local CPU register/L1 thread caching. Demonstrates how `volatile` creates memory barriers to force flushing to main memory. |
| **Deadlock & Prevention** | [`DeadlockDemo.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/DeadlockDemo.java) | Recreates Coffman deadlock conditions (Circular Wait). Shows mitigation using strict lock ordering and non-blocking timed acquisitions (`tryLock()`). |
| **Low-Level Coordination** | [`ThreadCommunicationDemo.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/ThreadCommunicationDemo.java) | Monitor-based thread communication using `wait()`, `notify()`, and `notifyAll()`. Demonstrates avoiding **Spurious Wakeups** via standard `while` loop checks. |
| **Executor Framework** | [`ExecutorsAndFutures.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/ExecutorsAndFutures.java) | Explains `ThreadPoolExecutor` architecture, work queues, core vs max thread scaling, and futures. |
| **Asynchronous Pipelines** | [`CompletableFuturesExample.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/CompletableFuturesExample.java) | Builds non-blocking event-driven pipeline composition (`thenApplyAsync`, `thenCombine`). |
| **Explicit Locking** | [`ReentrantLockAndCondition.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/ReentrantLockAndCondition.java) | Showcase `ReentrantLock` properties (fairness policies, explicit unlocking). Implements a highly optimized bounded buffer using multiple `Condition` queues. |
| **Concurrent Synchronizers** | [`SynchronizersDemo.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/SynchronizersDemo.java) | Complete implementation of **CountDownLatch** (one-time latches), **CyclicBarrier** (reusable checkpoints), and **Semaphore** (resource throttling). |
| **Fork/Join Work-Stealing** | [`ForkJoinPoolDemo.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/ForkJoinPoolDemo.java) | Divide-and-conquer processing using `RecursiveTask<V>`. Maps out internal Work-Stealing deque behaviors. |
| **Atomics & CAS** | [`AtomicClassesDemo.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/AtomicClassesDemo.java) | Hardware-level lock-free Compare-And-Swap (CAS). Showcases highly memory-efficient **`AtomicIntegerFieldUpdater`** to avoid wrapper class allocations. |
| **StampedLock & Optimistic Reads**| [`StampedLockExample.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/StampedLockExample.java) | Implements lock-free **Optimistic Reading** with validation fallbacks. Prevents reader-writer starvation. Covers its critical non-reentrant rule. |
| **Striped Cell Counters** | [`LongAdderPerformance.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/LongAdderPerformance.java) | Performance benchmark showing `LongAdder` vs `AtomicLong` CAS loop contention. Explains cache-line cell-striping. |
| **Loom Carrier Pinning** | [`VirtualThreadPinningDemo.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/VirtualThreadPinningDemo.java) | Visualizes Project Loom virtual threads pinning OS carrier threads inside `synchronized` blocks. Outlines the clean refactoring fix using `ReentrantLock`. |
| **Lock-Free Treiber Stack** | [`LockFreeStack.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/concepts/LockFreeStack.java) | Builds a lock-free Treiber stack using `AtomicReference` CAS loops. Details the theoretical **ABA Problem** and its mitigation. |

---

## ❓ Coding Interview Puzzles

These classic concurrency questions are located in the `questions/` package:

1.  **[`Q1_OddEvenPrinter.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/questions/Q1_OddEvenPrinter.java) / [`OddEven.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/questions/OddEven.java)**: Coordinate alternating numbers between two threads using wait/notify locks.
2.  **[`Q2_ProducerConsumer.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/questions/Q2_ProducerConsumer.java)**: Standard Producer-Consumer blocking queue using synchronized wait/notify monitors.
3.  **[`Q3_ThreadSafeSingleton.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/questions/Q3_ThreadSafeSingleton.java)**: Double-Checked Locking (DCL) singleton pattern. Explains why the instance variable **must** be marked `volatile` to prevent instruction reordering during instantiation.
4.  **[`Q4_Print1ToNUsing3Threads.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/questions/Q4_Print1ToNUsing3Threads.java)**: Print sequential numbers 1 to N using three threads in round-robin order.
5.  **[`Q5_PrintABCSequence.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/questions/Q5_PrintABCSequence.java)**: Coordinate threads to print "ABCABC..." repeatedly.
6.  **[`Q6_CustomThreadPool.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/questions/Q6_CustomThreadPool.java)**: Implement a custom, runnable thread pool mimicking `FixedThreadPool` with worker threads and a custom task queue.
7.  **[`Q7_DiningPhilosophers.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/questions/Q7_DiningPhilosophers.java)**: Classical resource allocation puzzle. Prevents circular wait by forcing the last philosopher to pick up chopsticks in reverse order.
8.  **[`Q8_CustomBlockingQueue.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/questions/Q8_CustomBlockingQueue.java)**: Implement a custom bounded blocking queue from scratch using lock monitors.
9.  **[`Q9_MultithreadedFibonacci.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/questions/Q9_MultithreadedFibonacci.java)**: Compute fibonacci terms concurrently using parallel callable threads.
10. **[`Q10_CustomReadWriteLock.java`](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/questions/Q10_CustomReadWriteLock.java)**: Build a custom read-write lock protecting shared data from writers while allowing concurrent readers.

---

## 🧠 Architectural Deep-Dive for Senior Engineers

### 1. The Java Memory Model (JMM) & Happens-Before Guarantees
Modern CPU architectures do not write values directly to main memory; they write to fast local L1/L2/L3 caches. Compilation steps (both JIT and compiler) along with out-of-order CPU executions frequently reorder assembly instructions to maximize instruction pipelining.
The JMM defines formal guarantees to enforce thread safety:
*   **Happens-Before Relationship**: A set of visibility rules. If action $A$ happens-before $B$, then $A$'s changes are guaranteed to be fully visible to the thread executing $B$.
*   **Volatile Variables**: A write to a `volatile` variable establishes a happens-before relationship to any subsequent read of that variable. At the CPU level, this inserts a **Memory Barrier (Fence)** instruction, flushing write buffers to main memory and invalidating readers' L1 caches.
*   **Monitor Locks**: Releasing a monitor lock happens-before acquiring that exact same monitor.

### 2. False Sharing & Striped Counters (`LongAdder` vs `AtomicLong`)
CPU cache lines are typically 64 bytes wide. If two variables are physically located adjacent to each other in memory, they occupy the same cache line. If Thread 1 is writing to variable $X$ and Thread 2 is writing to variable $Y$:
*   When Thread 1 writes to $X$, it invalidates the entire cache line in Thread 2's core.
*   Thread 2 must reload the cache line from L3 or main memory, even though it is writing to $Y$, not $X$.
*   This performance-degrading phenomenon is called **False Sharing**.
*   **`LongAdder`** resolves false sharing internally by using the `@Contended` annotation on its dynamic `Cell` array, forcing separate cells onto distinct cache lines. Coupled with thread-hashing, it eliminates CAS-spinning bottlenecks.

### 3. Work-Stealing in `ForkJoinPool`
Standard thread pools use a single shared blocking task queue. Under high thread counts, threads contend heavily for the queue's lock.
The `ForkJoinPool` avoids this using **Work-Stealing**:
*   Every worker thread owns a private **Double-Ended Queue (Deque)**.
*   When a task spawns a subtask (`fork()`), it pushes it to the **HEAD** of its own deque.
*   Worker threads process their own tasks in **LIFO** order (popping from the HEAD), maximizing cache locality.
*   When a thread is idle, it steals a task from the **TAIL** of a busy thread's deque in **FIFO** order (minimizing lock contention with the owner, who is working at the head).

### 4. Carrier Thread Pinning in Project Loom (Virtual Threads)
Project Loom introduces Virtual Threads (lightweight M:N concurrent fibers). Instead of a 1:1 mapping to OS threads, thousands of virtual threads are scheduled onto a small pool of carrier OS threads.
When a virtual thread makes a blocking call (e.g., waiting on socket reading, sleeping), the JVM's virtual thread scheduler unmounts the virtual thread, freeing the carrier thread.
**The Pinning Defect**:
If a virtual thread performs blocking operations inside a `synchronized` block/method, or inside a native JNI call, Loom *cannot* unmount the thread. It becomes **Pinned** to the carrier thread.
If all carrier threads become pinned, the system stalls. To prevent pinning, senior developers **must** refactor blocking synchronized sections to use explicit `ReentrantLock` wrappers.
