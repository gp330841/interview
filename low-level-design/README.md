# Low Level Design (LLD) Interview Guide

Welcome to the Low Level Design (LLD) repository. This section covers OOP principles, creational/structural/behavioral design patterns, and standard LLD problems commonly asked in technical interviews.

Each topic contains clear object-oriented design code, class diagrams, state/sequence flowcharts, and typical interview Q&A.

---

## LLD Concept Dashboard

| ID | Concept | Category | Difficulty | Key Focus / Interview Questions |
|----|---------|----------|------------|---------------------------------|
| 01 | [SOLID Principles](concepts/01-solid-principles.md) | OOP Principles | Medium | Single Responsibility, Interface Segregation, Dependency Inversion |
| 02 | [Singleton Pattern](concepts/02-singleton-pattern.md) | Creational Pattern | Easy | Double-checked locking, Bill Pugh Singleton, Reflection/Serialization safety |
| 03 | [Factory & Abstract Factory](concepts/03-factory-abstract-factory.md) | Creational Pattern | Medium | Object creation decoupling, family of products |
| 04 | [Builder Pattern](concepts/04-builder-pattern.md) | Creational Pattern | Easy | Complex object construction, immutability, validation during build |
| 05 | [Prototype Pattern](concepts/05-prototype-pattern.md) | Creational Pattern | Easy | Cloning objects, Shallow vs Deep Copy, Performance optimization |
| 06 | [Adapter Pattern](concepts/06-adapter-pattern.md) | Structural Pattern | Easy | Translating incompatible interfaces, wrapper pattern |
| 07 | [Decorator Pattern](concepts/07-decorator-pattern.md) | Structural Pattern | Medium | Open-Closed principle, dynamic behaviors, Java I/O streams |
| 08 | [Observer / Pub-Sub Pattern](concepts/08-observer-pubsub-pattern.md) | Behavioral Pattern | Medium | One-to-many dependency, event-driven architecture, decoupling publisher/subscriber |
| 09 | [Strategy Pattern](concepts/09-strategy-pattern.md) | Behavioral Pattern | Easy | Family of algorithms, dynamic algorithm swapping at runtime |
| 10 | [Command Pattern](concepts/10-command-pattern.md) | Behavioral Pattern | Medium | Encapsulating requests, undo/redo operations, Smart Remote systems |
| 11 | [Chain of Responsibility](concepts/11-chain-of-responsibility.md) | Behavioral Pattern | Medium | Processing pipeline, decoupling request sender and handlers |
| 12 | [Proxy Pattern](concepts/12-proxy-pattern.md) | Structural Pattern | Medium | Lazy initialization, security/access control, caching proxy |
| 13 | [Facade Pattern](concepts/13-facade-pattern.md) | Structural Pattern | Easy | Simplifying complex API gateways, unified subsystem interfaces |
| 14 | [State Pattern](concepts/14-state-pattern.md) | Behavioral Pattern | Medium | State transitions, avoiding nested if-else checks, OCP compliance |
| 15 | [Design Parking Lot](concepts/15-parking-lot-design.md) | LLD System | Medium | Spot allocation strategies, concurrency in ticketers, vehicle types |
| 16 | [Design BookMyShow](concepts/16-bookmyshow-design.md) | LLD System | Hard | Concurrency & seat booking, payment states, caching available seats |
| 17 | [Design Splitwise](concepts/17-splitwise-design.md) | LLD System | Hard | Expense dividing algorithms, debt simplification (min cash flow) |
| 18 | [Design Snake & Ladder](concepts/18-snake-ladder-design.md) | LLD System | Easy | Board entity relationships, gameplay loop, modular board rules |
| 19 | [Design Chess Game](concepts/19-chess-design.md) | LLD System | Hard | Piece move validations (Strategy pattern), undo moves, board state |
| 20 | [Design Elevator System](concepts/20-elevator-system-design.md) | LLD System | Medium | SCAN algorithm, dispatch optimization, state handling (Idle, Up, Down) |
| 21 | [Design Hotel Management](concepts/21-hotel-management-design.md) | LLD System | Medium | Room booking states, housekeeping dispatch, invoicing & payment |
| 22 | [Design Library Management](concepts/22-library-management-design.md) | LLD System | Easy | Catalog search (title, author), fine calculation, reservation states |
| 23 | [Design ATM](concepts/23-atm-design.md) | LLD System | Hard | Cash dispenser (Chain of Responsibility), card validation, transaction rollback |
| 24 | [Design Cricbuzz/Cricinfo](concepts/24-cricinfo-design.md) | LLD System | Medium | Live updates push/pull, statistics compilation, match state transitions |
| 25 | [Design In-Memory File System](concepts/25-in-memory-filesystem-design.md) | LLD System | Medium | Composite pattern for Directory/File, path parsing, traversal |
| 26 | [Design E-Commerce LLD](concepts/26-ecommerce-lld.md) | LLD System | Medium | Order state machine, shopping cart lifecycle, payment integration |
| 27 | [Design Vending Machine](concepts/27-vending-machine-design.md) | LLD System | Medium | Coin/Cash validation, item dispensing states, refunds |
| 28 | [Design Meeting Scheduler](concepts/28-meeting-scheduler-design.md) | LLD System | Hard | Room conflict detection, calendar interval matching, notifications |
| 29 | [Design Logger & Rate Limiter](concepts/29-logger-rate-limiter.md) | LLD System | Medium | Sliding window log in memory, log levels, output routing |
| 30 | [Design Jira / Task Planner](concepts/30-jira-task-planner.md) | LLD System | Medium | Task hierarchies, sprint board movements, notifications |
| 31 | [API Gateway Router](concepts/31-api-gateway-lld.md) | LLD System | Medium | Chain of Responsibility, Interceptor Filters, context mapping |
| 32 | [Design Instagram Stories](concepts/32-instagram-story-lld.md) | LLD System | Hard | 24-hour expiration TTL, ReentrantReadWriteLock, unique views tracking |

