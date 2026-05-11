# Spring Boot - Testing (JUnit & Mockito)

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Mocking External APIs for Fast Builds**: Your `BillingService` relies on calling Stripe's API. You cannot hit the real Stripe API during your CI/CD pipeline (it's slow, requires network access, and might charge you). Using Mockito (`@Mock`), you create a fake version of the Stripe client that instantly returns a "Success" response, allowing you to test your own business logic in milliseconds without network dependencies.
2. **End-to-End API Validation**: Before deploying to production, you need absolute certainty that your `/api/users` endpoint returns the correct JSON format. Using `@SpringBootTest` and `MockMvc`, you boot up the entire Spring context (including the database, perhaps an H2 in-memory DB or Testcontainers), simulate an HTTP GET request, and assert that the response status is 200 and `$.name` equals "John", ensuring all layers (Controller, Service, Repository) work together flawlessly.

---

## Beginner Level Questions

### Q1. Why is automated testing important? [Easy]
**Answer:** It ensures code works as expected, prevents regressions (breaking existing features when adding new ones), serves as living documentation, and enables rapid refactoring and safe CI/CD deployments.

### Q2. What is JUnit? [Easy]
**Answer:** JUnit is the most widely used testing framework for Java. It provides annotations (like `@Test`, `@BeforeEach`) and assertions (like `assertEquals`) to write and run repeatable automated tests. Modern Spring Boot uses JUnit 5 (Jupiter).

### Q3. What is Mockito? [Easy]
**Answer:** Mockito is a popular mocking framework for Java. It allows you to create "mock" objects (fake implementations of interfaces or classes) to simulate the behavior of real dependencies, isolating the specific unit of code you want to test.

### Q4. What is the difference between Unit Testing and Integration Testing? [Easy]
**Answer:**
* **Unit Testing**: Tests a single component (like one Service class or one method) in isolation. All external dependencies (databases, other services) are mocked. Fast execution.
* **Integration Testing**: Tests how multiple components work *together* (e.g., Service + Repository + Database). Typically involves booting up part or all of the Spring context. Slower execution.

### Q5. What does the `@Test` annotation do? [Easy]
**Answer:** It tells the JUnit framework that the method to which it is attached can be run as a test case.

### Q6. What is the purpose of `@BeforeEach` and `@AfterEach` in JUnit 5? [Easy]
**Answer:**
* `@BeforeEach`: Executes before *every* `@Test` method in the class. Used to set up test data or initialize mocks.
* `@AfterEach`: Executes after *every* `@Test` method. Used to tear down or clean up resources.

### Q7. What is `@SpringBootTest`? [Easy]
**Answer:** It is a Spring Boot annotation used for Integration Testing. It bootstraps the entire Spring `ApplicationContext` (starts the application just like in production, minus the web server by default), allowing you to `@Autowired` real beans and test the full application flow.

### Q8. What is the difference between `@Mock` and `@InjectMocks`? [Easy]
**Answer:**
* `@Mock`: Creates a fake (mock) instance of a dependency (e.g., `UserRepository`).
* `@InjectMocks`: Creates an instance of the class you are actually trying to test (e.g., `UserService`) and automatically injects all the `@Mock` fields into it.

### Q9. How do you define mock behavior using Mockito? [Easy]
**Answer:** Using the `when().thenReturn()` syntax.
**Code Example:**
```java
when(userRepository.findById(1L)).thenReturn(Optional.of(new User("John")));
```

### Q10. How do you verify that a specific method was called on a mock? [Easy]
**Answer:** Using the `verify()` method.
**Code Example:**
```java
verify(userRepository, times(1)).deleteById(1L);
```

---

## Intermediate Level Questions

### Q11. What is the `@WebMvcTest` annotation? [Medium]
**Answer:** It is used to test the web layer (Controllers) in isolation. It does *not* start the full Spring context. It only configures the MVC infrastructure (`DispatcherServlet`, JSON converters) and instantiates the specific Controller you provide. You must use `@MockBean` to mock out the Service layer.

### Q12. What is `MockMvc`? [Medium]
**Answer:** `MockMvc` is a Spring Test framework utility that allows you to simulate sending HTTP requests (GET, POST) to the `DispatcherServlet` and assert the HTTP response status, headers, and body without actually starting a real embedded web server (like Tomcat).

### Q13. Explain `@DataJpaTest`. [Medium]
**Answer:** It is used specifically to test JPA repositories. It disables full auto-configuration and applies only configuration relevant to JPA tests. By default, it uses an in-memory embedded database (like H2) and runs every test in a transaction, automatically rolling back at the end of each test so the database remains clean.

### Q14. What is the difference between `@Mock` and `@MockBean`? [Medium]
**Answer:**
* `@Mock`: A standard Mockito annotation used in pure Unit Tests that don't involve the Spring context.
* `@MockBean`: A Spring Boot annotation. It creates a Mockito mock and *adds it to the Spring ApplicationContext*, replacing any existing real bean of the same type. It is essential when running Integration Tests (like `@WebMvcTest`) where the Controller expects Spring to `@Autowired` a Service.

### Q15. How do you mock an exception being thrown using Mockito? [Medium]
**Answer:** You use the `when().thenThrow()` syntax.
**Code Example:**
```java
when(userRepository.findById(99L)).thenThrow(new EntityNotFoundException());
```

### Q16. What is `ArgumentCaptor` in Mockito? [Medium]
**Answer:** It is used during the `verify()` phase to capture the exact arguments passed to a mocked method, allowing you to run assertions on the internal state of the object that was passed.
**Code Example:**
```java
verify(userRepository).save(captor.capture());
User savedUser = captor.getValue();
assertEquals("John", savedUser.getName());
```

