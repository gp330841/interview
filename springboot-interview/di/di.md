# Spring Boot - Dependency Injection (DI)

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Payment Gateway Integration**: An e-commerce app requires processing payments. Instead of tightly coupling the checkout service to a `StripePaymentProcessor`, you inject an interface `PaymentProcessor`. Depending on the environment (e.g., test vs. prod) or the user's choice, Spring DI injects the concrete implementation (Stripe, PayPal, or a Mock processor) at runtime.
2. **Notification System**: A user registration service needs to send a welcome message. Using DI, a `NotificationSender` interface is injected. The actual implementation could be `EmailSender` or `SmsSender`, chosen based on application properties via `@ConditionalOnProperty`. This keeps the registration service oblivious to the delivery mechanism.

---

## Beginner Level Questions

### Q1. What is Inversion of Control (IoC)? [Easy]
**Answer:** IoC is a design principle where the control flow of a program is inverted: instead of the programmer controlling the flow by manually instantiating dependencies, a framework (like Spring) takes control of object creation, configuration, and lifecycle.
**Key Points:**
* Reverses traditional control flow.
* Decouples execution from implementation.

### Q2. What is Dependency Injection (DI)? [Easy]
**Answer:** DI is a specific implementation of IoC. It is a design pattern where an object receives its dependencies from an external source (the Spring IoC container) rather than creating them itself.
**Key Points:**
* Helps achieve loose coupling.
* Makes code more testable.

### Q3. What are the types of Dependency Injection in Spring? [Easy]
**Answer:** There are three main types: Constructor Injection (passing dependencies through the constructor), Setter Injection (passing dependencies via setter methods), and Field Injection (using `@Autowired` directly on fields).
**Key Points:**
* Constructor Injection is recommended for mandatory dependencies.
* Setter Injection is for optional dependencies.
* Field Injection is discouraged due to testability issues.

### Q4. Why is Constructor Injection recommended over Field Injection? [Easy]
**Answer:** Constructor injection ensures that an object is fully initialized and in a valid state before it's used. It makes dependencies explicit, makes writing unit tests easier (no need for reflection or Spring context), and allows fields to be marked as `final`.
**Code Example:**
```java
@Service
public class OrderService {
    private final PaymentService paymentService; // Can be final!

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

### Q5. What does the `@Autowired` annotation do? [Easy]
**Answer:** It tells the Spring IoC container to automatically resolve and inject a collaborating bean into the annotated field, setter, or constructor.
**Key Points:**
* Default injection is by Type.
* Since Spring 4.3, `@Autowired` is optional on constructors if there is only one constructor in the class.

### Q6. What happens if multiple beans of the same type exist and you use `@Autowired`? [Easy]
**Answer:** Spring will throw a `NoUniqueBeanDefinitionException` because it doesn't know which specific bean to inject.
**Key Points:**
* Resolved using `@Qualifier` or `@Primary`.

### Q7. What is the role of the `@Qualifier` annotation? [Easy]
**Answer:** It is used alongside `@Autowired` to specify the exact bean name that should be injected when multiple beans of the same type exist in the ApplicationContext.
**Code Example:**
```java
@Autowired
@Qualifier("stripePaymentService")
private PaymentService paymentService;
```

### Q8. What does `@Primary` do? [Easy]
**Answer:** It indicates that a bean should be given preference when multiple candidates are qualified to autowire a single-valued dependency. If exactly one 'primary' bean exists among the candidates, it will be the autowired value.

### Q9. What is a Spring Bean? [Easy]
**Answer:** An object that is instantiated, assembled, and otherwise managed by a Spring IoC container. 

### Q10. Can you autowire a bean that doesn't exist? [Easy]
**Answer:** By default, no. Spring will throw a `NoSuchBeanDefinitionException`. However, you can make the injection optional by using `@Autowired(required = false)`.

---

## Intermediate Level Questions

### Q11. Explain the difference between `@Component`, `@Service`, `@Repository`, and `@Controller`. [Medium]
**Answer:** `@Component` is a generic stereotype for any Spring-managed component. `@Service`, `@Repository`, and `@Controller` are specializations of `@Component` for specific use cases.
* `@Repository`: Used for Data Access Objects (DAOs). It enables automatic translation of database exceptions into Spring's `DataAccessException`.
* `@Service`: Used for business logic layers. Currently, it acts as a marker but might get specific behavior in future Spring versions.
* `@Controller`: Used for presentation layer (MVC). Tells Spring this class serves web requests.

### Q12. How does Spring resolve dependencies internally? [Medium]
**Answer:** Spring uses a three-step process: 
1. **Type matching:** It looks for beans of the exact type. 
2. **Qualifier matching:** If multiple types exist, it looks for `@Qualifier` annotations. 
3. **Name matching:** If no qualifier is found, it falls back to matching the bean name with the property/field name.

### Q13. What is the difference between ApplicationContext and BeanFactory? [Medium]
**Answer:** `BeanFactory` is the basic container interface providing basic IoC and DI features using lazy initialization. `ApplicationContext` is an advanced container extending `BeanFactory`. It provides eager initialization (for singletons), event publishing, internationalization (i18n), and enterprise-specific functionality. You almost always use `ApplicationContext` in Spring Boot.

### Q14. What is the `@Value` annotation used for? [Medium]
**Answer:** It is used to inject values from property files, OS environment variables, or default values directly into bean fields or method parameters.
**Code Example:**
```java
@Value("${app.max-retries:3}")
private int maxRetries;
```

### Q15. Can you inject a prototype bean into a singleton bean? What is the problem? [Medium]
**Answer:** Yes, but it causes the **Scoped Proxy Problem**. Because the singleton bean is instantiated only once, its dependencies are also injected only once. Thus, the prototype bean injected into it will effectively act as a singleton; you won't get a new instance of the prototype every time you call a method on the singleton.

### Q16. How do you solve the Scoped Proxy Problem (Singleton depending on Prototype)? [Medium]
**Answer:** There are three main ways:
1. Use `@Lookup` method injection.
2. Inject `ApplicationContext` and manually call `getBean()` (violates IoC).
3. Inject `ObjectFactory<MyPrototypeBean>` or `Provider<MyPrototypeBean>` and call `.getObject()`.
**Code Example (Lookup):**
```java
@Service
public abstract class SingletonService {
    @Lookup
    public abstract PrototypeBean getPrototypeBean();
    
