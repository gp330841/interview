# Exception Handling in Java

This module covers Java's robust exception handling model, runtime vs checked exceptions, resource management, and interview questions.

---

## 🌟 Core Concepts

Exception handling in Java is used to maintain normal execution flow in case of runtime disruptions.

```
                      Throwable
                     /         \
                Exception      Error
               /         \         \
       Checked Exceptions \     OutofMemoryError, StackOverflowError
                           \
                     RuntimeException (Unchecked)
                           \
                       NullPointerException, ArithmeticException
```

---

## ❓ Frequently Asked Interview Questions

### Q1: What is the difference between Checked and Unchecked Exceptions?
> [!IMPORTANT]
> A core interview question focusing on compile-time vs runtime safety.

- **Checked Exceptions**: Classes that inherit from `Exception` (excluding `RuntimeException`). The compiler forces you to handle them (using `try-catch` or `throws`). They represent recoverable scenarios outside your program control.
  - *Examples*: `IOException`, `SQLException`, `ClassNotFoundException`.
- **Unchecked Exceptions**: Classes that inherit from `RuntimeException`. The compiler does not verify them. They usually represent programming bugs or invalid api inputs.
  - *Examples*: `NullPointerException`, `ArrayIndexOutOfBoundsException`, `IllegalArgumentException`.

---

### Q2: How does `try-with-resources` work under the hood?
Introduced in Java 7, `try-with-resources` simplifies resource management (closing files, db connections).
- Any class used in this block must implement the `AutoCloseable` or `Closeable` interface.
- It automatically calls `close()` in a implicit `finally` block in the reverse order of initialization.
- **Suppressed Exceptions**: If both the `try` block and the `close()` method throw exceptions, the exception in the `try` block is thrown, and the `close()` exception is suppressed. These can be retrieved via `e.getSuppressed()`.

```java
try (BufferedReader br = new BufferedReader(new FileReader("test.txt"))) {
    System.out.println(br.readLine());
} // Auto-closes br here
```

---

### Q3: Under what conditions will the `finally` block NOT execute?
> [!WARNING]
> It is a common misconception that the `finally` block is guaranteed to execute 100% of the time.

The `finally` block will **not** execute in the following cases:
1. When calling `System.exit(int)` which shuts down the JVM.
2. In case of a system crash, power failure, or OS killing the JVM process.
3. If an infinite loop or a deadlock occurs in the preceding `try` or `catch` block.
4. If the thread executing the block is killed or terminated asynchronously.

---

### Q4: What is the impact of return statement in a `finally` block?
If a `finally` block contains a `return` statement, it will override any `return` or `throw` statements in the `try` or `catch` blocks.
```java
public static int test() {
    try {
        return 1;
    } finally {
        return 2; // Overrides the previous return! Returns 2.
    }
}
```

---

### Q5: What is Exception Propagation?
If an exception is thrown in a method and not caught locally, it is propagated up the call stack to the caller method.
- **Unchecked Exceptions** are automatically propagated down the call stack.
- **Checked Exceptions** are not propagated unless declared explicitly in the method signature using the `throws` keyword.

---

## 🛠️ Code Examples
- **[ExceptionHandlingBasics.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/exceptions/ExceptionHandlingBasics.java)**: Demonstrates checked vs unchecked, try-with-resources, suppressed exceptions, and execution sequence of try-catch-finally.