### Q17. How do you test a method that returns `void` using Mockito? [Medium]
**Answer:** You cannot use `when(mock.voidMethod())` because Java doesn't allow void inside method calls. Instead, you use `doNothing()`, `doThrow()`, or `verify()`.
**Code Example:**
```java
doThrow(new RuntimeException()).when(mockService).deleteUser(1L);
```

### Q18. What is the `@MockMvc` `andExpect()` method used for? [Medium]
**Answer:** It is used to chain assertions against the simulated HTTP response. You can expect specific status codes (`status().isOk()`), specific JSON paths (`jsonPath("$.name").value("John")`), or specific headers.

### Q19. How do you test a `@Value` property injection? [Medium]
**Answer:** In an integration test (`@SpringBootTest`), you can override properties using `@TestPropertySource` or the `properties` attribute (`@SpringBootTest(properties = "my.prop=test-value")`). For pure unit tests, use Spring's `ReflectionTestUtils.setField(targetObject, "propertyName", "value")`.

### Q20. What is `@ActiveProfiles("test")`? [Medium]
**Answer:** It forces the Spring context to load the `application-test.properties` (or `.yml`) file during the test execution, overriding the default application properties. This is crucial for pointing tests to a different database (like H2) instead of the production database.

---

## Advanced Level Questions

### Q21. Explain Testcontainers. Why are they better than H2 in-memory databases? [Hard]
**Answer:** Testcontainers is a Java library that spins up lightweight, throwaway instances of real databases, message brokers (Kafka), or anything that can run in a Docker container, purely for integration testing. 
*Why better?* H2 is not 100% compatible with MySQL/PostgreSQL (e.g., custom JSONB columns or specific functions). Testcontainers ensures your tests run against the *exact same database engine* you use in production, eliminating "works in H2 but fails in Prod" bugs.

### Q22. How do you use Testcontainers in a Spring Boot test? [Hard]
**Answer:** You use the `@Testcontainers` and `@Container` annotations. Spring Boot 3+ introduced `@ServiceConnection`, which automatically injects the dynamically generated Docker container's IP/Port into the Spring context (replacing the need for `@DynamicPropertySource`).

### Q23. What is `@RestClientTest`? [Hard]
**Answer:** It is a Spring Boot slice test used to test clients that use `RestTemplate` or `RestClient`. It auto-configures a `MockRestServiceServer`. You use this server to define expected outgoing HTTP requests and stub the JSON responses they should receive, allowing you to test your parsing logic without making real HTTP network calls.

### Q24. How do you mock a static method using Mockito? [Hard]
**Answer:** Starting with Mockito 3.4, you can use `mockStatic`. Because it manipulates classloaders, it must be used carefully within a `try-with-resources` block to ensure the mock is immediately deregistered after the test.
**Code Example:**
```java
try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class)) {
    mockedUuid.when(UUID::randomUUID).thenReturn(UUID.fromString("123-abc"));
    // execute test
}
```

### Q25. What is the difference between a Mock and a Spy (`@Spy`)? [Hard]
**Answer:**
* **Mock**: A completely fake object. By default, calling any method on a mock returns `null` (or 0/false) unless you explicitly stub it using `when()`.
* **Spy**: A partial mock. It wraps a *real* object. Calling a method on a spy actually executes the real method logic, unless you explicitly stub it. It's useful when you want to execute the real code but just `verify()` that it was called.

### Q26. Explain the `@Nested` annotation in JUnit 5. [Hard]
**Answer:** It allows you to group related test methods inside inner classes within a main test class. This is excellent for creating hierarchical tests, especially for organizing tests based on the state of the object (e.g., `class GivenValidUser`, `class GivenInvalidUser`) or specific methods.

### Q27. How do you test asynchronous methods (`@Async`)? [Hard]
**Answer:** You cannot use standard synchronous assertions because the `@Test` thread will finish before the background `@Async` thread completes. You must use the **Awaitility** library (`await().atMost(5, SECONDS).untilAsserted(() -> verify(...))`), or use `CompletableFuture.get()` if the method returns a Future.

### Q28. What is the `@ParameterizedTest` annotation? [Hard]
**Answer:** It allows you to run the exact same test method multiple times with different sets of inputs. You supply the inputs using `@ValueSource` (arrays of strings/ints), `@EnumSource`, or `@MethodSource` (which references a method that returns a Stream of arguments).

### Q29. How do you handle database state in Integration Tests to prevent tests from affecting each other? [Hard]
**Answer:**
1. Annotate the test class with `@Transactional`. Spring will wrap each test in a transaction and automatically roll it back at the end, leaving the DB clean.
2. If testing outside a transactional context (e.g., testing `WebTestClient` with an actual running Tomcat server `RANDOM_PORT`), you must use a tool like Flyway/Liquibase to clean/migrate the schema, or write an `@AfterEach` method to manually delete all data from the repositories.

### Q30. What is Contract Testing (Spring Cloud Contract)? [Hard]
**Answer:** In microservices, integration testing all services together is slow and brittle. Contract testing verifies that the API provider (e.g., User Service) and the consumer (e.g., Order Service) agree on the JSON format. You write a "Contract" (Groovy/YAML). Spring generates stubs from it. The Consumer tests against the stub. The Provider runs auto-generated tests to ensure its API actually fulfills the contract. It prevents one team from silently breaking the API for another team.
