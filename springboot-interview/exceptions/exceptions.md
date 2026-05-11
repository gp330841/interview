# Spring Boot - Exception Handling

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Standardized API Error Responses**: A mobile application consumes your REST API. If something goes wrong (e.g., validation fails, user not found, database down), the mobile app expects a strictly formatted JSON response like `{"timestamp": "...", "status": 404, "errorCode": "USER_MISSING", "message": "User ID 123 not found"}`. Using `@RestControllerAdvice`, you intercept all exceptions thrown anywhere in the application and format them into this consistent JSON structure before sending it to the client.
2. **Handling Form Validation Errors**: A user submits a registration form but leaves the email field blank. Spring validation throws a `MethodArgumentNotValidException`. An exception handler catches this, extracts the specific field errors ("Email cannot be null"), and returns a 400 Bad Request containing a list of exactly which fields failed, allowing the frontend to highlight the input boxes in red.

---

## Beginner Level Questions

### Q1. How does standard Java exception handling work? [Easy]
**Answer:** In standard Java, you use `try`, `catch`, and `finally` blocks. You try to execute code, catch specific exceptions if they occur to handle them gracefully, and use finally to close resources regardless of success or failure.

### Q2. Why shouldn't you use `try-catch` blocks in every Spring Boot Controller method? [Easy]
**Answer:** Using `try-catch` in every method leads to massive code duplication, violates the DRY (Don't Repeat Yourself) principle, mixes business logic with error handling logic, and makes ensuring a consistent API error response format across the entire application extremely difficult.

### Q3. What is the `@ExceptionHandler` annotation? [Easy]
**Answer:** It is an annotation used at the method level to define a method that will handle specific exceptions thrown by request handling methods (`@RequestMapping`).

### Q4. Where can you place an `@ExceptionHandler` method? [Easy]
**Answer:** You can place it directly inside a `@Controller` or `@RestController` class. However, in this case, it will *only* handle exceptions thrown by methods within that specific controller class.

### Q5. What is `@ControllerAdvice`? [Easy]
**Answer:** It is a class-level annotation that allows you to consolidate your multiple, scattered `@ExceptionHandler`s from all over your application into a single, global error-handling component. It intercepts exceptions from *all* controllers.

### Q6. What is the difference between `@ControllerAdvice` and `@RestControllerAdvice`? [Easy]
**Answer:** 
* `@ControllerAdvice` is used in traditional MVC apps; its exception handler methods resolve to Views (HTML pages).
* `@RestControllerAdvice` is a convenience annotation combining `@ControllerAdvice` and `@ResponseBody`. It automatically serializes the returned object from the exception handler method into JSON (or XML) and writes it directly to the HTTP response body.

### Q7. How do you return a specific HTTP status code from an `@ExceptionHandler`? [Easy]
**Answer:** You can use the `@ResponseStatus` annotation on the handler method, or you can have the method return a `ResponseEntity<Object>`, which allows dynamic control over the status code, headers, and body.

### Q8. What happens if an exception is thrown and you haven't written a handler for it? [Easy]
**Answer:** Spring Boot provides a default fallback error handling mechanism. For web browsers, it returns a generic "Whitelabel Error Page". For REST clients (Postman/Curl), it returns a generic JSON payload containing the timestamp, status, error, and path.

### Q9. How do you map a custom exception to an HTTP status code without an `@ExceptionHandler`? [Easy]
**Answer:** By annotating your custom exception class directly with `@ResponseStatus`.
**Code Example:**
```java
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found")
public class UserNotFoundException extends RuntimeException { ... }
```

### Q10. What is `ResponseStatusException`? [Easy]
**Answer:** Introduced in Spring 5, it is an exception class you can instantiate and throw directly from your business logic without needing to create custom exception classes or `@ExceptionHandler` methods.
**Code Example:**
```java
throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
```

---

## Intermediate Level Questions

### Q11. How does Spring Boot determine which `@ExceptionHandler` to call if an exception is thrown? [Medium]
**Answer:** Spring looks for the most specific exception handler. If a `NullPointerException` is thrown, it looks for an `@ExceptionHandler(NullPointerException.class)`. If it doesn't find one, it walks up the exception hierarchy and looks for an `@ExceptionHandler(RuntimeException.class)`, and finally `@ExceptionHandler(Exception.class)`.

### Q12. What is `ResponseEntityExceptionHandler` and why should you extend it? [Medium]
**Answer:** It is a convenient base class for `@ControllerAdvice` classes provided by Spring. It already contains `@ExceptionHandler` methods for standard internal Spring MVC exceptions (like `HttpRequestMethodNotSupportedException`, `HttpMediaTypeNotSupportedException`, etc.). Extending it allows you to customize the responses for these standard exceptions by overriding its protected methods (like `handleExceptionInternal`).

### Q13. How do you handle Validation errors globally (e.g., when `@Valid` fails)? [Medium]
**Answer:** When `@Valid` fails on a `@RequestBody`, Spring throws a `MethodArgumentNotValidException`. In your `@RestControllerAdvice`, you can write an `@ExceptionHandler` for this specific exception, extract the `BindingResult`, iterate through the `FieldError`s, and return a clean list of what the user did wrong. (If extending `ResponseEntityExceptionHandler`, you override `handleMethodArgumentNotValid`).

### Q14. Can you have multiple `@ControllerAdvice` classes? [Medium]
**Answer:** Yes. You can have multiple advice classes to organize your error handling (e.g., `DatabaseExceptionHandler`, `ValidationExceptionHandler`).

### Q15. How do you control the order in which multiple `@ControllerAdvice` classes are evaluated? [Medium]
**Answer:** By using the `@Order` annotation or implementing the `Ordered` interface on the advice classes. Lower numbers have higher precedence.

### Q16. Can `@ControllerAdvice` target specific packages or controllers? [Medium]
**Answer:** Yes. By default, it assists all controllers. You can restrict it using the `basePackages`, `annotations`, or `assignableTypes` attributes.
**Code Example:**
```java
@ControllerAdvice(basePackages = "com.myapp.api") // Only handles exceptions from this package
```

### Q17. How do you handle exceptions thrown outside the Controller layer (e.g., in a Filter)? [Medium]
**Answer:** `@ControllerAdvice` ONLY intercepts exceptions thrown by the `DispatcherServlet` (Controllers). If an exception is thrown in a Servlet Filter (like Spring Security filters), the advice cannot catch it. You must either handle it within the filter itself (using `try-catch` and writing directly to the `HttpServletResponse`) or use a custom `HandlerExceptionResolver`.

### Q18. What is `HandlerExceptionResolver`? [Medium]
**Answer:** It is the core Spring interface for resolving exceptions. `@ControllerAdvice` is just the modern, declarative way of utilizing it (specifically via `ExceptionHandlerExceptionResolver`). You can implement this interface directly to build highly custom, low-level exception resolution logic that executes before the response is committed.

### Q19. How do you customize the default Spring Boot "Whitelabel" error page and JSON response? [Medium]
**Answer:** You can provide your own implementation of the `ErrorController` interface, or more simply, you can create a custom `ErrorAttributes` bean to change the contents of the default JSON payload, and create an HTML file named `error.html` in your `/public` or `/templates` directory for the UI.

### Q20. How do you log the exception details before returning the response? [Medium]
**Answer:** Inside your `@ExceptionHandler` method, you simply inject a Logger (e.g., SLF4J) and log the exception stack trace (`log.error("Error occurred", ex)`) before returning your `ResponseEntity`.

---

## Advanced Level Questions

### Q21. What happens if an Exception is thrown inside an `@ExceptionHandler` method? [Hard]
**Answer:** The exception handling process aborts. Spring does not attempt to find *another* exception handler to catch the new exception. The error propagates up to the Servlet Container (Tomcat), which will typically return a raw 500 Internal Server Error page to the client.

### Q22. Explain how to handle exceptions in asynchronous methods (`@Async`). [Hard]
**Answer:** Exceptions thrown in `@Async` methods returning `void` cannot be caught by `@ControllerAdvice` because they execute on a different thread. To handle them, you must implement `AsyncConfigurer` and override the `getAsyncUncaughtExceptionHandler()` method to provide a custom `AsyncUncaughtExceptionHandler` to log or process the error. If the method returns a `Future` or `CompletableFuture`, the caller must handle the exception when calling `.get()`.

### Q23. How do you handle `AccessDeniedException` from Spring Security? [Hard]
**Answer:** Spring Security's `ExceptionTranslationFilter` usually catches `AccessDeniedException` and redirects to a login page or returns a 403. If you want to handle it in `@ControllerAdvice` for a REST API, you can, but you must ensure the advice executes. Alternatively, provide a custom `AccessDeniedHandler` in your Spring Security configuration to format the 403 JSON response.

### Q24. How do you handle exceptions thrown during JSON deserialization (e.g., malformed JSON)? [Hard]
**Answer:** If Jackson fails to parse the incoming JSON (e.g., parsing a String into an Integer field), it throws an `HttpMessageNotReadableException`. You must add an `@ExceptionHandler(HttpMessageNotReadableException.class)` in your advice class to catch this and return a 400 Bad Request explaining the format error.

### Q25. What is Problem Details for HTTP APIs (RFC 7807)? [Hard]
**Answer:** It is a standardized JSON format for reporting errors in HTTP APIs. Instead of everyone inventing their own error JSON structure, RFC 7807 dictates fields like `type`, `title`, `status`, `detail`, and `instance`. Spring Boot 3 has built-in support for this via `ProblemDetail` class.

### Q26. How do you enable RFC 7807 Problem Details in Spring Boot 3? [Hard]
**Answer:** You can enable it globally by setting `spring.mvc.problemdetails.enabled=true` in `application.properties`. When enabled, Spring's default exception handlers and `ResponseEntityExceptionHandler` will automatically format responses using the `ProblemDetail` specification instead of the standard Spring Boot error JSON.

### Q27. How do you add custom fields to a `ProblemDetail` response? [Hard]
**Answer:** The `ProblemDetail` class provides a `setProperty(String name, Object value)` method. Inside your `@ExceptionHandler`, you instantiate or retrieve a `ProblemDetail` object, set your custom properties (e.g., a tracking ID or a list of validation errors), and return it.

### Q28. How does `@ControllerAdvice` affect performance? [Hard]
**Answer:** Because it relies on AOP and reflection to match exception hierarchies to handler methods at runtime, it does add a slight overhead. However, Spring caches the resolution results after the first time an exception is handled. Overall, the organizational benefits vastly outweigh the negligible performance cost.

### Q29. How do you test `@ControllerAdvice` classes? [Hard]
**Answer:** You test them using `@WebMvcTest` and `MockMvc`. You write a test that sends an HTTP request to a controller endpoint. In the test setup, you mock the controller's underlying service to forcefully throw the specific exception you want to test. Then, you use `MockMvc` assertions to verify that the HTTP status code and the JSON body returned match the logic in your `@ControllerAdvice`.

### Q30. Can you use `try-catch` AND `@ControllerAdvice` together? [Hard]
**Answer:** Yes. You might use `try-catch` in a Service layer if you can actually *recover* from the error (e.g., if Database A fails, catch the exception and try saving to Database B). If you cannot recover, you should let the exception bubble up to the Controller, where the `@ControllerAdvice` will intercept it and format the HTTP response.
