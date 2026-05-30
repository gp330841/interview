# Spring Boot Mastery & Interview Preparation

Welcome to the **Spring Boot Mastery Module**. This repository serves a dual purpose: 
1. **Interview Preparation**: It contains in-depth theoretical notes and code mockups for top Spring Boot interview concepts.
2. **Working Application**: It physically integrates all those theoretical concepts into a living, executable **Order Processing System**.

## 🚀 How to Run the Application

This module is a fully working Spring Boot 3.2 application running on Java 21.

1. Open a terminal in the root of the workspace.
2. Run the application using Maven:
   ```bash
   mvn clean spring-boot:run -pl springboot
   ```
3. Wait for the application to start. It uses an in-memory **H2 Database**, so no external database setup is required.
4. Try accessing some endpoints:
   - DI Demo: `curl -u admin:admin http://localhost:8080/api/di/demo`
   - Users REST API: `curl -u admin:admin http://localhost:8080/api/users`

> **Note**: A basic Spring Security configuration is present. Use `admin` / `admin` for credentials.
> **Note**: Kafka listener auto-startup is disabled by default to prevent connection spam if you aren't running a local Kafka broker.

---

## 📚 Concepts Guide

We have divided the Spring Boot ecosystem into 16 distinct technical domains. Each domain contains:
- **Theory (`*.md` / `*QA.java`)**: The raw interview questions, explanations, and mock code.
- **Implementation (`components/`)**: The actual working classes wired into the Order Processing System.

Click on any concept below to explore its directory:

### Core Framework & Beans
1. **[Dependency Injection (DI)](src/main/java/springboot/interview/di)** 
   - Explores `@Autowired`, Constructor/Setter injection, `@Qualifier`, `@Primary`, `@Lazy`, and `@Lookup`.
2. **[Bean Lifecycles](src/main/java/springboot/interview/beans)** 
   - Demonstrates Bean Scopes, `BeanPostProcessor`, `@PostConstruct`, and `DisposableBean`.

### Data Access Layer
3. **[Spring Data JPA](src/main/java/springboot/interview/jpa)** 
   - Covers Entities, Relationships (`@OneToMany`, `@ManyToOne`), and Spring Data Repositories.
   - *Note: Unified models are located in [domain](src/main/java/springboot/interview/domain).*
4. **[Hibernate & Caching](src/main/java/springboot/interview/hibernate)** 
   - Covers Hibernate Sessions, L1/L2 Cache, Interceptors, and optimistic locking (`@Version`).
5. **[Transactions](src/main/java/springboot/interview/transactions)** 
   - Demonstrates `@Transactional` propagation, isolation, rollback rules, and `@TransactionalEventListener`.

### Web & REST
6. **[Spring Web MVC](src/main/java/springboot/interview/mvc)** 
   - Demonstrates MVC architecture, `@Controller`, and request mapping.
7. **[REST APIs](src/main/java/springboot/interview/rest)** 
   - Explores `@RestController`, HTTP verbs, and data serialization.
8. **[Exception Handling](src/main/java/springboot/interview/exceptions)** 
   - Covers global error handling with `@ControllerAdvice` and `@ExceptionHandler`.

### Advanced & Enterprise Features
9. **[Aspect-Oriented Programming (AOP)](src/main/java/springboot/interview/aop)** 
   - Demonstrates `@Aspect`, `@Around` pointcuts, and method execution interception.
10. **[Spring Security](src/main/java/springboot/interview/security)** 
    - Explains Authentication, Authorization, CSRF, CORS, and JWT.
11. **[Microservices & Feign](src/main/java/springboot/interview/microservices)** 
    - Demonstrates `@FeignClient` declarations and Fallback resilience.
12. **[Caching (Redis/In-Memory)](src/main/java/springboot/interview/cache)** 
    - Covers `@Cacheable`, `@CachePut`, and `@CacheEvict`.
13. **[Kafka Messaging](src/main/java/springboot/interview/kafka)** 
    - Demonstrates `KafkaTemplate` producers and `@KafkaListener` consumers.

### DevOps & Testing
14. **[Actuator](src/main/java/springboot/interview/actuator)** 
    - Covers management endpoints and creating a custom `HealthIndicator`.
15. **[Observability & Logging](src/main/java/springboot/interview/observability)** 
    - Demonstrates passing correlation IDs using SLF4J MDC (Mapped Diagnostic Context).
16. **[Testing](src/main/java/springboot/interview/testing)** 
    - Covers `@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest`, and Mockito.
17. **[Docker & Orchestration (Multi-Stage Layering)](DOCKER_README.md)** 
    - Comprehensive guide to building optimized JRE 25 images, multi-stage JAR layering caching, and Zookeeper-less KRaft Kafka + Redis orchestration.

---

## 🏗️ Project Architecture

To ensure the application compiles and runs without the theoretical "Mock" classes causing `DuplicateMappingException` or `UnsatisfiedDependencyException` crashes, we designed a specific component scanning boundary:

*   **`springboot.interview.domain.*`**: Contains all unified JPA Entities and Repositories.
*   **`springboot.interview.*.components.*`**: Contains all active, working services, controllers, and components that make up the Order Processing System.
*   **`*QA.java` Classes**: These classes contain mock nested classes for educational purposes. They are explicitly excluded via a Regex filter in `SpringBootInterviewApplication.java`.

This strict separation guarantees that the theoretical examples and the functional architecture can coexist in harmony!
