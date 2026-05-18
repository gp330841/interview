# Spring Boot - Transactions

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Financial Fund Transfer**: A banking application transfers $100 from Account A to Account B. This involves two SQL operations: deducting from A and adding to B. These operations *must* be wrapped in a single `@Transactional` method. If adding to B fails (e.g., database constraint violation or network failure), deducting from A must automatically roll back so money isn't lost.
2. **Audit Logging in a separate transaction**: A system processes a complex order. If the order processing fails and rolls back, we still want to save a record in the `order_audit_log` table explaining why it failed. Using `@Transactional(propagation = Propagation.REQUIRES_NEW)` on the audit logging method ensures the audit log commits successfully even if the parent transaction rolls back.

---

## Beginner Level Questions

### Q1. What is a Database Transaction? [Easy]
**Answer:** A transaction is a logical unit of work that contains one or more SQL operations. It must adhere to the ACID properties (Atomicity, Consistency, Isolation, Durability) ensuring that either all operations succeed entirely or none of them are applied to the database.

### Q2. How do you implement transaction management in Spring Boot? [Easy]
**Answer:** The easiest and most common way is Declarative Transaction Management using the `@Transactional` annotation on your service layer methods or classes.

### Q3. What does the `@Transactional` annotation actually do? [Easy]
**Answer:** When Spring sees this annotation, it creates an AOP proxy around the class. When the method is called, the proxy intercepts the call, opens a database transaction, and executes the method. If the method completes successfully, the proxy commits the transaction. If an exception is thrown, the proxy rolls back the transaction.

### Q4. Which exceptions trigger an automatic rollback by default? [Easy]
**Answer:** By default, Spring only rolls back transactions for **Unchecked Exceptions** (subclasses of `RuntimeException` and `Error`). It does *not* roll back for **Checked Exceptions** (exceptions that extend `Exception` but not `RuntimeException`, like `IOException` or `SQLException`).

### Q5. How do you force a rollback for Checked Exceptions? [Easy]
**Answer:** By using the `rollbackFor` attribute in the annotation.
**Code Example:**
```java
@Transactional(rollbackFor = Exception.class)
public void processFile() throws IOException { ... }
```

### Q6. Can you put `@Transactional` on an interface? [Easy]
**Answer:** Yes, but it is highly discouraged by the Spring team. Annotations on interfaces are not inherited in certain proxying scenarios (like CGLIB proxies). It is always best practice to put `@Transactional` on the concrete class or method implementations.

### Q7. Should `@Transactional` be placed in the Controller, Service, or Repository layer? [Easy]
**Answer:** It should almost always be placed in the **Service layer**. Controllers handle web routing, and Repositories handle single DB interactions. The Service layer contains the business logic that often combines multiple Repository calls into a single logical transaction unit.

### Q8. What is programmatic transaction management? [Easy]
**Answer:** It's an alternative to `@Transactional`. You write manual code to manage the transaction using the `TransactionTemplate` or directly using the `PlatformTransactionManager`. It's more complex but allows fine-grained control over exactly when a transaction starts and commits within a method.

### Q9. What does the `readOnly = true` attribute do? [Easy]
**Answer:** `@Transactional(readOnly = true)` hints to the database and Hibernate that the transaction will only perform `SELECT` queries. Hibernate can optimize this by bypassing dirty-checking and avoiding taking out certain database locks, which improves performance.

### Q10. What happens if you catch an exception inside a `@Transactional` method? [Easy]
**Answer:** If you wrap the failing database call in a `try-catch` block and do *not* rethrow the exception, the AOP proxy won't know an error occurred. The proxy will assume the method finished successfully and will attempt to commit the transaction, preventing the rollback.

---

## Intermediate Level Questions

### Q11. Explain Transaction Propagation in Spring. [Medium]
**Answer:** Propagation defines how transactions relate to each other when one `@Transactional` method calls another `@Transactional` method. It defines whether the second method should join the existing transaction, start a new one, or throw an exception.

### Q12. Explain the `REQUIRED` propagation level. [Medium]
**Answer:** It is the **default** propagation level. If an active transaction exists, the method joins it. If no transaction exists, it creates a new one.

### Q13. Explain the `REQUIRES_NEW` propagation level. [Medium]
**Answer:** It always suspends the current transaction (if one exists) and creates a brand-new, independent transaction. If the new transaction rolls back, it does *not* affect the outer transaction. If the outer transaction rolls back, it does *not* affect the new transaction.

### Q14. Explain the `NESTED` propagation level. [Medium]
**Answer:** It executes within a nested transaction if a current transaction exists. It does this by creating a "savepoint" in the database. If the nested transaction rolls back, it only rolls back to the savepoint, leaving the outer transaction intact. If no transaction exists, it behaves like `REQUIRED`.

### Q15. Explain `SUPPORTS`, `NOT_SUPPORTED`, `MANDATORY`, and `NEVER`. [Medium]
**Answer:**
* `SUPPORTS`: Joins the transaction if one exists. If not, runs non-transactionally.
* `NOT_SUPPORTED`: Suspends the current transaction and runs non-transactionally.
* `MANDATORY`: Throws an exception if there is no active transaction.
* `NEVER`: Throws an exception if there *is* an active transaction.

### Q16. What is the "Self-Invocation" problem with `@Transactional`? [Medium]
**Answer:** Because `@Transactional` relies on AOP proxies, if `MethodA()` calls `MethodB()` within the *same* class, the call bypasses the proxy. Even if `MethodB()` has `@Transactional(propagation = REQUIRES_NEW)`, it will simply execute in `MethodA`'s transaction (or no transaction at all if A doesn't have one).

