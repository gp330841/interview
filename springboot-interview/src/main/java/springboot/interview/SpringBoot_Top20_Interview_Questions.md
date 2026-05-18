# Top 20 Spring Boot Interview Questions (With Code Examples)

This document covers the top 20 most frequently asked Spring Boot interview questions, providing both conceptual answers and concrete code examples.

---

### 1. What is the difference between Spring and Spring Boot?
**Spring** is a comprehensive framework for Java built on Dependency Injection (DI) and Inversion of Control (IoC). It provides a lot of flexibility but requires extensive boilerplate configuration (XML or Java config) to wire things up.
**Spring Boot** is an extension of the Spring framework intended to eliminate boilerplate. It provides **Auto-Configuration**, **Starter Packages**, and an **Embedded Web Server**.

### 2. How does Auto-Configuration work under the hood?
Auto-configuration reads `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` and evaluates `@Conditional` annotations to spin up default beans.

**Code Example (`@Conditional` usage):**
```java
@Configuration
public class MyAutoConfig {
    
    @Bean
    @ConditionalOnClass(name = "org.apache.kafka.clients.producer.KafkaProducer")
    @ConditionalOnMissingBean
    public CustomKafkaManager customKafkaManager() {
        // This bean is ONLY created if Kafka is on the classpath 
        // AND the user hasn't already defined a CustomKafkaManager bean.
        return new CustomKafkaManager();
    }
}
```

### 3. Explain the `@SpringBootApplication` annotation.
It is a convenience annotation that combines three crucial annotations: `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.

**Code Example:**
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InterviewApplication {
    public static void main(String[] args) {
        // Bootstraps the application, starts embedded Tomcat, creates IoC container
        SpringApplication.run(InterviewApplication.class, args);
    }
}
```

### 4. What are Spring Boot Starters?
Starters are a set of convenient dependency descriptors. Adding `spring-boot-starter-web` provides Spring MVC, Jackson, and embedded Tomcat immediately without needing to manage their individual versions.

### 5. `@Controller` vs `@RestController`?
- `@Controller`: Methods return a String view name to render HTML templates.
- `@RestController`: Represents `@Controller` + `@ResponseBody`. Return values are serialized to JSON.

**Code Example:**
```java
// Traditional MVC
@Controller
public class WebController {
    @GetMapping("/home")
    public String home() {
        return "home_page"; // Resolves to src/main/resources/templates/home_page.html
    }
}

// REST API
@RestController
public class ApiController {
    @GetMapping("/api/user")
    public User getUser() {
        return new User("John", 25); // Automatically converted to {"name":"John", "age":25}
    }
}
```

### 6. Explain `@ControllerAdvice` and Global Exception Handling
`@ControllerAdvice` allows you to handle exceptions across all controllers globally.

**Code Example:**
```java
@RestControllerAdvice // Includes @ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError().body("An unknown error occurred.");
    }
}
```

### 7. What is `@Transactional` and how does it deal with exceptions?
`@Transactional` wraps a method execution in a database transaction. 
**Crucial detail**: By default, it *only* rolls back for **RuntimeExceptions**.

**Code Example:**
```java
@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Transactional // Unchecked exceptions roll this back
    public void createUser(User user) {
        repo.save(user);
        if (user.getName() == null) {
            throw new IllegalArgumentException("Invalid Name"); // Rolls back the save!
        }
    }

    // Checked exceptions DO NOT roll back by default. Must specify rollbackFor.
    @Transactional(rollbackFor = IOException.class)
    public void uploadFile() throws IOException {
        repo.save(new FileMetadata("file.txt"));
        throw new IOException("Disk Full"); // Will now correctly roll back the save.
    }
}
```

### 8. Explain the different types of Dependency Injection.
**Code Example:**
```java
@Service
public class OrderService {

    // 1. Field Injection (Discouraged - Hard to test without Spring)
    // @Autowired
    // private PaymentService paymentService;

    private final PaymentService paymentService;

    // 2. Constructor Injection (Recommended - Allows immutability, easy mocking)
    @Autowired // Optional if class only has one constructor
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 3. Setter Injection (Used for optional dependencies)
    // @Autowired
    // public void setPaymentService(PaymentService paymentService) { ... }
}
```

### 9. What is the difference between `@Component`, `@Service`, `@Repository`, and `@Controller`?
- `@Component`: Generic bean.
- `@Service`: Marker for business logic.
- `@Repository`: DAOs. Translates raw DB exceptions into `DataAccessException`.
- `@Controller`: Web layer routing.

