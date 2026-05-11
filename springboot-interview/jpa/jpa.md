# Spring Boot - Spring Data JPA

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Dynamic Dashboard Reporting**: An admin dashboard needs to display sales data filtered by dynamic criteria (date range, specific products, region) chosen by the user on the fly. Instead of writing hundreds of JPQL combinations, you use Spring Data JPA's `JpaSpecificationExecutor` (Criteria API) to programmatically build the WHERE clause based only on the criteria the user provided.
2. **High-Performance Batch Processing**: A nightly cron job needs to update the status of millions of expired subscriptions. Instead of fetching each entity and calling `.save()` (which would crash the app due to memory limits and N+1 issues), you use a Spring Data JPA `@Modifying` query to execute a single, massive SQL `UPDATE` statement directly on the database.

---

## Beginner Level Questions

### Q1. What is Spring Data JPA? [Easy]
**Answer:** It is a part of the larger Spring Data family that provides enhanced support for JPA-based data access layers. It aims to significantly reduce the amount of boilerplate code required to implement data access (DAOs) by automatically generating repository implementations at runtime.

### Q2. What is the difference between JPA and Spring Data JPA? [Easy]
**Answer:** JPA is merely a Java specification (a set of interfaces) for Object-Relational Mapping (ORM). Spring Data JPA is an abstraction layer built *on top* of JPA that implements the Repository pattern, freeing you from writing explicit `EntityManager` code.

### Q3. What is the `JpaRepository` interface? [Easy]
**Answer:** It is a Spring Data interface that you extend to create your custom repository. It inherits from `PagingAndSortingRepository` and `CrudRepository`. It provides full CRUD operations, pagination, sorting, and JPA-specific methods like `flush()` and `saveAndFlush()`.

### Q4. How do you define a custom query method in a Spring Data Repository without writing SQL? [Easy]
**Answer:** By using **Query Method Derivation**. You simply declare a method in your repository interface following Spring Data's naming conventions. Spring parses the method name and automatically generates the JPQL query.
**Code Example:**
```java
List<User> findByEmailAndIsActiveTrue(String email);
// Generates: SELECT * FROM users WHERE email = ? AND is_active = true
```

### Q5. What is the `@Entity` annotation used for? [Easy]
**Answer:** It marks a Java class as a JPA entity, meaning instances of this class will be mapped to rows in a database table.

### Q6. What is the `@Id` annotation used for? [Easy]
**Answer:** It specifies the primary key field of an entity.

### Q7. How do you handle auto-incrementing primary keys in JPA? [Easy]
**Answer:** By using the `@GeneratedValue` annotation alongside `@Id`. The most common strategy for modern databases (like MySQL/PostgreSQL) is `GenerationType.IDENTITY`, which relies on the database's auto-increment feature.

### Q8. What happens if you call `save()` on an entity that already exists? [Easy]
**Answer:** `save()` handles both insert and update operations. If the entity passed to `save()` has a null ID, or if the ID does not exist in the database, it performs an `INSERT`. If the ID exists, it performs an `UPDATE` (a merge operation in Hibernate terms).

### Q9. What is JPQL (Java Persistence Query Language)? [Easy]
**Answer:** JPQL is a query language similar to SQL, but instead of operating on database tables and columns, it operates on JPA Entity classes and their fields. It is database-agnostic.

### Q10. How do you write a custom JPQL query in Spring Data? [Easy]
**Answer:** By using the `@Query` annotation above a method in your repository interface.
**Code Example:**
```java
@Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
List<User> findActiveUsers();
```

---

## Intermediate Level Questions

### Q11. What is the difference between `CrudRepository` and `JpaRepository`? [Medium]
**Answer:** 
* `CrudRepository` provides basic CRUD functionality and returns `Iterable`.
* `JpaRepository` extends `CrudRepository` (via `PagingAndSortingRepository`), returns `List` instead of `Iterable`, and provides JPA-specific methods like batch deletion (`deleteInBatch`) and context flushing (`saveAndFlush`). `JpaRepository` is generally preferred in Spring Boot.

