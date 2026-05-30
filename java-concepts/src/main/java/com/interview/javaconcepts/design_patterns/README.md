# Gang of Four (GoF) Design Patterns in Java

This module covers creational, structural, and behavioral design patterns in Java, focusing on Singleton, Builder, and Factory implementations.

---

## 🌟 Core Concepts

Design patterns are documented solutions to common software design problems. They represent best practices developed by experienced object-oriented practitioners (originally compiled by the Gang of Four).

### 1. Categories of Design Patterns
- **Creational Patterns**: Deal with object creation mechanisms, trying to create objects in a manner suitable to the situation.
  - *Examples*: Singleton, Builder, Factory Method, Abstract Factory, Prototype.
- **Structural Patterns**: Deal with class and object composition, helping different interfaces/classes work together.
  - *Examples*: Adapter, Decorator, Facade, Proxy, Composite.
- **Behavioral Patterns**: Deal with communication between objects, explaining how objects interact and distribute responsibility.
  - *Examples*: Observer, Strategy, Command, Iterator, State.

---

## ❓ Frequently Asked Interview Questions

### Q1: What is the best way to implement a Singleton pattern in Java?
There are three standard modern ways to implement Singletons, each with trade-offs:
1. **Double-Checked Locking (DCL)**: Lazily initialized, thread-safe, but complex and prone to errors if `volatile` is omitted.
2. **Bill Pugh Static Helper**: Lazily initialized, thread-safe, elegant, and performs fast because it relies on standard JVM classloader synchronization.
3. **Enum Singleton**: Joshua Bloch's recommended approach. Absolutely safe against reflection (Enums prevent constructor call) and serialization (JVM handles serialization of enums automatically).

---

### Q2: Why is the Builder Pattern preferred over constructor overloading?
Constructor overloading (telescoping constructors) occurs when a class has multiple constructors with varying parameters:
```java
public Computer(String ram) { ... }
public Computer(String ram, String hdd) { ... }
public Computer(String ram, String hdd, boolean hasGraphics) { ... }
```
- **The Problem**: Hard to read, prone to passing arguments in the wrong order if they share the same type, and forced to pass values for optional fields (like `null` or `false`).
- **The Builder Solution**: Provides a highly readable, fluent interface, creates objects step-by-step, enforces immutability (fields are `final`), and checks object state validity before final building.

---

### Q3: What is the difference between Factory Method and Abstract Factory?
- **Factory Method**: A creational pattern that uses methods to deal with the problem of creating objects without having to specify the exact class of the object that will be created. (Focuses on creating *one* product type).
- **Abstract Factory**: Provides an interface for creating families of related or dependent objects without specifying their concrete classes. (A "Factory of Factories").

---

## 🛠️ Code Examples
- **[SingletonPattern.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/design_patterns/SingletonPattern.java)**: Compares Double-Checked Locking, Bill Pugh Holder, and Enum Singleton.
- **[BuilderPattern.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/design_patterns/BuilderPattern.java)**: Classic static inner-class builder with parameter chaining.
- **[FactoryPattern.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/design_patterns/FactoryPattern.java)**: Detailed side-by-side comparison of Simple Factory and Abstract Factory implementations.
