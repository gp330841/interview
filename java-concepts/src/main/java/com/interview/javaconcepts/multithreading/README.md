# Multithreading & Concurrency in Java

This module covers concurrent programming in Java, the Java Memory Model (JMM), thread pools, Virtual Threads (Project Loom), and classical concurrency coding puzzles.

---

## 🌟 Core Concepts

Java supports multithreading natively at the language level. Modern concurrency is managed through low-level primitives (`volatile`, `synchronized`) and high-level wrappers (`java.util.concurrent`).

### 1. Thread Lifecycle
A thread can reside in one of the following states:
`NEW` ➡️ `RUNNABLE` ➡️ `BLOCKED` (waiting for lock) ➡️ `WAITING` (wait/join) ➡️ `TIMED_WAITING` (sleep) ➡️ `TERMINATED`.

### 2. The Java Memory Model (JMM)
The JMM defines how threads interact through memory. It establishes the critical **Happens-Before Relationship**:
- **Volatile Variable Rule**: A write to a `volatile` variable happens-before every subsequent read of that same variable. This prevents compiler/CPU instruction reordering and guarantees thread-visibility.
- **Monitor Lock Rule**: An unlock on a monitor happens-before every subsequent lock on that same monitor.

---

## ❓ Frequently Asked Interview Questions

### Q1: What is the difference between `volatile` and `synchronized`?
| Feature | `volatile` | `synchronized` |
| :--- | :--- | :--- |
| **Applicability** | Variables only | Methods and blocks only |
| **Type of Operation** | Non-blocking (lock-free) | Blocking (requires thread locking) |
| **Visibility** | Guarantees visibility across threads | Guarantees both visibility and atomicity |
| **Mutual Exclusion** | No (multiple threads can write at once) | Yes (only one thread runs block at a time) |

---

### Q2: What is a Deadlock and how can we prevent it?
A deadlock occurs when thread A holds lock 1 and waits for lock 2, while thread B holds lock 2 and waits for lock 1. Neither can proceed.
- **Conditions for Deadlock (Coffman Conditions)**:
  1. Mutual Exclusion
  2. Hold and Wait
  3. No Preemption
  4. Circular Wait
- **Prevention**: Acquire locks in a strict global order, use timed lock acquisitions (`tryLock()`), or avoid nesting locks altogether.

---

### Q3: Explain Virtual Threads in Java 21 (Project Loom)?
> [!IMPORTANT]
> A highly modern Java concurrency question. Virtual threads are lightweight threads that do not map 1:1 to OS threads.

- **Classic Platform Threads**: Map 1:1 to operating system threads. They are expensive (require ~1MB stack memory), and context switching consumes OS cycles. Limit concurrency to a few thousand threads.
- **Virtual Threads**: Managed by the JVM. They are extremely lightweight (~hundreds of bytes stack memory), mapping thousands of virtual threads onto a few OS "Carrier Threads". When a virtual thread makes a blocking IO call, the JVM unmounts it from the carrier thread, freeing the carrier thread to execute other virtual tasks.

---

### Q4: What is the difference between `Runnable` and `Callable`?
- **Runnable**: Defines `run()` return-type `void`, cannot throw checked exceptions.
- **Callable**: Defines `call()` return-type `V` (generic result), can throw checked exceptions. Used with `ExecutorService` returning a `Future` handle.

---

### Q5: How does Double-Checked Locking work for Singleton?
To avoid thread-synchronization overhead every time `getInstance()` is called, we check if instance is null twice, locking only once.
The instance variable **must** be declared `volatile` to prevent instruction reordering during instantiation (avoiding exposing a partially initialized object).

---

## 🛠️ Code Examples
- **[ThreadCreation.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/ThreadCreation.java)**: Classic Thread vs Runnable vs Callable.
- **[VolatileKeyword.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/VolatileKeyword.java)**: Practical visibility issues solved by volatile.
- **[VirtualThreadsExample.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/VirtualThreadsExample.java)**: Running massive virtual concurrent tasks.
- **[CompletableFuturesExample.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/multithreading/CompletableFuturesExample.java)**: Asynchronous non-blocking pipelines.
- **Coding Interview Puzzles in this module**:
  - `Q1_OddEvenPrinter.java`: Print odd/even using two threads.
  - `Q2_ProducerConsumer.java`: Producer-Consumer pattern using wait/notify.
  - `Q6_CustomThreadPool.java`: Implement thread pool manually.
  - `Q8_CustomBlockingQueue.java`: Implement bounded thread-safe queue.