### Q12. How do you execute a Native SQL query in Spring Data JPA? [Medium]
**Answer:** You use the `@Query` annotation and set the `nativeQuery` flag to `true`.
**Code Example:**
```java
@Query(value = "SELECT * FROM users_table WHERE email_address = ?1", nativeQuery = true)
User findByEmailNative(String email);
```

### Q13. How do you implement Pagination and Sorting? [Medium]
**Answer:** You add a `Pageable` parameter to your repository method. Spring Boot handles the `LIMIT` and `OFFSET` automatically.
**Code Example:**
```java
// Repository
Page<User> findByLastName(String lastName, Pageable pageable);

// Service
Pageable pageable = PageRequest.of(0, 10, Sort.by("firstName").ascending());
Page<User> page = userRepository.findByLastName("Smith", pageable);
```

### Q14. What is the `@Modifying` annotation? [Medium]
**Answer:** It is used alongside `@Query` to indicate that the query modifies the database state (i.e., it is an `UPDATE` or `DELETE` query, not a `SELECT`). Without it, Spring expects the query to return a result set and will throw an exception.
**Code Example:**
```java
@Modifying
@Query("UPDATE User u SET u.status = 'INACTIVE' WHERE u.lastLoginDate < :date")
int deactivateOldUsers(@Param("date") LocalDate date);
```

### Q15. Explain `@Transient`. [Medium]
**Answer:** By default, JPA maps every field in an Entity class to a database column. The `@Transient` annotation tells JPA to ignore the field. It will not be saved to or retrieved from the database. It's useful for calculated fields or temporary state data.

### Q16. What is the difference between `FetchType.LAZY` and `FetchType.EAGER`? [Medium]
**Answer:** These define how related entities (e.g., `@OneToMany`) are loaded from the database.
* **EAGER**: The related entities are loaded immediately along with the parent entity using a SQL JOIN.
* **LAZY**: The related entities are *not* loaded immediately. They are replaced by a Proxy object. The actual SQL query to fetch them is only executed when you first access the collection (e.g., calling `user.getOrders().size()`).

### Q17. What is the N+1 Query Problem? [Medium]
**Answer:** It's a severe performance issue. It occurs when you execute 1 query to fetch a list of N parent entities, and then, while looping through those parents, you access a lazy-loaded child collection. This triggers N additional queries (one for each parent) to fetch the children, resulting in N+1 total queries.

### Q18. How do you fix the N+1 Query Problem in Spring Data JPA? [Medium]
**Answer:** The most common solution is to use a `JOIN FETCH` in your JPQL query, or use Spring Data's `@EntityGraph`. Both force JPA to fetch the related entities in the initial SQL query, completely eliminating the N subsequent queries.
**Code Example:**
```java
@Query("SELECT u FROM User u JOIN FETCH u.orders")
List<User> findAllUsersWithOrders();
```

