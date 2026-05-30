# Tricky Java Program Puzzles

This module collects tricky code snippets, execution puzzles, compilation nuances, and edge cases frequently asked in Java interviews to catch developers off-guard.

---

## ❓ Classic Tricky Puzzles & Explanations

### Q1: The Overloading Resolution Puzzle
Consider the following overloaded methods:
```java
public static void method(Object o) { System.out.println("Object"); }
public static void method(String s) { System.out.println("String"); }

// Calling method(null);
```
**Output**: `"String"`
**Why?**
When resolving overloaded methods, the Java compiler always selects the **most specific method** matching the arguments. Since `String` is a subclass of `Object` (more specific), and `null` is a valid literal for any reference type, the compiler picks `method(String)`.

---

### Q2: Floating-Point Arithmetic Precision
What is the result of `System.out.println(0.1 + 0.2 == 0.3);`?
**Output**: `false`
**Why?**
Java represents floating-point numbers (`double` and `float`) using IEEE 754 standard binary formats. Simple decimal fractions like $0.1$ and $0.2$ cannot be represented exactly in binary, leading to minor precision loss. The actual addition yields approximately `0.30000000000000004`.
*To solve precision issues in financial calculations, always use `BigDecimal`!*

---

### Q3: Integer Caching Pool Pitfall
What is the output of the following comparisons?
```java
Integer a = 100;
Integer b = 100;
System.out.println(a == b); // Comparison 1

Integer c = 200;
Integer d = 200;
System.out.println(c == d); // Comparison 2
```
**Output**:
- Comparison 1: `true`
- Comparison 2: `false`
**Why?**
When boxing primitive `int` values into `Integer` wrappers, Java uses `Integer.valueOf()`. To save memory, Java caches Integer objects in the range **`-128` to `127`**.
- `100` falls within the cache range, so `a` and `b` point to the exact same cached object in memory (`==` returns true).
- `200` is outside the cache, so Java instantiates two separate `Integer` objects on the heap (`==` returns false).
*Always use `equals()` for object wrapper comparisons!*

---

### Q4: The `finally` and `System.exit()` Battle
What is printed here?
```java
try {
    System.out.println("Try");
    System.exit(0);
} finally {
    System.out.println("Finally");
}
```
**Output**: `"Try"`
**Why?**
`System.exit(0)` terminates the running JVM immediately. Once the JVM shuts down, no further instructions (including the `finally` block) can be executed.

---

## 🛠️ Code Examples
- **[JavaTricky.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/tricky_programs/JavaTricky.java)**: Contains executable programs testing method overloading with null arguments, Integer caching pools, float arithmetic quirks, and try-catch-finally edge cases.
