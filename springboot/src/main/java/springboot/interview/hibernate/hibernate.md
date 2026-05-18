# Spring Boot - Hibernate & ORM

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Caching Catalog Data**: An e-commerce platform uses Hibernate's Second-Level Cache (integrated with Redis or Ehcache) to store frequently accessed but rarely changing product catalog data. This drastically reduces database load, as Hibernate fetches the entities from the fast memory cache rather than executing SQL queries for every page load.
2. **Audit Trails via Interceptors**: A banking application must log every change made to account balances. Using Hibernate Interceptors or Event Listeners (`PreUpdateEventListener`), the application automatically captures the "old state" and "new state" of the `Account` entity right before the SQL `UPDATE` is fired, and writes an immutable audit record to an `audit_log` table transparently.

---

## Beginner Level Questions

### Q1. What is Hibernate? [Easy]
**Answer:** Hibernate is a popular, open-source Object-Relational Mapping (ORM) framework for Java. It maps Java classes to database tables and Java data types to SQL data types, freeing developers from writing raw JDBC code.

### Q2. What is ORM? [Easy]
**Answer:** Object-Relational Mapping (ORM) is a programming technique used to convert data between incompatible type systems (object-oriented programming languages like Java and relational databases like MySQL).

### Q3. What is the difference between JPA and Hibernate? [Easy]
**Answer:** JPA (Java Persistence API) is a specification (a set of rules and interfaces). Hibernate is a concrete implementation of that specification. You use JPA annotations (like `@Entity`), and Hibernate does the actual work under the hood.

### Q4. What is a Hibernate `Session`? [Easy]
**Answer:** A `Session` is a single-threaded, short-lived object representing a conversation between the application and the persistent store. It wraps a JDBC connection and acts as a factory for `Transaction` objects. It holds the First-Level Cache.

### Q5. What is a `SessionFactory`? [Easy]
**Answer:** The `SessionFactory` is a thread-safe, immutable, heavyweight object that holds the database mapping metadata and connection configuration. It is created once during application startup and is responsible for creating `Session` instances.

### Q6. What are the three states of an object (Entity) in Hibernate? [Easy]
**Answer:**
1. **Transient**: A newly created object not associated with a Hibernate `Session` and with no representation in the database.
2. **Persistent**: An object associated with a current `Session` and guaranteed to have a corresponding row in the database.
3. **Detached**: An object that was once persistent but its `Session` has been closed. It represents database data, but Hibernate is no longer tracking changes to it.

### Q7. How do you move an object from Transient to Persistent state? [Easy]
**Answer:** By calling `session.save()`, `session.persist()`, or `session.saveOrUpdate()` on the transient object.

### Q8. How do you move an object from Detached to Persistent state? [Easy]
**Answer:** By calling `session.update()` or `session.merge()` with the detached object as an argument within a new active `Session`.

### Q9. What is the First-Level Cache in Hibernate? [Easy]
**Answer:** The First-Level Cache is the default cache associated with the Hibernate `Session` object. It is enabled by default and cannot be disabled. When you query an entity, Hibernate caches it in the Session. If you query the same entity again within the same session, Hibernate returns it from the cache instead of hitting the database.

### Q10. What is dirty checking in Hibernate? [Easy]
**Answer:** "Dirty checking" is a feature where Hibernate automatically detects if the state (fields) of a persistent object has been modified. When the session is flushed or a transaction is committed, Hibernate automatically generates an SQL `UPDATE` statement only for the objects that have changed (are "dirty").

---

## Intermediate Level Questions

### Q11. Explain the difference between `get()` and `load()` methods in Hibernate. [Medium]
**Answer:**
* `get(Class, id)`: Hits the database immediately. If the row doesn't exist, it returns `null`. Used when you aren't sure if the entity exists.
* `load(Class, id)`: Returns a proxy object without hitting the database immediately. The database is only hit when a non-identifier method is called on the proxy. If the row doesn't exist, it throws an `ObjectNotFoundException`. Used for performance optimization when you assume the entity exists and just need a reference (e.g., to set a foreign key).

