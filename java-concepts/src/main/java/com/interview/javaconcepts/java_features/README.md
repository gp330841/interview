# Evolution of Modern Java Features (Java 8 - 21+)

This module chronicles the major features introduced across Java releases, from foundational Java 8 up to standard Java 21 LTS and future Java 25 concepts.

---

## 🚀 Key Feature Milestones

### 1. Java 8 (The Paradigm Shift)
- **Lambda Expressions**: Introduced functional programming paradigms to Java.
- **Streams API**: Enables declarative functional data-processing pipelines.
- **Optional Class**: Encourages safe null-avoidance API designs.
- **Default/Static Interface Methods**: Allowed interface enhancement without breaking backwards-compatibility.
- **New Date & Time API (`java.time`)**: Joda-Time inspired immutable, thread-safe time modeling.

### 2. Java 11 LTS (Developer Ergonomics)
- **Local-Variable Type Inference (`var`)**: Allows omission of explicit variable types in local declarations.
- **HttpClient API**: Native, modern HTTP/2 support replacing `HttpURLConnection`.
- **Run Single-File Source Programs**: Execute a `.java` file directly using `java HelloWorld.java` without explicit compilation!

### 3. Java 17 LTS (Modern Modeling primitives)
- **Sealed Classes and Interfaces**: Restricts which subclasses or interfaces can extend or implement them.
- **Records**: Concise immutable data carriers (`record Point(int x, int y) {}`).
- **Pattern Matching for `instanceof`**: Eliminates redundant, boilerplate type-casting:
  ```java
  if (obj instanceof String s) { System.out.println(s.length()); }
  ```

### 4. Java 21 LTS (Concurrency & Sequence optimization)
- **Virtual Threads**: Light-weight, high-performance concurrency.
- **Sequenced Collections**: Unifies interfaces representing predefined order elements (`SequencedCollection`, `SequencedMap`).
- **Record Patterns**: Allows direct deconstruction of records inside pattern matching (`switch`).

---

## ❓ Frequently Asked Interview Questions

### Q1: What is a Sealed Class and why is it useful?
A sealed class or interface restricts which other classes or interfaces may extend or implement it.
- **Syntax**: `public sealed class Animal permits Dog, Cat {}`
- **Use Cases**: Domain modeling where the set of subclasses is finite and known (e.g., status types: Success, Failure, Pending).
- **Advantages**: Promotes **exhaustive analysis** in pattern matching expressions without needing a fallback default branch.

---

### Q2: What is a Java `Record` and how is it different from a standard class?
A `record` is a special final class designed to act as an immutable data carrier.
- **Boilerplate reduction**: The compiler automatically generates:
  - `private final` fields for all record components.
  - Public getter accessor methods matching the variable names (e.g., `x()` instead of `getX()`).
  - Canonical constructor.
  - Consistent `equals()`, `hashCode()`, and `toString()` implementations based on components.
- **Rules**: Cannot extend other classes, cannot have instance fields (other than defined components), and fields are strictly final.

---

### Q3: What is the benefit of `var` and when should it be avoided?
- **Benefits**: Reduces code clutter and improves readability by inferring types at compile-time.
- **Best Practices**: Use when the type is obvious from the right-hand side assignment:
  ```java
  var list = new ArrayList<String>(); // Good
  var data = service.fetch(); // Bad (unclear return type)
  ```

---

## 🛠️ Code Examples
- **[Java8Features.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/java_features/Java8Features.java)**: Comprehensive Java 8 features demo.
- **[Java11Features.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/java_features/Java11Features.java)**: HttpClient and local variable `var` demo.
- **[Java17Features.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/java_features/Java17Features.java)**: Sealed classes, records, and pattern matching.
- **[Java21Features.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/java_features/Java21Features.java)**: Sequenced collections, record patterns, and virtual threads.
- **[Java25Features.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/java_features/Java25Features.java)**: Preview features like unnamed variables.
