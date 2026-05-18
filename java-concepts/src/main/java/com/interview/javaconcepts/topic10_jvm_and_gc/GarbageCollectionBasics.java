package com.interview.javaconcepts.topic10_jvm_and_gc;

/**
 * JVM Architecture and Garbage Collection
 * 
 * 1. JVM Architecture Components:
 *    - Class Loader Subsystem (Loading, Linking, Initialization)
 *    - Runtime Data Areas (Method Area, Heap, Stack, PC Register, Native Method Stack)
 *    - Execution Engine (Interpreter, JIT Compiler, Garbage Collector)
 * 
 * 2. Garbage Collection (GC) Basics:
 *    - Process of automatically reclaiming unused memory (objects without active references).
 *    - Minor GC: Cleans the Young Generation (Eden space, Survivor spaces). Fast but frequent.
 *    - Major/Full GC: Cleans the Old Generation. Slower and causes "Stop the World" pauses.
 * 
 * 3. GC Roots:
 *    - Local variables in active threads (Stack references).
 *    - Static variables (Method area).
 *    - JNI (Java Native Interface) references.
 * 
 * 4. Common GC Algorithms:
 *    - Serial GC: Single-threaded, good for small apps.
 *    - Parallel GC: Multi-threaded for throughput.
 *    - G1 GC (Garbage-First): Divides heap into regions, predicts pause times. Default in modern Java.
 *    - ZGC (Z Garbage Collector): Scalable, low-latency GC (pause times < 1ms).
 */
public class GarbageCollectionBasics {

    public static void main(String[] args) {
        System.out.println("GC Concept Explanation loaded.");
        
        // Suggestion for interview prep: Run with -verbose:gc or -Xlog:gc*
        // to see actual garbage collection logs during execution.
        
        // Simulating memory allocation that might trigger minor GC
        for (int i = 0; i < 100_000; i++) {
            String temp = new String("Garbage " + i); 
            // temp becomes unreachable immediately after the loop iteration
        }
        
        System.gc(); // Suggests the JVM to run GC, but it's not guaranteed.
        System.out.println("Memory allocation complete.");
    }
}