### Q12. Explain the difference between `save()`, `persist()`, and `saveOrUpdate()`. [Medium]
**Answer:**
* `save()`: Persists the object and immediately returns its generated identifier (Primary Key). It is not strictly JPA-compliant.
* `persist()`: Makes a transient instance persistent. It does *not* guarantee that the identifier value will be assigned to the persistent instance immediately (it might happen at flush time). It returns `void` and is JPA-compliant.
* `saveOrUpdate()`: Checks if the object has an identifier. If no, it calls `save()`. If yes, it calls `update()`.

### Q13. Explain the difference between `update()` and `merge()`. [Medium]
**Answer:**
* `update()`: Reattaches a detached object to the session. If another object with the same ID is already in the session, it throws a `NonUniqueObjectException`.
* `merge()`: Copies the state of the given object onto the persistent object with the same identifier in the session. If none exists, it loads it. It returns the managed instance, leaving the original object detached. It is safer than `update()`.

### Q14. What is the Second-Level Cache in Hibernate? [Medium]
**Answer:** Unlike the First-Level Cache (scoped to the Session), the Second-Level Cache is scoped to the `SessionFactory` and is shared across all sessions. It is disabled by default. If enabled (usually with an provider like Ehcache, Hazelcast, or Redis), Hibernate checks the L1 cache, then the L2 cache, and only hits the database if the entity is not found in either.

### Q15. How do you map a composite primary key in Hibernate? [Medium]
**Answer:** You create a separate class for the primary key implementing `Serializable` and overriding `equals()` and `hashCode()`. Then, use either:
1. `@EmbeddedId` on the entity and `@Embeddable` on the key class.
2. `@IdClass` on the entity class and multiple `@Id` annotations on the fields.

### Q16. What is the Query Cache? [Medium]
**Answer:** The Query Cache stores the *results* of a query (specifically, the primary keys of the entities returned by the query). It relies on the Second-Level Cache to retrieve the actual entity data. You must explicitly enable it globally and per-query (`query.setCacheable(true)`).

### Q17. Explain HQL (Hibernate Query Language). [Medium]
**Answer:** HQL is an object-oriented query language, similar to SQL, but instead of operating on tables and columns, HQL works with persistent objects and their properties. HQL queries are translated by Hibernate into conventional SQL queries, making them database independent. (JPQL is a subset of HQL).

### Q18. What is the `Criteria` API? [Medium]
**Answer:** The Criteria API provides an object-oriented, programmatic, and strongly-typed way to construct queries dynamically at runtime. It eliminates syntax errors commonly found when writing raw HQL strings.

### Q19. How do you define a unidirectional One-to-Many relationship? [Medium]
**Answer:** By using the `@OneToMany` annotation on the collection field in the parent entity and `@JoinColumn` to specify the foreign key column in the child table. Without `@JoinColumn`, Hibernate creates an unnecessary junction/join table.

### Q20. What is mappedBy and why is it used in bidirectional relationships? [Medium]
**Answer:** In a bidirectional relationship, both entities have references to each other. `mappedBy` is placed on the "inverse" (non-owning) side of the relationship. It tells Hibernate: "Look at the other entity to find the foreign key configuration." This prevents Hibernate from trying to create two separate foreign keys or a join table. The side without `mappedBy` is the "owning" side responsible for updating the foreign key.

---

## Advanced Level Questions

### Q21. How do you implement a custom Hibernate Interceptor? [Hard]
**Answer:** You implement the `org.hibernate.Interceptor` interface (or extend `EmptyInterceptor`). This allows you to inspect and manipulate entities right before they are saved, updated, deleted, or loaded. You can register it at the `SessionFactory` level (global) or the `Session` level.

### Q22. Explain Hibernate's inheritance mapping strategies. Which one is best? [Hard]
**Answer:**
1. **Single Table (`InheritanceType.SINGLE_TABLE`)**: All classes in the hierarchy map to one massive table. Uses a discriminator column. Best for performance, but causes many NULL columns and prevents NOT NULL constraints on subclass fields.
2. **Joined Table (`InheritanceType.JOINED`)**: One table for the parent class, and separate tables for subclasses containing only their specific fields. Requires SQL JOINs. Good for normalization, bad for query performance on deep hierarchies.
3. **Table per Class (`InheritanceType.TABLE_PER_CLASS`)**: A separate table for every concrete class containing all parent and child fields. Does not support polymorphic queries efficiently (requires UNIONs).
*Best?* Single Table is usually the best default due to performance, unless strict database normalization is required.