### 10. Spring Data JPA: `CrudRepository` vs `JpaRepository`?
- `CrudRepository`: Provides generic basic CRUD.
- `JpaRepository`: Adds batch operations, flushing, and List returns.

**Code Example:**
```java
// Spring automatically provides the implementation at runtime!
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Custom query generation based purely on method name!
    List<User> findByAgeGreaterThanAndNameStartingWith(int age, String prefix);
}
```

### 11. What is Spring Boot Actuator?
Actuator provides production-ready endpoints (`/actuator/health`, `/actuator/metrics`) to monitor and manage your application.

### 12. Externalizing properties directly and safely.
**Code Example (`@ConfigurationProperties`):**
```yaml
# application.yml
app:
  mail:
    host: smtp.google.com
    port: 587
    credentials:
      username: admin
      password: secretPassword
```
```java
@Component
@ConfigurationProperties(prefix = "app.mail")
public class MailProperties {
    private String host;
    private int port;
    private Map<String, String> credentials;
    
    // Standard Getters and Setters must exist for Spring to bind the YAML to this POJO
    // Or use @ConstructorBinding in modern Spring Boot versions
}
```

### 13. What are Spring Profiles?
Profiles segregate environments. 
Activate via `java -jar app.jar --spring.profiles.active=prod`.

**Code Example:**
```java
@Service
@Profile("dev")
public class DummyPaymentProvider implements PaymentProvider {
    // Only created when 'dev' profile is active
}

@Service
@Profile("prod")
public class StripePaymentProvider implements PaymentProvider {
    // Only created when 'prod' profile is active
}
```

### 14. `@Bean` vs `@Component`?
- `@Component` sits directly on a class definition you wrote.
- `@Bean` is used inside configuration classes to instantiate library code where you can't add an `@Component` annotation.

**Code Example:**
```java
@Configuration
public class SecurityConfig {

    @Bean // Telling Spring "Hey, I made this PasswordEncoder, please manage it"
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 15. What is `@Qualifier`?
Resolves ambiguities when multiple beans of the same type exist.

**Code Example:**
```java
@Service("pdfFormatter")
class PdfFormatter implements Formatter {}

@Service("wordFormatter")
class WordFormatter implements Formatter {}

@Service
class ReportGenerator {
    private final Formatter formatter;

    public ReportGenerator(@Qualifier("pdfFormatter") Formatter formatter) {
        this.formatter = formatter; // Explicitly selects PdfFormatter
    }
}
```

### 16. Explain the Bean Lifecycle.
1. Instantiation
2. Populate Properties
3. Pre-initialization (`@PostConstruct`)
4. Post-initialization
5. Destruction (`@PreDestroy`)

**Code Example:**
```java
@Service
public class LifecycleDemo {
    @PostConstruct
    public void init() {
        System.out.println("Runs right after DI completes and before bean is given to the app.");
    }
    
    @PreDestroy
    public void cleanup() {
        System.out.println("Runs right before context shuts down.");
    }
}
```

### 17. How does Spring Boot handle cross-origin requests (CORS)?
**Code Example:**
```java
@RestController
@CrossOrigin(origins = "http://localhost:3000") // Allow specific frontend origins
public class ApiController {
    @GetMapping("/data")
    public String getData() { return "Secure Data"; }
}
```

### 18. What is the use of `META-INF/spring-configuration-metadata.json`?
It provides IDE tooltips for your custom `@ConfigurationProperties`, generated by adding the `spring-boot-configuration-processor` dependency in `pom.xml`.

### 19. How do you configure Multiple DataSources?
You define separate configurations, specifically marking one as `@Primary`.

**Code Example:**
```java
@Configuration
@EnableJpaRepositories(
    basePackages = "com.app.primary.repo",
    entityManagerFactoryRef = "primaryEntityManager",
    transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDatabaseConfig {

    @Primary // MANDATORY to declare which Db takes precedence in ambiguous cases
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    // Also define primaryEntityManager and primaryTransactionManager...
}
```

### 20. What is Constructor Binding in `@ConfigurationProperties`?
It allows configuration settings to be bound directly into `final` fields, maximizing safety.

**Code Example:**
```java
@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(String jwtSecret, int tokenExpirationDays) {
    // Automatically populated via the canonical constructor in Java Records
    // Completely immutable representing safe configurations!
}
```
