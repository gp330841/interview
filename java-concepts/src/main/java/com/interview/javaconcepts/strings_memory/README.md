# String Handling & Memory Management in Java

This module covers Java's internal String mechanisms, memory optimization, and frequently asked interview questions on String manipulation.

---

## 🌟 Core Concepts

String is one of the most widely used classes in Java. Because of this, Java optimizes String storage extensively.

### 1. String Constant Pool (SCP)
The SCP is a special memory region inside the **Java Heap** (previously in PermGen in older versions) that stores string literals. When you create a string literal:
```java
String s1 = "Hello";
```
The JVM checks the SCP first. If `"Hello"` is already there, it returns the reference. If not, it creates a new String object in the SCP.

When you create a String using `new`:
```java
String s2 = new String("Hello");
```
The JVM creates a String object in the normal Heap memory, and also makes sure `"Hello"` exists inside the SCP.

---

## ❓ Frequently Asked Interview Questions

### Q1: Why is String Immutable in Java?
> [!IMPORTANT]
> String immutability is vital for security, caching, and thread-safety.

1. **String Constant Pool (SCP) Caching**: If Strings were mutable, changing one reference would affect all other variables pointing to the same literal inside the pool.
2. **Security**: Strings are heavily used for network connections, database URLs, usernames, and file paths. If mutable, a hacker could modify them after validation.
3. **Thread Safety**: Immutable objects are inherently thread-safe. Multiple threads can share strings without synchronization.
4. **HashMap Key Optimization**: Since string contents are immutable, their `hashCode` can be cached at creation time, leading to highly optimized hashmap key lookups.

---

### Q2: What is the difference between String, StringBuilder, and StringBuffer?
| Property | String | StringBuilder | StringBuffer |
| :--- | :--- | :--- | :--- |
| **Mutability** | Immutable | Mutable | Mutable |
| **Thread-Safe** | Yes (inherently) | No | Yes (synchronized methods) |
| **Performance** | Slow for mutations | Very Fast | Moderately Fast (overhead of lock acquisition) |
| **Storage** | Heap & SCP | Heap | Heap |

---

### Q3: What does the `intern()` method do?
The `intern()` method is used to manually put a string inside the String Constant Pool.
When `s.intern()` is called, if the SCP already contains a string equal to `s` (as determined by `equals()`), it returns the pool reference. Otherwise, it adds `s` to the SCP and returns its reference.

```java
String s1 = new String("Java");
String s2 = s1.intern(); // Puts "Java" in pool (if absent) and returns SCP reference
System.out.println(s2 == "Java"); // true
```

---

### Q4: How many objects are created in the following snippet?
```java
String s = new String("Developer");
```
**Answer**: **Two objects**.
1. One object is created in the normal heap memory (referenced by variable `s`).
2. One literal string object `"Developer"` is created inside the String Constant Pool (if not already present).

---

### Q5: What is String De-duplication in G1 Garbage Collector?
Introduced in Java 8, it is a GC feature that identifies duplicate strings in heap memory (having same char/byte array values) and points them to a single shared char/byte array. This reduces memory footprint by up to 10% without altering your Java code!

---

## 🛠️ Code Examples
- **[StringHandling.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/strings_memory/StringHandling.java)**: Demonstrates literal creation, `new String()`, SCP validation, `intern()`, and performance difference between String and StringBuilder.