### Q23. What is the purpose of `@DynamicUpdate` and `@DynamicInsert`? [Hard]
**Answer:** By default, Hibernate generates SQL `UPDATE` and `INSERT` statements that include *all* columns of the entity, even if only one field changed (it caches these SQL strings at startup).
* `@DynamicUpdate`: Forces Hibernate to generate an `UPDATE` statement containing *only* the modified columns.
* `@DynamicInsert`: Forces Hibernate to generate an `INSERT` statement containing *only* non-null columns.
They reduce database network traffic but add minor processing overhead in Hibernate to generate the dynamic SQL.

### Q24. How do you call a Stored Procedure using Hibernate? [Hard]
**Answer:** You can use the `@NamedStoredProcedureQuery` annotation on an entity to define the procedure name and its `IN`/`OUT` parameters. Then, use `EntityManager.createNamedStoredProcedureQuery()` to execute it. Alternatively, use standard JPA `StoredProcedureQuery` programmatically.

### Q25. What is the Hibernate `StatelessSession`? [Hard]
**Answer:** A `StatelessSession` is a command-oriented API that does *not* use a first-level cache, does not perform dirty checking, does not cascade operations, and bypasses Hibernate's event model/interceptors. It is strictly used for massive bulk data processing (batch inserts/updates) where the overhead of maintaining entity state in memory would cause OutOfMemory errors.

### Q26. Explain the Cartesian Product (Multiple Bag Fetch) Exception. [Hard]
**Answer:** If you try to eager-fetch multiple `List` (Bag) collections on the same entity simultaneously (e.g., `SELECT u FROM User u JOIN FETCH u.roles JOIN FETCH u.permissions`), Hibernate throws a `MultipleBagFetchException`. This happens because SQL generates a Cartesian product (Rows = Users × Roles × Permissions), resulting in a massive, unparseable result set.
*Fix*: Change the collections from `List` to `Set` (which removes duplicates), or execute multiple distinct queries.

### Q27. What is Bytecode Enhancement in Hibernate? [Hard]
**Answer:** Bytecode enhancement modifies the compiled `.class` files. It allows Hibernate to implement:
1. **Lazy fetching for basic attributes** (e.g., delaying the loading of a massive `byte[]` image column until it's accessed).
2. **In-line dirty checking** (instead of comparing the current state with a snapshot, the entity itself notifies Hibernate when a setter is called).

### Q28. How does Hibernate handle locking for concurrent updates? [Hard]
**Answer:** 
* **Optimistic Locking**: Uses a `@Version` field to prevent lost updates without database locks.
* **Pessimistic Locking**: Uses `LockModeType.PESSIMISTIC_WRITE` (translates to `SELECT ... FOR UPDATE` in SQL) to physically lock the database row, preventing any other transaction from reading or modifying it until the current transaction commits.

### Q29. What is a Hibernate Proxy and what problems can it cause? [Hard]
**Answer:** A proxy is a dynamically generated subclass (via ByteBuddy/CGLIB) that intercepts method calls to trigger lazy loading. 
*Problems*:
1. `LazyInitializationException` outside an active session.
2. `instanceof` checks might fail because the object is a subclass proxy, not the actual entity type.
3. Jackson serialization fails because the proxy contains internal Hibernate handler fields. (Fix: use `Hibernate5Module`).

### Q30. Explain the concept of "Flush Mode" in Hibernate. [Hard]
**Answer:** FlushMode determines *when* Hibernate synchronizes the in-memory state of the `Session` with the database.
* `AUTO` (Default): Flushes before a query is executed (to ensure the query sees the latest changes) and before transaction commit.
* `COMMIT`: Flushes only when the transaction is explicitly committed.
* `MANUAL`: The application must explicitly call `session.flush()`. Used for complex, long-running conversations spanning multiple requests.
