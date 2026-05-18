# Spring Data JPA: Advanced Interview Questions

## 1. What is the difference between Hibernate, JPA, and Spring Data JPA?

This is a fundamental conceptual question to test clarity of architecture.

*   **JPA (Java Persistence API)**: It is just a **specification** (a set of interfaces and rules) defined in Java EE. It describes how relational data should be mapped to Java objects. It has no actual implementation.
*   **Hibernate**: It is an **implementation** of the JPA specification (an ORM framework). It provides the actual code that performs the database operations, mapping, and connection pooling. Other implementations include EclipseLink and OpenJPA.
*   **Spring Data JPA**: It is a Spring framework module that sits *on top of* JPA. It is an abstraction layer that removes the need to write boilerplate repository code (like `EntityManager.persist()` or `EntityManager.find()`). It automatically generates the implementation for repository interfaces at runtime.

---

## 2. Explain the difference between `save()` and `saveAndFlush()` in Spring Data JPA.

Both methods are used to persist entities, but their interaction with the Hibernate Session/Persistence Context differs.

*   **`save(entity)`**: It adds the entity to the current Persistence Context. Hibernate will not execute the actual SQL `INSERT` or `UPDATE` immediately. Instead, it waits until the transaction commits or the session is flushed automatically (behind the scenes). This allows Hibernate to batch multiple operations together for better performance.
*   **`saveAndFlush(entity)`**: It saves the entity and immediately triggers a `flush()` on the underlying `EntityManager`. This forces Hibernate to execute the SQL statement against the database *immediately* during the transaction, before moving to the next line of code.

**When to use `saveAndFlush`?**
When you are inserting a record and you immediately need to trigger a database-level trigger, or you need to catch a `DataIntegrityViolationException` (like a unique constraint violation) immediately on that exact line of code, rather than waiting for the end of the transaction.

---

## 3. What is the N+1 Query Problem in JPA? How do you solve it?

**The Problem:**
Occurs when you fetch a list of entities (1 query) and then access a lazily-loaded related entity for each of them (N queries).
*Example*: Fetching 50 `Author` entities (`SELECT * FROM Author`). Then looping through them and calling `author.getBooks()`. Since books are usually `@OneToMany(fetch = FetchType.LAZY)`, Hibernate executes an additional query for *each* author (`SELECT * FROM Book WHERE author_id = ?`). Total queries = 1 + 50 = 51.

**Solutions:**

**1. JOIN FETCH in JPQL / @Query:**
Explicitly tell Hibernate to fetch the related entities in the initial query using an inner or left join.
```java
@Query("SELECT a FROM Author a JOIN FETCH a.books")
List<Author> findAllAuthorsWithBooks();
```

**2. EntityGraphs (Spring Data JPA feature):**
Allows you to define fetch plans dynamically without writing JPQL.
```java
@EntityGraph(attributePaths = {"books"})
List<Author> findAll();
```

**3. @BatchSize (Hibernate specific):**
If you still want Lazy loading but want to optimize it. You can tell Hibernate to fetch collections in batches instead of one by one.
```java
@Entity
public class Author {
    @OneToMany(mappedBy = "author")
    @BatchSize(size = 10) // Will fetch books for 10 authors in an IN clause
    private List<Book> books;
}
```

---

## 4. How does Pagination work in Spring Data JPA?

Spring Data JPA provides built-in support for pagination and sorting using the `Pageable` interface and `Page` return type.

**Code Example:**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data automatically handles the LIMIT and OFFSET based on the Pageable object
    Page<User> findByStatus(String status, Pageable pageable);
}
```

**Service Layer Usage:**
```java
// Fetch page 0, size 10, sorted by 'createdAt' descending
Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
Page<User> userPage = userRepository.findByStatus("ACTIVE", pageable);

List<User> users = userPage.getContent();
int totalPages = userPage.getTotalPages();
long totalElements = userPage.getTotalElements();
```
*Note: A `Page` return type triggers an extra `COUNT(*)` query to determine the total number of elements. If you only need "Next/Prev" functionality without total counts, use `Slice<User>` instead to avoid the expensive count query.*

---

## 5. What are Projections in Spring Data JPA?

Projections allow you to fetch only specific columns from the database rather than the entire Entity. This is crucial for performance when you have large entities but only need a few fields.

**1. Interface-based Projections (Closed Projections)**
Define an interface containing getter methods for the fields you want. Spring generates a proxy implementing this interface.
```java
public interface UserSummary {
    String getFirstName();
    String getLastName();
}