    public void doWork() {
        PrototypeBean bean = getPrototypeBean(); // Returns new instance every time
    }
}
```

### Q17. What does the `@Lazy` annotation do in DI? [Medium]
**Answer:** By default, Spring eagerly instantiates all singleton beans at startup. `@Lazy` delays the initialization of the bean until it is first requested (either directly or injected into another bean). It's useful for resolving circular dependencies or speeding up startup time for heavy beans rarely used.

### Q18. How can you inject a list or map of beans? [Medium]
**Answer:** If you have an interface with multiple implementations, you can autowire a `List` or `Map` of that interface. Spring will automatically find all beans implementing the interface and inject them into the collection.
**Code Example:**
```java
@Autowired
private List<PaymentStrategy> paymentStrategies;

@Autowired
private Map<String, PaymentStrategy> strategyMap; // Bean name becomes the map key
```

### Q19. What is `@Bean` used for? [Medium]
**Answer:** It is a method-level annotation used within `@Configuration` classes. It tells Spring that the method returns an object that should be registered as a bean in the Spring application context. It is primarily used when you need to configure third-party classes (where you can't add `@Component`) or need complex initialization logic.

### Q20. What is circular dependency and how does Spring handle it? [Medium]
**Answer:** Circular dependency occurs when Bean A depends on Bean B, and Bean B depends on Bean A. 
* With **Constructor Injection**, Spring cannot resolve it and throws a `BeanCurrentlyInCreationException` immediately at startup.
* With **Setter/Field Injection**, Spring historically solved it using a three-level cache mechanism (early references). However, since Spring Boot 2.6, circular dependencies are prohibited by default and will fail unless explicitly allowed via `spring.main.allow-circular-references=true`.

---

## Advanced Level Questions

### Q21. Explain how the Spring three-level cache works for resolving circular dependencies. [Hard]
**Answer:** For singleton beans with setter/field injection, Spring uses three caches (maps):
1. `singletonObjects`: Fully initialized beans ready for use.
2. `earlySingletonObjects`: Partially initialized beans (instantiated but properties not yet injected).
3. `singletonFactories`: Object factories that can create an early reference to the bean.
When Bean A needs Bean B, and B needs A: A is instantiated, an early reference factory is put in cache 3. A requests B. B is instantiated, B requests A. B gets the early reference of A from cache 3 (moving it to cache 2). B finishes initialization and goes to cache 1. Finally, A gets B and finishes initialization.

### Q22. What are the differences between `@Autowired`, `@Inject`, and `@Resource`? [Hard]
**Answer:** 
* `@Autowired`: Spring-specific. Injects by Type first, then Name.
* `@Inject`: JSR-330 (Java DI standard). Functionally identical to `@Autowired`. Requires `javax.inject` dependency.
* `@Resource`: JSR-250 (Java standard). Injects by Name first, then Type. Useful when you have multiple beans of the same type and prefer name-based resolution without using `@Qualifier`.

### Q23. How do you conditionally create and inject beans? [Hard]
**Answer:** Using `@Conditional` annotations introduced in Spring Boot. Examples include:
* `@ConditionalOnProperty`: Bean created only if a specific property exists in `application.properties`.
* `@ConditionalOnClass`: Bean created only if a specific class is present on the classpath.
* `@ConditionalOnMissingBean`: Bean created only if no other bean of this type exists (used heavily in auto-configuration).
* `@Profile`: Bean created only if a specific profile (e.g., 'dev' or 'prod') is active.

### Q24. How does Spring AOP interfere with Dependency Injection? [Hard]
**Answer:** When a bean is wrapped in an AOP proxy (e.g., due to `@Transactional` or `@Async`), Spring injects the **Proxy object**, not the actual target instance. This means:
* You must inject interfaces rather than concrete classes if JDK Dynamic Proxies are used.
* "Self-invocation" (calling a method from within the same class) will bypass the proxy, meaning the AOP logic (like transaction management) will not be executed.

### Q25. Can you inject a bean dynamically at runtime based on a request parameter? [Hard]
**Answer:** Standard `@Autowired` happens at application startup. To get a bean dynamically per request, you cannot use field injection of a singleton. Instead, you inject the `ApplicationContext` (or `BeanFactory`) and call `context.getBean(beanName)` inside your request handling method.

### Q26. Explain the `BeanFactoryPostProcessor` and its role in DI. [Hard]
**Answer:** `BeanFactoryPostProcessor` is an interface that allows custom modification of an application context's bean definitions (the "recipes" for creating beans) *before* the beans are actually instantiated. `PropertySourcesPlaceholderConfigurer` is a classic example; it replaces `${...}` placeholders in bean definitions with actual values from properties files before the bean instances are created.

### Q27. Explain the `BeanPostProcessor` and its role in DI. [Hard]
**Answer:** `BeanPostProcessor` operates on bean *instances*. It provides callbacks (`postProcessBeforeInitialization` and `postProcessAfterInitialization`) allowing you to modify the bean instance immediately after Spring instantiates it and resolves dependencies. This is how Spring applies AOP proxies—by wrapping the raw bean in a proxy during the `postProcessAfterInitialization` phase.

### Q28. How does `@ConfigurationProperties` differ from `@Value` for injection? [Hard]
**Answer:** 
* `@Value`: Injects a single property. Harder to manage for large sets of properties. Does not support relaxed binding easily.
* `@ConfigurationProperties`: Binds an entire prefix of properties to a strongly-typed Java POJO. Supports hierarchical configuration, relaxed binding (e.g., `my-property` maps to `myProperty`), and validation via JSR-303 (`@Valid`). It is the recommended approach for structured configuration in Spring Boot.

### Q29. What happens if you try to Constructor Inject a bean into a class not managed by Spring? [Hard]
**Answer:** Spring's IoC container only manages beans it knows about. If you use `new MyClass()`, Spring is entirely unaware of the object creation, and no dependency injection will occur. The constructor arguments must be provided manually. If you desperately need injection in an unmanaged object, you must use AspectJ load-time weaving with `@Configurable`.

### Q30. Why does Spring Boot 2.6+ disable circular dependencies by default? [Hard]
**Answer:** Circular dependencies are generally a symptom of poor architectural design (violating the Single Responsibility Principle). Disabling them forces developers to refactor tightly coupled code into smaller, independent components or use events to decouple them, leading to cleaner, more maintainable codebases. It also simplifies the internal Spring container logic and improves startup performance slightly.
