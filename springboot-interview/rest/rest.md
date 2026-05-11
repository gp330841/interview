# Spring Boot - REST APIs

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Headless E-Commerce Backend**: A Spring Boot application exposes RESTful endpoints (`/api/v1/products`, `/api/v1/orders`). It doesn't render HTML. Instead, it accepts JSON payloads from a React/Angular frontend or a mobile app, processes the data, and returns JSON responses with appropriate HTTP status codes (200 OK, 201 Created, 404 Not Found).
2. **Third-Party Integrations via WebClient**: A microservice needs to fetch weather data from an external API (like OpenWeatherMap). It uses Spring's `WebClient` (or `RestTemplate`) to send asynchronous HTTP GET requests, parse the incoming JSON response into a Java DTO, and use that data in its own business logic.

---

## Beginner Level Questions

### Q1. What is REST? [Easy]
**Answer:** REST (Representational State Transfer) is an architectural style for designing networked applications. It relies on a stateless, client-server protocol, usually HTTP. In REST, resources (like Users or Orders) are identified by URIs, and standard HTTP methods (GET, POST, PUT, DELETE) are used to manipulate these resources.

### Q2. What is the difference between `@Controller` and `@RestController`? [Easy]
**Answer:** 
* `@Controller` is used for traditional Spring MVC where methods return logical view names (HTML pages) to a ViewResolver.
* `@RestController` is a convenience annotation that combines `@Controller` and `@ResponseBody`. It tells Spring that the return value of the methods should be automatically serialized (usually to JSON) and written directly into the HTTP response body.

### Q3. What is the `@ResponseBody` annotation? [Easy]
**Answer:** It tells Spring to bypass the ViewResolver entirely. Instead of looking for an HTML template, Spring takes the returned Java object, converts it to JSON (or XML) using an `HttpMessageConverter` (like Jackson), and writes it straight to the HTTP response body.

### Q4. What is the `@RequestBody` annotation? [Easy]
**Answer:** It is used to bind the HTTP request body (e.g., incoming JSON data) to a Java object in a method parameter. Spring uses an `HttpMessageConverter` to deserialize the incoming JSON string into the specified Java POJO.
**Code Example:**
```java
@PostMapping("/users")
public User createUser(@RequestBody User user) { ... }
```

### Q5. Explain the standard HTTP Methods used in REST. [Easy]
**Answer:** 
* **GET**: Retrieve a resource (Read-only, idempotent).
* **POST**: Create a new resource (Not idempotent).
* **PUT**: Update an existing resource completely, or create it if it doesn't exist (Idempotent).
* **PATCH**: Partially update an existing resource.
* **DELETE**: Remove a resource (Idempotent).

### Q6. What does "Idempotent" mean in the context of REST? [Easy]
**Answer:** An HTTP method is idempotent if making multiple identical requests has the same effect on the server as making a single request. `GET`, `PUT`, and `DELETE` are idempotent. `POST` is not (sending a POST twice will create two resources).

### Q7. How do you return a specific HTTP Status Code from a REST controller? [Easy]
**Answer:** You can wrap your return object in a `ResponseEntity` object, which allows you to specify the status code, headers, and body.
**Code Example:**
```java
@GetMapping("/users/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    User user = userService.find(id);
    if (user == null) return ResponseEntity.notFound().build(); // 404
    return ResponseEntity.ok(user); // 200
}
```

### Q8. What is the purpose of `@ResponseStatus`? [Easy]
**Answer:** It allows you to declare the HTTP status code that should be returned when a specific controller method executes successfully, or when a specific Exception is thrown.
**Code Example:**
```java
@PostMapping("/users")
@ResponseStatus(HttpStatus.CREATED) // Returns 201 instead of default 200
public User createUser(@RequestBody User user) { ... }
```

### Q9. What library does Spring Boot use by default to convert Java objects to JSON? [Easy]
**Answer:** **Jackson** (specifically `jackson-databind`). It is automatically included when you add the `spring-boot-starter-web` dependency.

