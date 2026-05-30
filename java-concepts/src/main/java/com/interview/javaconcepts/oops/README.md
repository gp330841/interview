# Object-Oriented Programming (OOP) in Java

This module covers the core Object-Oriented programming principles, abstract types, and related frequently asked interview questions in Java.

---

## 🌟 Core Concepts

Object-Oriented Programming (OOP) is a paradigm centered around **Objects** containing data (fields) and code (methods). Java is a class-based, object-oriented language.

### 1. The Four Pillars of OOP
- **Encapsulation**: Bundling state (fields) and behavior (methods) together, and restricting access to some of the object's components (using access modifiers). It ensures data integrity and hiding.
- **Abstraction**: Hiding complex implementation details and showing only the essential features of an object (implemented via `abstract` classes and `interface`s).
- **Inheritance**: A mechanism where one class acquires the properties and behaviors of a parent class (using `extends`), promoting code reusability (IS-A relationship).
- **Polymorphism**: The ability of a reference variable to change behavior based on the concrete implementation it holds.
  - *Compile-time (Static)*: Method Overloading.
  - *Run-time (Dynamic)*: Method Overriding.

---

## ❓ Frequently Asked Interview Questions

### Q1: What is the difference between an Abstract Class and an Interface in Java?
> [!NOTE]
> This is one of the most common core Java interview questions, especially since Java 8 and 9 introduced new interface capabilities.

| Feature | Abstract Class | Interface |
| :--- | :--- | :--- |
| **Multiple Inheritance** | A class can extend only one abstract class. | A class can implement multiple interfaces. |
| **State/Fields** | Can declare instance fields (state). | Can only declare `public static final` constants. |
| **Constructors** | Can have constructors (called by subclasses). | Cannot have constructors. |
| **Methods (Java 8+)** | Can have concrete, abstract, protected methods. | Can have `abstract`, `default`, and `static` methods. |
| **Private Methods (Java 9+)**| Yes. | Yes (private interface helper methods). |
| **Design Intention** | Represents a strong "IS-A" identity. | Represents a behavioral capability ("CAN-DO"). |

---

### Q2: What is "Static Binding" vs "Dynamic Binding"?
- **Static Binding (Compile-time)**: Occurs when the binding of overloaded methods or static/private/final methods is resolved by the compiler. It is fast because it is decided at compile-time.
- **Dynamic Binding (Run-time)**: Occurs when the binding of overridden virtual methods is resolved by the JVM at run-time using the actual object's type (through virtual method tables or VTABLEs).

```java
Animal animal = new Dog(); // Dynamic Binding: myDog.makeSound() binds at run-time
```

---

### Q3: What is "Method Hiding" in Java?
If a subclass defines a static method with the exact same signature as a static method in the superclass, the method in the subclass **hides** the one in the superclass. This is *not* polymorphism, but method hiding, determined at compile time.

> [!WARNING]
> You cannot override static methods in Java. Dynamic binding applies only to instance methods!

---

### Q4: Why does Java not support multiple inheritance of classes?
To avoid the **Diamond Problem**. If Class A has a method `foo()`, and Classes B and C inherit from A and override `foo()`, and Class D inherits from both B and C, calling `foo()` on an instance of D would be ambiguous because the compiler wouldn't know whether to invoke B's or C's version.
*Note: Java avoids this for classes but allows multiple interface implementation because interfaces do not hold instance state.*

---

### Q5: What is the difference between Composition and Inheritance?
- **Inheritance (IS-A)**: Tight coupling. The subclass depends on the implementation of the superclass. Breaks encapsulation if the superclass changes.
- **Composition (HAS-A)**: Loose coupling. An object holds references to other objects. Enables dynamic behavior switching at run-time and is highly recommended ("Favor composition over inheritance").

---

## 🛠️ Code Examples
- **[OOPsConcepts.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/oops/OOPsConcepts.java)**: Demonstrates the practical implementations of Encapsulation, Abstraction, Inheritance, and Polymorphism.
- **[StaticHiding.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/oops/StaticHiding.java)**: Clarifies static method hiding vs dynamic method overriding.
