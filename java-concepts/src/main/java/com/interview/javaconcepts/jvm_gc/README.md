# JVM Internals & Garbage Collection

This module covers Java Virtual Machine (JVM) internals, runtime memory layout, generational garbage collection pipelines, modern collectors (G1, ZGC), and memory leak diagnostics.

---

## 🌟 Core Concepts

The JVM compiles Java bytecode (`.class`) down to native machine code using the JIT (Just-In-Time) compiler and manages execution lifecycle, memory, and garbage collection.

### 1. JVM Runtime Data Areas
- **Heap Memory**: Shared across all threads. Stores all objects and arrays. Divided into Young Generation (Eden, Survivor spaces S0/S1) and Old Generation.
- **JVM Stack**: Thread-private. Stores stack frames containing local variables, method invocations, and partial results.
- **Metaspace**: Shared across threads. Stores class metadata. Allocated out of native memory (replaces PermGen in Java 8+, avoiding JVM OutOfMemory limits).
- **Program Counter (PC) Register**: Thread-private. Tracks the address of the current JVM instruction being executed.
- **Native Method Stack**: Thread-private. Stores native C/C++ execution states.

```
+-------------------------------------------------------------+
|                     JVM Runtime Memory                      |
|  +---------------------------+  +------------------------+  |
|  |       Thread-Shared       |  |     Thread-Private     |  |
|  |  +---------------------+  |  |  +------------------+  |  |
|  |  |    Heap Memory      |  |  |  |   JVM Stack      |  |  |
|  |  +---------------------+  |  |  +------------------+  |  |
|  |  |    Metaspace        |  |  |  |   PC Register    |  |  |
|  |  +---------------------+  |  |  +------------------+  |  |
|  +---------------------------+  +------------------------+  |
+-------------------------------------------------------------+
```

---

## ❓ Frequently Asked Interview Questions

### Q1: What is the difference between JVM Stack and Heap Memory?
| Feature | Stack Memory | Heap Memory |
| :--- | :--- | :--- |
| **Visibility** | Thread-private (exclusive to one thread). | Thread-shared (accessed by all threads). |
| **Object Storage** | Stores local variables and reference variables. | Stores the actual objects and arrays. |
| **Size Limitation** | Fixed size (defaults to ~1MB, yields `StackOverflowError`). | Dynamic size (defaults to RAM % limits, yields `OutOfMemoryError`). |
| **Lifecycle** | Fast allocation/deallocation based on method push/pop. | Managed dynamically by the Garbage Collector. |

---

### Q2: Explain the Generational GC Hypothesis?
Most objects die young. Therefore, JVM splits the Heap into:
1. **Young Generation**:
   - **Eden**: Where new objects are initially allocated.
   - **Survivor Spaces (S0 & S1)**: Active objects surviving minor GCs are copied between S0 and S1.
2. **Old Generation**:
   - Objects surviving multiple minor GC cycles (exceeding `MaxTenuringThreshold`) are promoted to Old Gen. Major GCs (Full GC) clean this area, which is more expensive.

---

### Q3: What are the differences between modern Garbage Collectors?
- **Parallel GC**: High-throughput collector. Uses multiple threads to sweep, but stops all application threads during GC phases ("Stop-the-World" or STW).
- **G1 GC (Garbage-First)**: Designed for multi-gigabyte heaps. Splits heap into equal-sized regions and cleans regions with the most garbage first to minimize STW pauses.
- **ZGC (Z Garbage Collector)**: A ultra-low latency collector introduced in Java 15. Performs all expensive GC operations concurrently with application threads, maintaining STW pauses under 1 millisecond even for terabyte-sized heaps!

---

### Q4: How do you detect and fix Memory Leaks in Java?
A memory leak occurs when objects that are no longer needed remain referenced in Heap memory, preventing the GC from reclaiming them.
- **Common Causes**: Static collections holding unused elements, unclosed resources, unremoved ThreadLocals, incorrect `equals()` overrides in keys.
- **Tools**: Generate a Heap Dump (`jmap -dump:format=b,file=heap.hprof <pid>`) and analyze it using **Eclipse Memory Analyzer (MAT)** or profile using **VisualVM** to isolate large retained size paths.

---

## 🛠️ Code Examples
- **[GarbageCollectionBasics.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/jvm_gc/GarbageCollectionBasics.java)**: Demonstrates triggering garbage collection, understanding object lifecycle, and using references.
