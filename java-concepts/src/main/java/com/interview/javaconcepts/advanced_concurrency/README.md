# Advanced Concurrency in Java

This module covers Java's advanced concurrent utilities, synchronizers, thread-safety controls, lock structures, and ThreadLocals.

---

## 🌟 Core Concepts

Beyond `synchronized`, `java.util.concurrent` provides standard coordination synchronizers like `CountDownLatch`, `CyclicBarrier`, and `Semaphore`.

### 1. Synchronizers
- **CountDownLatch**: A one-time synchronization aid that allows one or more threads to wait until a set of operations performed in other threads completes.
- **CyclicBarrier**: A reusable barrier that allows a fixed set of threads to all wait for each other to reach a common barrier point before continuing.
- **Semaphore**: Manages a set of permits. Used to bound resource access (e.g., limiting database connections).

---

## ❓ Frequently Asked Interview Questions

### Q1: What is the difference between `CountDownLatch` and `CyclicBarrier`?
> [!IMPORTANT]
> A common question highlighting synchronizer design differences.

| Feature | CountDownLatch | CyclicBarrier |
| :--- | :--- | :--- |
| **Reusability** | Cannot be reset. One-time use only. | Can be reset and reused after threads are released. |
| **Mechanics** | Main thread waits (using `await()`) until count becomes 0 via `countDown()`. | Workers call `await()` and block until all worker threads arrive. |
| **Thread count** | The decrementing threads can be the same or different. | The number of waiting threads must match the barrier capacity. |
| **Barrier Action** | None. | Can execute an optional runnable task when the barrier is tripped. |

---

### Q2: What is CAS (Compare-And-Swap) and how do Atomic classes use it?
Atomic classes (like `AtomicInteger`) provide lock-free thread safety. Under the hood, they use **CAS (Compare-And-Swap)** CPU instructions:
- CAS takes three arguments: a memory location ($V$), the expected old value ($A$), and a new value ($B$).
- It updates the memory location to $B$ only if the current value is $A$. Otherwise, it does nothing and reports failure (allowing the thread to retry).
- It is highly performant compared to traditional locks because it avoids thread suspension and context switches.

---

### Q3: What is a `ThreadLocal` and how can it cause memory leaks?
`ThreadLocal` provides thread-local variables. Each thread holds an implicit reference to its copy of a thread-local variable in a custom map (`ThreadLocalMap`).
- **Memory Leak Danger**:
  In modern web servers, threads are reused using Thread Pools. If a thread-local variable is allocated but not explicitly removed, it remains stored in the thread's local map.
  Since the thread never terminates, the associated large objects cannot be garbage collected, creating a **ThreadLocal Memory Leak**.
- **Solution**: Always call `threadLocalVariable.remove()` in a `finally` block once the operation completes.

---

### Q4: ReentrantLock vs Synchronized?
- **Synchronized**: Implicit locking. Easy to write. Automatically releases locks when exception occurs. Non-fair by default.
- **ReentrantLock**: Explicit locking. Offers features like **Fairness control** (gives lock to longest waiting thread), **Interruptible lock acquisition**, and **Timed lock attempts** (`tryLock()`). Requires explicit `unlock()` inside a `finally` block.

---

## 🛠️ Code Examples
- **[CountDownLatchExample.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/advanced_concurrency/CountDownLatchExample.java)**: Practical usage of coordinating worker threads using CountDownLatch.