### Q17. How do you solve the Self-Invocation problem? [Medium]
**Answer:** 
1. The cleanest way is to move `MethodB()` into a different service class and inject that service into the first one.
2. Alternatively, inject the application context, lookup the current bean, and call the method on the looked-up proxy.
3. Use AspectJ compile-time weaving instead of Spring AOP proxies.

### Q18. What are Transaction Isolation Levels? [Medium]
**Answer:** Isolation levels define how changes made by one transaction become visible to other concurrent transactions, preventing anomalies like Dirty Reads, Non-Repeatable Reads, and Phantom Reads.

### Q19. What are the standard Isolation Levels available in Spring? [Medium]
**Answer:**
1. `DEFAULT`: Uses the underlying datastore's default isolation level (usually `READ_COMMITTED` for PostgreSQL/SQL Server, `REPEATABLE_READ` for MySQL).
2. `READ_UNCOMMITTED`: Lowest isolation, allows Dirty Reads.
3. `READ_COMMITTED`: Prevents Dirty Reads.
4. `REPEATABLE_READ`: Prevents Dirty & Non-Repeatable Reads.
5. `SERIALIZABLE`: Highest isolation, prevents all anomalies (Phantom reads), but kills concurrency performance.

### Q20. Can you set a timeout on a Spring Transaction? [Medium]
**Answer:** Yes, using the `timeout` attribute: `@Transactional(timeout = 5)`. If the transaction takes longer than 5 seconds to complete, an exception is thrown, and the transaction is automatically rolled back.

---

## Advanced Level Questions

### Q21. Explain the `PlatformTransactionManager` interface. [Hard]
**Answer:** It is the core interface in Spring's transaction infrastructure. It abstracts away the specific database connection technology. It provides three methods: `getTransaction()`, `commit()`, and `rollback()`. Depending on your stack, Spring Boot auto-configures a specific implementation (e.g., `JpaTransactionManager` for JPA/Hibernate, `DataSourceTransactionManager` for raw JDBC/MyBatis, `JtaTransactionManager` for distributed transactions).

### Q22. What happens if a `REQUIRES_NEW` transaction commits, but the outer transaction later rolls back? [Hard]
**Answer:** Because `REQUIRES_NEW` creates a completely independent transaction with its own database connection, its commit is final. The rollback of the outer transaction will *not* undo the changes made by the inner `REQUIRES_NEW` transaction.

### Q23. What happens if a `REQUIRED` transaction (Method B) throws an exception, but the outer transaction (Method A) catches it? [Hard]
**Answer:** Even though Method A caught the exception, Method B (the inner call) already marked the shared transaction proxy as `rollback-only`. When Method A tries to finish and commit, Spring will throw an `UnexpectedRollbackException` because the transaction was marked for rollback by a participating method, and the commit is impossible.

### Q24. How does Spring manage the Database Connection during a transaction? [Hard]
**Answer:** Spring uses a `ThreadLocal` variable via `TransactionSynchronizationManager` to bind the database `Connection` (or Hibernate `Session`) to the currently executing thread. This ensures that all DAOs/Repositories called within that thread use the exact same database connection, allowing them to participate in the same transaction.

### Q25. Can you use `@Transactional` with private or protected methods? [Hard]
**Answer:** No. When using the default Spring AOP (proxy-based), the proxy can only intercept `public` methods. If you put `@Transactional` on a private/protected/package-private method, Spring simply ignores it without throwing an error, which can lead to silent transaction failures. (AspectJ weaving is required to intercept non-public methods).

### Q26. What is a Distributed Transaction, and what is JTA? [Hard]
**Answer:** A distributed transaction is a transaction that spans multiple independent resources (e.g., saving data to a MySQL database AND sending a message to a RabbitMQ queue). Standard `JpaTransactionManager` cannot handle this (it only manages one database). You must use JTA (Java Transaction API) and a transaction coordinator like Atomikos or Bitronix to implement the Two-Phase Commit (2PC) protocol to ensure Atomicity across multiple systems.

### Q27. What is the Two-Phase Commit (2PC) protocol? [Hard]
**Answer:** It's an algorithm used in distributed systems to guarantee Atomicity.
* **Phase 1 (Prepare):** The coordinator asks all participating resources (databases, message brokers) if they are ready to commit. They lock the necessary resources and reply "Yes" or "No".
* **Phase 2 (Commit/Rollback):** If *all* resources replied "Yes", the coordinator tells them all to commit. If *any* resource replied "No" (or timed out), the coordinator tells them all to rollback.

### Q28. What is the Transactional Outbox Pattern? [Hard]
**Answer:** It's an alternative to complex JTA distributed transactions in microservices. Instead of trying to update a database and publish a Kafka message in one 2PC transaction, you write the business data AND the "message to be sent" into the *same database* within a standard local transaction (the "outbox" table). A separate background process (like Debezium) reads the outbox table and guarantees delivery to Kafka.

### Q29. How do `TransactionSynchronization` callbacks work? [Hard]
**Answer:** You can register callbacks using `TransactionSynchronizationManager.registerSynchronization()`. This allows you to execute custom Java code immediately `beforeCommit`, `afterCommit`, or `afterRollback`. For example, you might want to send an email *only after* the database transaction has successfully committed, rather than before, so the user doesn't get an email if the DB rolls back.

### Q30. What is `@TransactionalEventListener`? [Hard]
**Answer:** Introduced in Spring 4.2, it binds event listeners to a transaction phase. Instead of manually registering a `TransactionSynchronization`, you publish an Application Event, and your listener method annotated with `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` will only execute if the publisher's transaction commits successfully.
