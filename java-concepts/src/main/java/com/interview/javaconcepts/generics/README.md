# Generics in Java

This module covers Java Generics, type safety, type erasure, bounded wildcards, the PECS rule, and related interview questions.

---

## 🌟 Core Concepts

Generics were introduced in Java 5 to provide compile-time type safety and eliminate the need for manual type casting when working with collections.

### 1. Type Erasure
Java implements generics using **Type Erasure**. To ensure backward compatibility with pre-Java 5 legacy code, the compiler removes all generic type parameters during compilation.
- Generic types (like `List<String>`) are replaced with their raw type (`List` or `Object` or their upper bound, like `Comparable`).
- The compiler inserts type casts automatically at call sites.
- **Bridge Methods** are generated to preserve polymorphism in inherited generic types.

> [!WARNING]
> Because of Type Erasure, you cannot check generic types at run-time: `if (list instanceof List<String>)` is illegal. Also, you cannot instantiate generic types directly: `new T()`.

---

## ❓ Frequently Asked Interview Questions

### Q1: Explain the PECS Rule in Java Generics?
> [!IMPORTANT]
> A crucial concept for designing highly reusable generic libraries and interfaces.

**PECS** stands for **Producer Extends, Consumer Super**. It governs the use of Bounded Wildcards:
1. **Producer Extends (`? extends T`)**: Use this when you are only reading (producing) elements out of a generic structure.
   ```java
   public void process(List<? extends Number> list) {
       for (Number n : list) { // Safe to read as Number
           System.out.println(n);
       }
       // list.add(10); // COMPILE ERROR: Cannot write/produce elements!
   }
   ```
2. **Consumer Super (`? super T`)**: Use this when you are only writing (consuming) elements into a generic structure.
   ```java
   public void addNumbers(List<? super Integer> list) {
       list.add(10); // Safe to write Integer
       // Object obj = list.get(0); // Only safe to read as raw Object
   }
   ```

---

### Q2: What are Bounded and Unbounded Wildcards?
- **Unbounded Wildcard (`<?>`)**: Represents any unknown type. Safe to read as `Object`. Useful when the method logic doesn't depend on the generic type (e.g., printing collections).
- **Upper-Bounded Wildcard (`<? extends T>`)**: Restricts the type to be a subclass of `T` (or `T` itself).
- **Lower-Bounded Wildcard (`<? super T>`)**: Restricts the type to be a superclass of `T` (or `T` itself).

---

### Q3: What is the difference between `List<Object>` and `List<?>`?
- **`List<Object>`**: A concrete list of Objects. You can add any object (Strings, Integers, etc.) into it. It is *not* covariant: you cannot assign a `List<String>` to a `List<Object>`.
- **`List<?>`**: A read-only list of an unknown type. You can assign any list (`List<String>`, `List<Integer>`) to it. However, you cannot add anything to it (except `null`) because the underlying concrete type is unknown.

---

## 🛠️ Code Examples
- **[GenericsBasics.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/generics/GenericsBasics.java)**: Demonstrates generic classes, generic methods, type erasure constraints, and a complete PECS rule explanation with working code.