### Q10. How do you ignore a field when serializing an object to JSON? [Easy]
**Answer:** By using the `@JsonIgnore` annotation from the Jackson library on the field you want to hide (e.g., a user's password).

---

## Intermediate Level Questions

### Q11. What is the difference between PUT and PATCH? [Medium]
**Answer:** 
* **PUT** replaces the *entire* resource. If a field is missing in the PUT request payload, the server typically sets that field to null.
* **PATCH** applies a *partial* update. It only modifies the fields that are provided in the payload, leaving the rest of the resource intact.

### Q12. Explain Content Negotiation in REST. [Medium]
**Answer:** It's the mechanism by which the client and server agree on the format of the data being exchanged. A client sends an `Accept` header (e.g., `Accept: application/xml`) to tell the server what format it expects. Spring Boot looks at this header, checks its available `HttpMessageConverters`, and returns the response in the requested format (if supported).

### Q13. How do you handle exceptions globally in a REST API? [Medium]
**Answer:** By creating a class annotated with `@RestControllerAdvice` (which is `@ControllerAdvice` + `@ResponseBody`) and writing methods annotated with `@ExceptionHandler`.
**Code Example:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(new ErrorDetails(ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}
```

### Q14. What is `RestTemplate`? [Medium]
**Answer:** `RestTemplate` is a synchronous HTTP client provided by Spring to make calling external REST APIs easier. It abstracts away the complex boilerplate of creating HTTP connections and automatically converts request/response bodies to/from Java objects.

### Q15. Why is `RestTemplate` marked for deprecation / in maintenance mode? [Medium]
**Answer:** Spring 5 introduced the reactive stack. `RestTemplate` is blocking and synchronous, which doesn't scale well in high-throughput scenarios. Spring strongly recommends using `WebClient` (from `spring-webflux`) for all new development, as it supports both synchronous and asynchronous (reactive) API calls.

### Q16. How do you document a Spring Boot REST API? [Medium]
**Answer:** Traditionally using **Swagger/OpenAPI**. In modern Spring Boot applications, the `springdoc-openapi-ui` dependency is used. It automatically scans your `@RestController` classes and generates an interactive HTML documentation page (Swagger UI) at `/swagger-ui.html`.

### Q17. How do you validate an incoming JSON payload? [Medium]
**Answer:** By using the `@Valid` (or `@Validated`) annotation alongside `@RequestBody`. The DTO class should have JSR-380 validation annotations like `@NotNull`, `@Size`, or `@Email`. If validation fails, Spring throws a `MethodArgumentNotValidException` (resulting in a 400 Bad Request).

### Q18. What is HATEOAS? [Medium]
**Answer:** Hypermedia As The Engine Of Application State. It is the highest level of REST maturity (Richardson Maturity Model Level 3). A HATEOAS-compliant API includes hypermedia links within its JSON responses. These links guide the client dynamically on what actions they can take next (e.g., a "User" response might include links to "update-user", "delete-user", or "view-orders"). Spring provides the `spring-boot-starter-hateoas` module to implement this.

### Q19. How do you implement pagination in a REST API? [Medium]
**Answer:** You accept `page` and `size` parameters via `@RequestParam`. In Spring Data JPA, you pass these into a `PageRequest.of(page, size)` object and pass that to your repository. You then return a `Page<T>` object, which Spring serializes into a JSON response containing the data array plus metadata like `totalElements` and `totalPages`.

### Q20. What is CORS and how do you configure it in a REST API? [Medium]
**Answer:** Cross-Origin Resource Sharing (CORS) is a browser security feature that blocks web pages from making HTTP requests to a different domain than the one that served the web page. In Spring Boot, you can allow requests from specific origins using the `@CrossOrigin(origins = "http://localhost:3000")` annotation on your controller methods or classes.

---

## Advanced Level Questions

### Q21. Explain the Richardson Maturity Model. [Hard]
**Answer:** It's a model that grades APIs on how strictly they adhere to REST principles:
* **Level 0 (The Swamp of POX):** Uses HTTP strictly as a transport mechanism (like SOAP). Only uses POST for everything, ignoring other methods and status codes.
* **Level 1 (Resources):** Introduces individual URIs for individual resources (e.g., `/users/1`).
* **Level 2 (HTTP Verbs):** Uses standard HTTP methods (GET, POST, PUT, DELETE) and standard HTTP status codes correctly. (Most APIs stop here).
* **Level 3 (Hypermedia Controls):** HATEOAS. Responses include links to related resources and actions.

### Q22. How do you version a REST API? What are the pros and cons of each approach? [Hard]
**Answer:** 
1. **URI Versioning** (`/api/v1/users`): Easiest to implement and test. Con: Clutters the URI, violates the principle that a URI should represent a resource, not a version.
2. **Query Parameter Versioning** (`/api/users?version=1`): Simple. Con: Clutters query string.
3. **Custom Header Versioning** (`X-API-Version: 1`): Keeps URIs clean. Con: Harder to test in a browser without tools like Postman.
4. **Media Type / Accept Header Versioning** (`Accept: application/vnd.mycompany.v1+json`): Most conceptually pure REST approach. Con: Complex to implement and use.

### Q23. How do you secure a REST API? [Hard]
**Answer:** REST APIs are stateless, so session-based authentication (Cookies/JSESSIONID) is inappropriate. 
The standard approach is using **Tokens** (like **JWT - JSON Web Tokens** or OAuth2). The client sends the token in the HTTP `Authorization: Bearer <token>` header with every request. Spring Security intercepts the request, validates the token signature/expiry, and authenticates the user.

### Q24. How do you customize the Jackson Object Mapper globally in Spring Boot? [Hard]
**Answer:** You can define a `@Bean` of type `Jackson2ObjectMapperBuilderCustomizer`. This allows you to configure things globally, such as forcing all dates to be serialized as ISO-8601 strings, enabling/disabling pretty print, or ignoring unknown properties during deserialization.
**Code Example:**
```java
@Bean
public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
}
```

### Q25. How does Spring Boot handle filtering JSON responses dynamically? [Hard]
**Answer:** Jackson provides `@JsonView` for simple filtering based on context (e.g., hiding the `salary` field in a public view but showing it in an admin view). For highly dynamic filtering, you can use Jackson's `MappingJacksonValue` and `SimpleFilterProvider`, or GraphQL (which allows clients to query exactly the fields they want).

### Q26. What is the role of `HttpMessageConverter`? [Hard]
**Answer:** It is an interface that specifies how to convert between HTTP requests/responses and Java objects. When a `@RequestBody` or `@ResponseBody` is encountered, Spring iterates through registered converters (like `MappingJackson2HttpMessageConverter` for JSON, or `StringHttpMessageConverter` for plain text) and asks `canRead()` or `canWrite()`. The first one that says yes handles the conversion.

### Q27. How do you implement a robust Rate Limiting strategy for a REST API? [Hard]
**Answer:** Rate limiting prevents abuse (DDoS) and ensures fair usage. In Spring Boot, this is typically implemented using an API Gateway (like Spring Cloud Gateway) or using a caching layer (like Redis) combined with a library like **Bucket4j**. You intercept incoming requests (via a Filter or Interceptor), check the client's IP or API Key against Redis, and return a `429 Too Many Requests` status if they exceed their quota.

### Q28. What are `ETags` and how do they optimize REST APIs? [Hard]
**Answer:** An ETag (Entity Tag) is an HTTP header representing the version of a resource. 
1. Server sends resource + ETag header.
2. Client caches it. Next time, client sends `If-None-Match: <ETag>`.
3. Server checks if the resource has changed. If not, it returns `304 Not Modified` with an empty body, saving significant bandwidth and processing time. Spring provides `ShallowEtagHeaderFilter` to implement this automatically by hashing the response body.

### Q29. Explain how to use `WebClient` for asynchronous REST calls. [Hard]
**Answer:** `WebClient` is part of Spring WebFlux. It is non-blocking and reactive.
**Code Example:**
```java
Mono<User> userMono = webClient.get()
    .uri("https://api.example.com/users/1")
    .retrieve()
    .bodyToMono(User.class);

// The HTTP call doesn't actually execute until you subscribe to the Mono
userMono.subscribe(user -> System.out.println(user.getName()));
```

### Q30. How do you gracefully handle large file downloads in a REST API to avoid memory exceptions (OOM)? [Hard]
**Answer:** You must never load the entire file into memory (e.g., returning `byte[]`). Instead, you should stream it. In Spring Boot, you can return a `StreamingResponseBody`, `Resource`, or `InputStreamResource`. This allows Spring to stream the file chunks directly to the HTTP output stream, keeping memory footprint low.
