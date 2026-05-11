# Spring Boot - Aspect-Oriented Programming (AOP)

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Centralized Logging and Performance Monitoring**: Instead of adding `log.info("started")`, `log.info("ended")`, and execution time tracking code into every single service method, you create a single `@Around` Aspect. This aspect intercepts calls to any method in the `service` package, logs the start/end, and records the execution time, keeping business logic incredibly clean.
2. **Method-Level Security (Authorization)**: When a user attempts to call `deleteUser(id)`, an AOP Aspect intercepts the request. The aspect checks if the current logged-in user holds the `ADMIN` role. If not, it throws an `AccessDeniedException` and prevents the actual `deleteUser` method from ever executing. Spring Security's `@PreAuthorize` uses this exact mechanism.

---

## Beginner Level Questions

### Q1. What is Aspect-Oriented Programming (AOP)? [Easy]
**Answer:** AOP is a programming paradigm that aims to increase modularity by allowing the separation of cross-cutting concerns. It allows you to add additional behavior to existing code without modifying the code itself.

### Q2. What is a "Cross-Cutting Concern"? [Easy]
**Answer:** It is a concern or logic that is scattered across multiple classes and modules in an application. Examples include logging, transaction management, caching, error handling, and security. Because this logic is repetitive, AOP extracts it into a centralized "Aspect."

### Q3. Define the term "Aspect" in Spring AOP. [Easy]
**Answer:** An Aspect is a modularization of a concern that cuts across multiple classes. In Spring, an aspect is typically a regular Java class annotated with `@Aspect`. 

### Q4. What is a "Join Point"? [Easy]
**Answer:** A Join Point is a specific point during the execution of a program where an aspect can be plugged in. In Spring AOP, a join point **always represents a method execution**.

### Q5. What is an "Advice"? [Easy]
**Answer:** Advice is the actual action taken by an aspect at a particular join point. It is the block of code that runs when the aspect is triggered. Types of advice include `@Before`, `@After`, and `@Around`.

### Q6. What is a "Pointcut"? [Easy]
**Answer:** A Pointcut is a predicate or expression that matches join points. It defines exactly *where* the advice should be applied. For example, "apply this advice to all methods inside the `com.myapp.service` package."

### Q7. What is "Weaving"? [Easy]
**Answer:** Weaving is the process of linking aspects with other application types or objects to create an advised object. This can be done at compile time, load time, or runtime. Spring AOP performs weaving at **runtime**.

### Q8. What does the `@Before` advice do? [Easy]
**Answer:** It executes the advice code *before* the target method execution begins. It cannot prevent the execution flow from proceeding to the target method (unless it throws an exception).

### Q9. What does the `@AfterReturning` advice do? [Easy]
**Answer:** It executes the advice code *only if* the target method completes successfully (returns normally without throwing any exceptions). You can also capture the return value of the method in this advice.

### Q10. What does the `@AfterThrowing` advice do? [Easy]
**Answer:** It executes the advice code *only if* the target method exits by throwing an exception. It is highly useful for centralized exception logging.

---

## Intermediate Level Questions

### Q11. Explain the `@Around` advice. Why is it considered the most powerful? [Medium]
**Answer:** `@Around` surrounds the target method execution. It is the most powerful because it controls the entire execution process:
* It can perform custom behavior before *and* after the method invocation.
* It is responsible for explicitly calling `proceed()` to execute the actual target method.
* It can choose to bypass the target method entirely by returning its own value or throwing an exception.

### Q12. How do you pass the target method's execution context into an `@Around` advice? [Medium]
**Answer:** By using the `ProceedingJoinPoint` interface as the first parameter in the advice method. Calling `proceedingJoinPoint.proceed()` actually invokes the target method.
**Code Example:**
```java
@Around("execution(* com.myapp.service.*.*(..))")
public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = pjp.proceed(); // Execute actual method
    long time = System.currentTimeMillis() - start;
    System.out.println("Execution time: " + time);
    return result;
}
```

### Q13. How do you access method arguments inside an Advice? [Medium]
**Answer:** You can use the `JoinPoint` object passed to the advice and call `joinPoint.getArgs()`. Alternatively, you can use the `args()` pointcut designator to bind arguments directly to the advice method parameters.

### Q14. What is the difference between Spring AOP and AspectJ? [Medium]
**Answer:** 
* **Spring AOP**: A simpler, proxy-based AOP framework. It only supports method execution join points. Weaving happens at runtime. It's sufficient for 80% of enterprise use cases.
* **AspectJ**: A full-fledged AOP framework. It supports field interception, constructor interception, and object initialization. Weaving happens at compile-time or load-time by modifying `.class` bytecodes directly. It is much faster and more powerful but harder to configure.

### Q15. Does Spring AOP use compile-time or runtime weaving? [Medium]
**Answer:** Spring AOP uses **runtime weaving**. It creates dynamic proxies (either JDK Dynamic Proxies or CGLIB proxies) around the target beans when the Spring ApplicationContext is loaded.