// In Repository
List<UserSummary> findByStatus(String status);
```

**2. Class-based Projections (DTOs)**
Use standard DTO classes.
```java
public class UserDto {
    private String name;
    // constructor matching the fields
    public UserDto(String firstName, String lastName) { 
        this.name = firstName + " " + lastName; 
    }
}

// In Repository (requires fully qualified class name)
@Query("SELECT new com.example.UserDto(u.firstName, u.lastName) FROM User u")
List<UserDto> findAllUserDtos();
```

---

## 6. Explain the `@Transactional` annotation. How does proxying work?

`@Transactional` manages database transactions declaratively.

**How it works (AOP Proxying):**
When you annotate a class or method with `@Transactional`, Spring creates a dynamic proxy around that class. 
When another class calls the transactional method:
1.  The proxy intercepts the call.
2.  The proxy starts a database transaction (or joins an existing one based on Propagation).
3.  It invokes the actual target method.
4.  If the method completes successfully, the proxy **commits** the transaction.
5.  If the method throws an **unchecked exception** (e.g., `RuntimeException`), the proxy **rolls back** the transaction. (It does *not* roll back for checked exceptions by default unless specified via `rollbackFor`).

**Self-Invocation Problem:**
Because it relies on proxies, calling a `@Transactional` method from *within the same class* will **NOT** create a transaction. The call bypasses the proxy and hits the target object directly.

---

## 7. What is the difference between `CrudRepository` and `JpaRepository`? Which one should you use?

Both are interfaces provided by Spring Data, but they serve slightly different purposes and offer different features.

*   **`CrudRepository`**: Provides basic CRUD (Create, Read, Update, Delete) functions. It returns `Iterable` for its `findAll()` method.
*   **`PagingAndSortingRepository`**: Extends `CrudRepository` and adds methods to enable pagination and sorting.
*   **`JpaRepository`**: Extends `PagingAndSortingRepository` (and therefore `CrudRepository`). It provides JPA-specific methods like flushing the persistence context (`flush()`, `saveAndFlush()`) and batch deletion (`deleteInBatch()`). Crucially, it returns `List` instead of `Iterable` for methods like `findAll()`, which is often much more convenient to work with.

**Use Cases:**
*   Use `CrudRepository` if you want to keep your project strictly decoupled from JPA-specific technologies (e.g., if you might switch to Spring Data MongoDB later).
*   Use `JpaRepository` if you are firmly building a JPA-based project and need the extra JPA-specific features (flushing, batch operations, returning `List`). This is the standard choice for most Spring Boot SQL projects.

---

## 8. Complete Code Example: Putting it all together (Entity, Repository, Service)

To properly understand JPA, let's look at a comprehensive example involving an `Employee` entity with relationships, a custom repository, and a transactional service.

### 1. The JPA Entity (`Employee.java`)
```java
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "employees") // Maps to 'employees' table in DB
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    // Many Employees belong to One Department
    // FetchType.LAZY is best practice for performance
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id") // Foreign key column
    private Department department;

    // One Employee can have Many Tasks
    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    // Constructors, Getters, and Setters omitted for brevity
}
```

### 2. The Repository (`EmployeeRepository.java`)
```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 1. Spring Data Method Derivation (no SQL needed)
    Optional<Employee> findByEmail(String email);

    // 2. Custom JPQL Query (using objects, not tables)
    @Query("SELECT e FROM Employee e WHERE e.department.name = :deptName")
    List<Employee> findEmployeesByDepartmentName(@Param("deptName") String deptName);

    // 3. Solving N+1 problem using JOIN FETCH
    @Query("SELECT e FROM Employee e JOIN FETCH e.tasks WHERE e.id = :id")
    Optional<Employee> findByIdWithTasks(@Param("id") Long id);
}
```

### 3. The Service Layer (`EmployeeService.java`)
```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // Constructor Injection (Best Practice)
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Wraps the method in a database transaction
    @Transactional
    public Employee createEmployeeAndAssignTask(Employee employee, Task task) {
        // 1. Save the employee
        Employee savedEmployee = employeeRepository.save(employee);
        
        // 2. Add task to employee's list 
        // (CascadeType.ALL ensures the task gets saved to DB automatically)
        task.setAssignee(savedEmployee);
        savedEmployee.getTasks().add(task);
        
        // If an exception is thrown here, the whole transaction rolls back,
        // and the employee won't be saved in the database.
        
        return savedEmployee;
    }
    
    @Transactional(readOnly = true) // Optimization for read-only operations
    public Employee getEmployeeDetails(Long id) {
        // Uses the custom JOIN FETCH query to prevent N+1 problem
        return employeeRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }
}
```
