# Data Structures & LRU Cache Implementation

This module covers custom data structure designs in Java, specifically the implementation of a thread-safe generic Least Recently Used (LRU) Cache using a HashMap and a Doubly Linked List.

---

## 🌟 Core Concepts

Custom data structure design is a staple of technical coding interviews, testing your understanding of time-complexities, generics, and pointer manipulations.

### The Least Recently Used (LRU) Cache
An LRU Cache is a key-value store that evicts the least recently accessed elements when it reaches capacity.
To achieve $O(1)$ operations for both `get` and `put`, we combine two structures:
1. **HashMap**: Stores key-to-Node mappings for instant $O(1)$ node lookups.
2. **Doubly Linked List (DLL)**: Maintains access order. The most recently accessed node is moved to the **head**, while the least recently accessed is located at the **tail** (eviction target).

```
          [Head] <-> [Node A] <-> [Node B] <-> [Node C] <-> [Tail]
             ^                                                 ^
      (Most Recent)                                     (Least Recent)
```

---

## ❓ Frequently Asked Interview Questions

### Q1: Why is a Doubly Linked List preferred over a Singly Linked List for LRU Cache?
In a Singly Linked List, removing a node requires finding its predecessor, which takes $O(N)$ time because we can only traverse forward.
A Doubly Linked List holds references to both `prev` and `next` nodes. This allows us to detach and remove any node in $O(1)$ time without searching, making it ideal for high-throughput caching.

---

### Q2: How can we make our custom LRU Cache Thread-Safe?
- **Approach 1**: Synchronize all public methods (`get`, `put`). This is easy but limits performance under concurrent reads.
- **Approach 2**: Use a `ReentrantReadWriteLock`. Multiple threads can read concurrently, while write operations lock the structure exclusively.
- **Approach 3**: Leverage standard JDK classes like `LinkedHashMap` and wrap it using `Collections.synchronizedMap()`, or override its `removeEldestEntry` method.

---

### Q3: What is the benefit of using Generics in Cache Design?
Generics allow the cache to accept any key and value type (e.g., `<Integer, User>` or `<String, Product>`) while enforcing compile-time type safety. This prevents casting errors at runtime.

---

## 🛠️ Code Examples
- **[CacheInterface.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/data_structures/CacheInterface.java)**: Defines the standard lookup and mutation contract for caching components.
- **[GenericCache.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/data_structures/GenericCache.java)**: Base caching structure.
- **[Node.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/data_structures/Node.java)** & **[DLL.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/data_structures/DLL.java)**: Implements custom pointer connections for Doubly Linked List representation.
- **[LRUCache.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/data_structures/LRUCache.java)**: Combines the generic DLL and HashMap into an optimized $O(1)$ LRU Cache.