---

## LLD Core Concepts & Design Principles

To excel in Low-Level Design interviews, you must master the fundamental building blocks of Object-Oriented Programming, SOLID principles, class relationships, and concurrent systems design.

### 1. The Four Pillars of OOP (JVM Context)
* **Encapsulation:** Restricting direct access to an object's state and keeping fields `private`, exposing access only through `public` getter/setter methods. This enforces data validation, invariance, and class integrity.
* **Abstraction:** Hiding complex implementation details and showing only the essential interface to the user. Achieved in Java using `interface` (contracts) and `abstract class` (partial templates).
* **Inheritance:** Establishing an "is-a" relationship to share behavior. *Caution:* Overuse of inheritance leads to rigid, tightly coupled class hierarchies (Fragile Base Class problem). Prefer **Composition over Inheritance**.
* **Polymorphism:** The ability of an object to take multiple forms at runtime.
  * *Ad-hoc Polymorphism:* Method Overloading (compile-time/static binding resolved by compiler based on method signature).
  * *Subtyping Polymorphism:* Method Overriding (runtime/dynamic binding resolved by the JVM's virtual method table (V-Table)).
  * *Parametric Polymorphism:* Generics (compile-time type safety via Type Erasure, enabling code reusability across different types).

---

### 2. SOLID Design Principles
SOLID principles guide you in writing modular, readable, and maintainable software.

* **Single Responsibility Principle (SRP):** A class should have one, and only one, reason to change. Each class should focus on a single cohesive responsibility.
* **Open-Closed Principle (OCP):** Software entities (classes, modules, functions) should be **open for extension** but **closed for modification**. You should be able to add new features by adding new code, not editing existing code (typically achieved using interfaces and polymorphism).
* **Liskov Substitution Principle (LSP):** Subtypes must be substitutable for their base types without altering the correctness of the program. Derived classes must honor the contracts/invariants defined in the base class.
* **Interface Segregation Principle (ISP):** Clients should not be forced to depend on methods they do not use. Prefer multiple small, client-specific interfaces over a single large, "fat" interface.
* **Dependency Inversion Principle (DIP):** Depend on abstractions, not concretions. High-level business logic modules should not import or depend on low-level implementation details; both must depend on shared interfaces.

---

### 3. UML Class Diagram Relationships
Understanding how objects associate is crucial for system design modeling.

```
Association (Uses)   : [Driver] ───> [Car]
Aggregation (Has-a)  : [Department] ◇───> [Professor]  (Independent lifecycles)
Composition (Part-of): [House] ◆───> [Room]            (Tightly coupled lifecycles)
Realization (Contract): [PaymentService] - - - ▷ [CreditCardPayment]
```

* **Association ("Uses-a"):** A loose relationship where one object uses another. The lifecycle of the two objects is completely independent.
* **Aggregation ("Has-a"):** A specialized association representing a whole-part relationship where parts can exist independently of the whole (weak ownership). Represented by an **empty diamond** on the parent side.
* **Composition ("Part-of"):** A strong whole-part relationship where the part cannot exist without the whole (dependent lifecycles). If the parent is deleted, all children are deleted. Represented by a **filled diamond** on the parent side.
* **Generalization/Realization:** Represents inheritance ("extends") or implementation ("implements") relationships.

---

### 4. Concurrency & Thread-Safety in LLD Systems
LLD systems (like Booking systems, Vending Machines, or Parking Lots) must be designed to handle concurrent execution safely.

* **Critical Sections & Mutual Exclusion:** Protect shared mutable variables (e.g., available spots count, seat booking state) using locks.
  * `synchronized` blocks: Lock on the intrinsic monitor of a Java object.
  * `ReentrantLock`: Offers advanced lock features like try-lock, fairness policies, and lock timeouts.
* **Lock-Free Concurrency (CAS):** Use `java.util.concurrent.atomic` classes (e.g., `AtomicInteger`, `AtomicReference`) to scale counters or state updates without thread blocking.
* **Thread-Safe Collections:** Avoid `Collections.synchronizedMap` or `Hashtable` in high-throughput paths due to single-lock bottlenecks. Prefer `ConcurrentHashMap` which uses lock-striping (segment-based locking) to scale read/write operations.

---

## How to Study LLD

1. **UML Diagrams**: Make sure you can draw class relationships (Composition, Aggregation, Inheritance, Association).
2. **Design Patterns**: Do not just memorize names; understand *when* to apply them to keep systems OCP (Open-Closed Principle) compliant.
3. **Concurrency**: Understand how multiple threads will interact with your system objects (e.g., BookMyShow seat allocation).
