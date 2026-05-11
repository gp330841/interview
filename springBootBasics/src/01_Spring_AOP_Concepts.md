# Spring AOP (Aspect-Oriented Programming): Advanced Interview Questions

## 1. What is AOP? What problem does it solve?
AOP (Aspect-Oriented Programming) is a programming paradigm that aims to increase modularity by allowing the separation of **Cross-Cutting Concerns**. 

A cross-cutting concern is a logic that is scattered across multiple classes and modules in an application, but isn't part of the core business logic. 
*Examples*: Logging, Transaction Management, Security (Authorization), Error Handling, and Performance Monitoring.

Without AOP, you would have to duplicate logging or transaction code in every single service method, violating the DRY (Don't Repeat Yourself) principle and making code hard to maintain. AOP allows you to define this logic in one place (an **Aspect**) and dynamically apply it to your business methods.

---

## 2. Define the core terminologies in Spring AOP: Aspect, Advice, Pointcut, JoinPoint.

*   **Aspect**: A modularization of a concern that cuts across multiple classes. (e.g., A `LoggingAspect` class).
*   **Advice**: The actual action (code) taken by an aspect at a particular JoinPoint. (e.g., The code that prints "Method execution started"). Types include `Before`, `After`, `Around`, etc.
*   **JoinPoint**: A specific point during the execution of a program where an aspect can be plugged in. In Spring AOP, a JoinPoint **always represents a method execution**. You can use the `JoinPoint` object in your advice to get method signatures, arguments, etc.
*   **Pointcut**: A predicate/expression that matches JoinPoints. It defines *where* the advice should be applied. (e.g., "Apply this advice to all methods inside the `service` package").
*   **Weaving**: The process of linking aspects with other application types to create an advised object. Spring AOP performs weaving at **runtime** using dynamic proxies.

---

## 3. Explain the different types of Advice in Spring AOP.

1.  **@Before**: Executes before the join point (target method). It cannot prevent the method execution unless it throws an exception.
2.  **@AfterReturning**: Executes *only* if the join point completes normally (without throwing an exception). You can access the returned result.
3.  **@AfterThrowing**: Executes *only* if the method exits by throwing an exception.
4.  **@After (Finally)**: Executes regardless of how the join point exits (normal return or exception thrown). Similar to a `finally` block.
5.  **@Around**: The most powerful advice. It surrounds the join point. It can execute custom behavior before and after the method. It is responsible for choosing whether to proceed to the actual method or return its own custom result, effectively bypassing the method entirely.

---

## 4. How do you implement @Around Advice? (Code Example)

`@Around` advice requires a `ProceedingJoinPoint` argument, which extends `JoinPoint` and adds the `proceed()` method to execute the target method.

**Use Case:** Calculating the execution time of service methods.

```java
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitoringAspect {

    // Pointcut: Matches all methods in classes ending with 'Service'
    @Around("execution(* com.example.service.*Service.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // 1. Before method execution
        System.out.println("Starting execution of: " + joinPoint.getSignature());

        Object result;
        try {
            // 2. Execute the actual target method
            result = joinPoint.proceed(); 
        } catch (Throwable ex) {
            // Can handle exceptions here
            throw ex;
        }

        // 3. After method execution
        long executionTime = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " executed in " + executionTime + "ms");

        // MUST return the result of the target method
        return result;
    }
}
```

---

## 5. How does Spring AOP differ from AspectJ?

*   **Weaving**: 
    *   **Spring AOP**: Uses **Runtime Weaving** via JDK dynamic proxies (for interfaces) or CGLIB proxies (for classes).
    *   **AspectJ**: Uses **Compile-time Weaving** (using a special compiler, `ajc`) or **Load-time Weaving**. It alters the actual `.class` files.
*   **JoinPoints**: 
    *   **Spring AOP**: Only supports **method execution** join points. You cannot intercept field access or object initialization.
    *   **AspectJ**: Supports field access, object creation, constructor execution, and method execution. Much more powerful.
*   **Performance**:
    *   **Spring AOP**: Slower due to proxy creation at runtime.
    *   **AspectJ**: Faster at runtime since weaving is done during compilation.
*   **Self-Invocation**:
    *   **Spring AOP**: If Method A calls Method B within the same class, the AOP advice on Method B will **not** be triggered because the proxy is bypassed.
    *   **AspectJ**: Solves the self-invocation issue because the byte-code itself is modified.

---

## 6. What is a Pointcut Expression? Give an example.

A pointcut expression uses AspectJ syntax to define which methods should be intercepted.

**Syntax Structure**: `execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?)`

**Examples**:
*   `execution(public * *(..))`: Any public method.
*   `execution(* set*(..))`: Any method starting with "set".
*   `execution(* com.example.service.UserService.*(..))`: Any method inside the `UserService` class.
*   `@annotation(org.springframework.transaction.annotation.Transactional)`: Any method annotated with `@Transactional`.