### Q16. Write a basic Pointcut expression that targets all methods in the `UserService` class. [Medium]
**Answer:**
`execution(* com.myapp.service.UserService.*(..))`
* `*` (first): Any return type.
* `com.myapp.service.UserService.*`: Any method in the `UserService` class.
* `(..)`: Any number and type of parameters.

### Q17. How do you extract and reuse a Pointcut expression? [Medium]
**Answer:** By creating an empty method annotated with `@Pointcut`. Other advices can then reference this method name instead of duplicating the expression.
**Code Example:**
```java
@Pointcut("execution(* com.myapp.service.*.*(..))")
public void serviceLayer() {} // Empty method body

@Before("serviceLayer()")
public void logBefore() { ... }
```

### Q18. Can you intercept private methods using Spring AOP? [Medium]
**Answer:** **No.** Because Spring AOP relies on runtime proxies (which either implement interfaces or extend classes), it can only intercept `public` methods. It cannot intercept `private`, `protected`, or `package-private` methods. If you need to intercept private methods, you must use full AspectJ weaving.

### Q19. What is the `@After` (Finally) advice? [Medium]
**Answer:** It executes after the target method completes, *regardless* of its outcome (whether it returned normally or threw an exception). It acts exactly like a `finally` block in a `try-catch` statement. It is typically used for releasing resources.

### Q20. How do you enable AspectJ support in a Spring Boot application? [Medium]
**Answer:** By ensuring the `spring-boot-starter-aop` dependency is in your `pom.xml`. Spring Boot auto-configuration automatically enables AOP proxying (equivalent to adding `@EnableAspectJAutoProxy`).

---

## Advanced Level Questions

### Q21. What is the "Self-Invocation" problem in Spring AOP? [Hard]
**Answer:** Spring AOP works using proxies. If an external class calls `methodA()` on a Spring Bean, the proxy intercepts it. However, if `methodA()` internally calls `methodB()` *within the same class*, the call is made directly on the `this` reference, bypassing the proxy completely. Therefore, any AOP advice (like `@Transactional` or `@Async`) attached to `methodB()` will **not** be executed.

### Q22. How do you solve the Self-Invocation problem? [Hard]
**Answer:** 
1. Refactor the code to move `methodB()` to a different Spring Bean/Service. (Best Practice)
2. Inject the application context, lookup the bean itself, and call the method on the looked-up bean.
3. Use AspectJ compile-time weaving instead of Spring AOP proxies.

### Q23. Explain the difference between JDK Dynamic Proxies and CGLIB Proxies in Spring AOP. [Hard]
**Answer:** 
* **JDK Dynamic Proxies**: Built into standard Java. Used when the target bean implements at least one interface. The proxy class implements the same interface(s) as the target and delegates calls.
* **CGLIB Proxies**: A third-party library. Used when the target bean does not implement any interfaces. CGLIB dynamically generates a subclass of the target class at runtime and overrides its non-final methods.

### Q24. Can you force Spring Boot to use CGLIB proxies even if interfaces are present? [Hard]
**Answer:** Yes. In Spring Boot 2.0+, CGLIB is actually the default. If you need to configure it manually, you can use `@EnableAspectJAutoProxy(proxyTargetClass = true)`. Setting it to `true` forces CGLIB subclassing. Setting it to `false` forces JDK interface-based proxies.

### Q25. How do you intercept a method based on a custom annotation? [Hard]
**Answer:** By using the `@annotation` pointcut designator in your expression.
**Code Example:**
```java
// Intercepts any method annotated with @LogExecutionTime
@Around("@annotation(com.myapp.annotation.LogExecutionTime)")
public Object logTime(ProceedingJoinPoint pjp) throws Throwable { ... }
```

### Q26. In what order do multiple Aspects execute if they target the same method? [Hard]
**Answer:** The order is officially undefined unless you explicitly specify it. You can define the order by implementing the `Ordered` interface on your Aspect class, or by using the `@Order` annotation. Lower numbers have higher precedence (they execute first on the way *in*, and last on the way *out*).

### Q27. Is it possible to modify the arguments passed to the target method using AOP? [Hard]
**Answer:** Yes, but only by using the `@Around` advice. Inside the advice, you can retrieve the original arguments, modify them, and pass the new array of arguments to the `proceed(Object[] args)` method.

### Q28. What are the performance implications of heavily using Spring AOP? [Hard]
**Answer:** Spring AOP adds overhead because method calls must traverse the proxy and the advice logic before reaching the actual target. While the proxy invocation is fast, creating heavy `@Around` advice or executing complex Pointcut matching on highly trafficked methods can degrade performance. It also increases application startup time due to proxy generation.

### Q29. Can an Aspect be applied to another Aspect? [Hard]
**Answer:** No. Spring AOP proxies cannot be applied to beans annotated with `@Aspect`. The Spring container specifically excludes Aspect classes from auto-proxying to prevent infinite recursive interception loops.

### Q30. Explain the `within` and `args` pointcut designators. [Hard]
**Answer:** 
* `within(com.myapp.service.*)`: Matches all join points within a specific package or class. It is broader and often more performant than `execution`.
* `args(java.lang.String, ..)`: Matches join points where the arguments passed at runtime perfectly match the specified types (e.g., first argument is a String, followed by any number of arguments).
