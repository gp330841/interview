# Functional Interfaces & Streams in Java

This module covers Java's functional programming API, lambdas, functional interfaces, lazy stream processing, complex reductions, grouping, and interview questions.

---

## 🌟 Core Concepts

Java 8 introduced functional programming concepts via lambdas and the Streams API, transforming collection processing from imperative loops to declarative pipelines.

### 1. Functional Interfaces
A functional interface contains **exactly one abstract method**. It can contain multiple default and static methods. Annotating with `@FunctionalInterface` is optional but recommended as it alerts the compiler to enforce this single abstract method rule.

Common functional interfaces in `java.util.function`:
- `Predicate<T>`: Takes type `T`, returns `boolean` (`test()`).
- `Function<T, R>`: Takes type `T`, returns type `R` (`apply()`).
- `Consumer<T>`: Takes type `T`, returns `void` (`accept()`).
- `Supplier<T>`: Takes nothing, returns type `T` (`get()`).

### 2. Stream Pipelines
A stream pipeline consists of:
1. **Source**: Collection, Array, or IO channel.
2. **Intermediate Operations**: (Lazy) Filter, map, flatMap, sorted, distinct. They transform a stream into another stream and are executed only when a terminal operation is called.
3. **Terminal Operations**: (Eager) Collect, reduce, count, forEach, findFirst. They execute the pipeline and close the stream.

---

## ❓ Frequently Asked Interview Questions

### Q1: What is the difference between `map` and `flatMap`?
> [!IMPORTANT]
> A vital stream concept focusing on transforming data shapes.

- **`map`**: Used for **1-to-1 mapping**. It transforms each element in a stream into a single transformed element of another type.
  - *Input*: `Stream<User>` ➡️ `map(User::getName)` ➡️ *Output*: `Stream<String>`.
- **`flatMap`**: Used for **1-to-many mapping** and flattening. It transforms each element into a stream of values, and then merges (flattens) all these generated streams into a single combined stream.
  - *Input*: `Stream<User>` where each user has a list of addresses. `flatMap(u -> u.getAddresses().stream())` ➡️ *Output*: `Stream<Address>`.

---

### Q2: What does "Lazy Evaluation" mean in Streams?
Intermediate operations are not executed when defined. They are lazily accumulated inside the pipeline. The stream pipeline starts processing elements only when a terminal operation is invoked.
This enables major optimizations. For example, if you filter, map, and take the `findFirst()`, the JVM processes only as many elements as needed to find the first matching one, avoiding processing the entire collection!

---

### Q3: What is the difference between `findFirst()` and `findAny()`?
- **`findFirst()`**: Returns the very first element in the encounter order of the stream. It is deterministic.
- **`findAny()`**: Returns any element from the stream. It is non-deterministic, designed for maximum speed in **parallel streams** where finding the exact first element has synchronization overhead.

---

### Q4: How does a parallel stream work and when should you avoid it?
Parallel streams split elements into sub-tasks using the standard **ForkJoinPool.commonPool()**, executing operations in parallel on multi-core processors.
- **When to avoid**:
  1. For small datasets (overhead of splitting and merging streams is greater than sequential processing time).
  2. If thread safety is breached (accessing shared state).
  3. If operations are blocking (IO, DB calls) since it will choke the common thread pool shared by the entire JVM application.

---

## 🛠️ Code Examples
- **[LambdaExpression.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/lambdas_streams/LambdaExpression.java)**: Classic lambdas, method references.
- **[StreamBasics.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/lambdas_streams/StreamBasics.java)**: Intermediate and terminal operations.
- **[FlatMapUsers.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/lambdas_streams/FlatMapUsers.java)**: Comprehensive map vs flatMap comparisons.
- **[ReduceExample.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/lambdas_streams/ReduceExample.java)**: Reducing streams down to aggregated answers.
- **[NestedGroupingByExample.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/lambdas_streams/NestedGroupingByExample.java)**: Multi-level advanced collection grouping.