### Q19. What is Auditing in Spring Data JPA? [Medium]
**Answer:** Auditing allows you to automatically populate specific fields when an entity is created or modified. By enabling `@EnableJpaAuditing`, you can use annotations like `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, and `@LastModifiedBy` on entity fields, and Spring handles setting the timestamps and current user automatically.

### Q20. What is a Projection in Spring Data JPA? [Medium]
**Answer:** Projections allow you to fetch only a subset of columns from the database instead of the entire heavy Entity object. You can use Interface-based projections (defining an interface with getter methods) or Class-based projections (DTOs), which significantly improves read performance.

---

## Advanced Level Questions

### Q21. How does `saveAndFlush()` differ from `save()`? [Hard]
**Answer:** 
* `save()` adds the entity to the Hibernate Persistence Context. The actual SQL `INSERT`/`UPDATE` is delayed until the end of the transaction or until the context is automatically flushed.
* `saveAndFlush()` immediately forces Hibernate to execute the SQL statement against the database. This is useful if you need to catch a database-level constraint violation immediately or trigger a database trigger during the transaction.

### Q22. Explain how Optimistic Locking works in JPA. [Hard]
**Answer:** Optimistic locking prevents "lost updates" in highly concurrent environments without using database locks. You add a field annotated with `@Version` (usually an integer or timestamp) to your entity. When JPA updates a record, it includes `WHERE id=? AND version=?` in the SQL, and increments the version. If another transaction modified the record in the meantime, the version won't match, 0 rows will be updated, and JPA throws an `ObjectOptimisticLockingFailureException`.

### Q23. What happens if you try to access a Lazy collection outside of an active Transaction? [Hard]
**Answer:** You will get a `LazyInitializationException`. When a transaction ends, the Hibernate `Session` is closed. If you then try to access a lazy proxy (e.g., `user.getOrders()`), the proxy tries to hit the database to fetch the data, but it can't because the session is closed.

### Q24. How do you solve the `LazyInitializationException`? [Hard]
**Answer:** 
1. **Best Practice:** Keep the transaction open in the Service layer and initialize the required lazy collections (or use `JOIN FETCH`/`@EntityGraph` in the repository) before returning the DTO to the Controller.
2. **Anti-pattern:** Use `OpenSessionInView` (enabled by default in Spring Boot). It keeps the database connection open while the View (or JSON serialization) is rendering. It prevents the exception but can lead to severe connection pool exhaustion and performance issues.

### Q25. What is the `JpaSpecificationExecutor` interface? [Hard]
**Answer:** It is an interface your repository can extend to execute queries based on the JPA Criteria API. It allows you to build highly complex, dynamic `WHERE` clauses programmatically at runtime (e.g., for advanced search filters) without writing enormous blocks of JPQL.

### Q26. Explain the difference between `@OneToMany` and `@ManyToMany` mapping complexities. [Hard]
**Answer:** 
* `@OneToMany` maps to a foreign key column in the child table. It is efficient and straightforward.
* `@ManyToMany` requires a hidden "join table" (junction table) in the database. JPA manages this automatically, but doing massive updates or deletes on Many-To-Many collections can result in terrible performance (e.g., deleting all rows in the join table and re-inserting them just to remove one item).

### Q27. What is `CascadeType` and `orphanRemoval`? [Hard]
**Answer:** 
* `CascadeType` defines how state changes (PERSIST, MERGE, REMOVE) cascade from a parent entity to its children. E.g., `CascadeType.ALL` means if you delete the parent, JPA automatically deletes all children.
* `orphanRemoval = true` is specifically for collections. If you remove a child entity from the parent's collection list, JPA will execute a SQL `DELETE` to completely remove that child row from the database (it becomes an "orphan").

### Q28. How does Spring Data JPA handle transactions internally? [Hard]
**Answer:** By default, all methods in a `JpaRepository` implementation (SimpleJpaRepository) are annotated with `@Transactional`. Read operations (like `findById`) are usually marked with `@Transactional(readOnly = true)` for performance optimization. If you call a repository method from a Service method that already has `@Transactional`, the repository method simply joins the existing transaction (Propagation.REQUIRED).

### Q29. How do you perform a bulk delete operation efficiently? [Hard]
**Answer:** `repository.deleteAll(entities)` is very inefficient because it loads every entity into memory and issues an individual `DELETE` statement for each one (to respect cascade rules). To delete efficiently, use `repository.deleteAllInBatch(entities)` or write a custom `@Modifying` query (`DELETE FROM User u WHERE u.id IN :ids`), which executes a single SQL statement.

### Q30. What is Query by Example (QBE) in Spring Data? [Hard]
**Answer:** It's an alternative querying technique where you create an actual instance of your Entity, populate the fields you want to search by, wrap it in an `Example<T>` object, and pass it to the repository. Spring automatically generates a query matching those non-null fields. It's great for simple dynamic queries but doesn't support complex logic like greater-than/less-than or complex joins.
