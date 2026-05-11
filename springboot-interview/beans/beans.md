# Spring Boot - Spring Beans & Lifecycle

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Application Caching Strategy**: A reporting service requires heavy data processing. By defining a bean with a `@Scope("prototype")`, you ensure that each time the report generator is requested, a fresh, clean instance is created so data doesn't leak between different user reports.
2. **Resource Initialization/Cleanup**: A database connection pool bean needs to open connections upon startup and gracefully close them when the application shuts down. Using `@PostConstruct` ensures connections are ready before traffic hits, and `@PreDestroy` ensures they are closed safely preventing resource leaks.

---

## Beginner Level Questions

### Q1. What is a Spring Bean? [Easy]
**Answer:** A Spring bean is an object that is instantiated, assembled, and managed by the Spring IoC (Inversion of Control) container. It is the backbone of a Spring application.

### Q2. How do you define a Spring Bean? [Easy]
**Answer:** You can define a bean using annotations like `@Component`, `@Service`, `@Repository`, or `@Controller` on a class. Alternatively, you can use the `@Bean` annotation on a method inside a `@Configuration` class.

### Q3. What is the default scope of a Spring Bean? [Easy]
**Answer:** The default scope is **Singleton**. This means the Spring container creates only one instance of the bean and caches it in memory. Every request for that bean returns the same shared instance.

### Q4. Name the different Bean Scopes available in Spring. [Easy]
**Answer:** 
1. Singleton (Default)
2. Prototype (New instance every time)
3. Request (One per HTTP request - Web only)
4. Session (One per HTTP session - Web only)
5. Application (One per ServletContext - Web only)
6. WebSocket (One per WebSocket - Web only)

### Q5. How do you change the scope of a Bean? [Easy]
**Answer:** Using the `@Scope` annotation.
**Code Example:**
```java
@Component
@Scope("prototype")
public class ReportGenerator { ... }
```

### Q6. What is the Bean Lifecycle in simple terms? [Easy]
**Answer:** 
1. Instantiation (Calling the constructor)
2. Populating properties (Dependency Injection)
3. Initialization (Custom setup methods)
4. Utility (Bean is ready for use)
5. Destruction (Cleanup methods when context closes)

### Q7. How can you execute custom logic immediately after a bean is initialized? [Easy]
**Answer:** By using the `@PostConstruct` annotation on a method. This method runs after the bean's constructor is called and all its dependencies have been injected.

### Q8. How can you execute custom logic right before a bean is destroyed? [Easy]
**Answer:** By using the `@PreDestroy` annotation. This is used to release resources, close file streams, or close database connections when the application shuts down.

### Q9. Are Singleton beans thread-safe? [Easy]
**Answer:** **No.** Spring does not do anything under the hood to make singletons thread-safe. If your singleton bean has mutable state (class-level variables that change), it will suffer from race conditions in a multi-threaded environment (like a web app).

### Q10. What is a `@Configuration` class? [Easy]
**Answer:** An annotation indicating that a class declares one or more `@Bean` methods. The Spring container processes these classes to generate bean definitions and manage the beans.

---

## Intermediate Level Questions

### Q11. Explain the difference between Singleton design pattern and Spring's Singleton Scope. [Medium]
**Answer:** The Gang of Four (GoF) Singleton pattern ensures that exactly one instance of a class exists *per ClassLoader* in the JVM. Spring's Singleton scope ensures that exactly one instance of the bean exists *per Spring IoC container*. If you have multiple IoC containers, you can have multiple instances of the same Spring Singleton class.

### Q12. What happens to `@PreDestroy` methods on Prototype beans? [Medium]
**Answer:** They are **never called**. Spring instantiates the prototype bean, injects dependencies, runs `@PostConstruct`, hands it to the client, and then completely forgets about it. The container does not track prototype beans, so it cannot destroy them. The client code must handle resource cleanup.

### Q13. Can you use `@Autowired` inside a `@PostConstruct` method? [Medium]
**Answer:** You cannot use it on the method signature (parameters), but you **can** use the autowired fields inside the method body. By the time `@PostConstruct` runs, Spring guarantees that all `@Autowired` dependencies have been fully injected and are ready to use.

### Q14. What are the three ways to define Initialization and Destruction callbacks? [Medium]
**Answer:**
1. Annotations: `@PostConstruct` and `@PreDestroy` (JSR-250).
2. Interfaces: Implementing `InitializingBean` (for `afterPropertiesSet()`) and `DisposableBean` (for `destroy()`).
3. Custom Methods: Using `@Bean(initMethod = "init", destroyMethod = "cleanup")` in a `@Configuration` class.

### Q15. In what order are Initialization callbacks executed? [Medium]
**Answer:**
1. Methods annotated with `@PostConstruct`.
2. `afterPropertiesSet()` as defined by the `InitializingBean` interface.
3. Custom `initMethod` configured via the `@Bean` annotation.

### Q16. What is the `ApplicationContextAware` interface used for? [Medium]
**Answer:** If a bean implements `ApplicationContextAware`, Spring will automatically call its `setApplicationContext(ApplicationContext context)` method during the bean lifecycle. This gives the bean direct access to the Spring container (IoC), which is useful if the bean needs to programmatically lookup other beans.

### Q17. What is `@DependsOn` and when would you use it? [Medium]
**Answer:** `@DependsOn` forces the Spring IoC container to initialize one or more specified beans *before* the bean annotated with `@DependsOn`. 
**Use Case:** Useful when Bean A relies on a side-effect (like a static variable or a database table creation) that happens during Bean B's initialization, even though Bean A doesn't directly `@Autowire` Bean B.

### Q18. How do you register a bean dynamically at runtime? [Medium]
**Answer:** By casting the `ApplicationContext` to a `BeanDefinitionRegistry` (or `GenericApplicationContext`) and calling `registerBeanDefinition()`.
**Code Example:**
```java
GenericApplicationContext context = (GenericApplicationContext) applicationContext;
context.registerBean(MyDynamicBean.class, () -> new MyDynamicBean("dynamic-value"));
```

### Q19. What is `@Conditional` and how does it affect bean creation? [Medium]
**Answer:** `@Conditional` allows you to define conditions that must be met for a bean to be created. For example, `@ConditionalOnProperty(name = "feature.x.enabled")` will only create the bean if that property is true in `application.properties`. If the condition fails, the bean is completely skipped during component scanning.

### Q20. What is the difference between `@Component` and `@Bean`? [Medium]
**Answer:** 
* `@Component` is a class-level annotation. It tells Spring to auto-detect the class during classpath scanning and turn it into a bean automatically.
* `@Bean` is a method-level annotation used inside `@Configuration` classes. It tells Spring to execute the method and register the returned object as a bean. It is used when you need to manually instantiate third-party classes or need complex setup logic.

---

## Advanced Level Questions

### Q21. Explain the role of `BeanPostProcessor` in the bean lifecycle. [Hard]
**Answer:** `BeanPostProcessor` (BPP) is an interface that allows custom modification of new bean instances. It provides two callbacks:
* `postProcessBeforeInitialization`: Runs before `@PostConstruct` and `afterPropertiesSet`.
* `postProcessAfterInitialization`: Runs after initialization methods. BPPs are responsible for creating AOP proxies. If a bean has `@Transactional`, the BPP wraps the raw bean in a proxy and returns the proxy to the container during this phase.

### Q22. Explain the role of `BeanFactoryPostProcessor` (BFPP). [Hard]
**Answer:** Unlike BPP which modifies bean *instances*, BFPP modifies bean *definitions* (the metadata recipes for creating beans) before any beans are instantiated. Spring uses BFPPs (like `PropertySourcesPlaceholderConfigurer`) to resolve `${...}` properties in `@Value` annotations before the beans are created.

### Q23. Detail the exact phases of the Spring Bean Lifecycle from start to finish. [Hard]
**Answer:** 
1. Load Bean Definitions (via Component Scan / @Configuration).
2. Execute `BeanFactoryPostProcessors`.
3. Instantiate the Bean (Constructor called).
4. Populate Properties (Dependency Injection).
5. `BeanNameAware` / `BeanFactoryAware` / `ApplicationContextAware` setter methods called.
6. `BeanPostProcessor.postProcessBeforeInitialization()`.
7. `@PostConstruct` methods.
8. `InitializingBean.afterPropertiesSet()`.
9. Custom `initMethod`.
10. `BeanPostProcessor.postProcessAfterInitialization()` (AOP Proxies created here).
11. Bean is ready for use.
12. Container shuts down.
13. `@PreDestroy` methods.
14. `DisposableBean.destroy()`.
15. Custom `destroyMethod`.

### Q24. How do you implement a custom Scope in Spring? [Hard]
**Answer:** 
1. Implement the `org.springframework.beans.factory.config.Scope` interface (implementing methods like `get`, `remove`, `registerDestructionCallback`).
2. Register the scope using a `CustomScopeConfigurer` bean or programmatically via `ConfigurableBeanFactory.registerScope(String scopeName, Scope scope)`.

### Q25. What is the `FactoryBean` interface and why is it used? [Hard]
**Answer:** A `FactoryBean` is a bean that serves as a factory for creating other beans within the Spring IoC container. Instead of returning the Factory instance itself, Spring calls `getObject()` on the factory and returns that result as the actual bean. It is heavily used internally by Spring for complex initialization (e.g., creating JPA `EntityManagerFactory` or Proxy objects). To get the factory itself instead of the object it creates, you prefix the bean name with an ampersand (`&myFactoryBean`).

### Q26. Why should you avoid using `ApplicationContextAware` if possible? [Hard]
**Answer:** Using it violates the principle of Inversion of Control (IoC). It couples your application code directly to the Spring Framework. Your class becomes aware of its container, making unit testing harder (you have to mock the `ApplicationContext`) and reducing code portability.

### Q27. What happens if a prototype bean is requested within a singleton bean using method injection? [Hard]
**Answer:** If you use standard `@Autowired`, the prototype is injected once when the singleton is created, effectively acting as a singleton. To solve this, you must use Method Injection (`@Lookup`). At runtime, Spring uses CGLIB to generate a subclass of your singleton bean that overrides the `@Lookup` method to fetch a new prototype instance from the container every time the method is called.

### Q28. Can a bean definition be overridden in Spring Boot? [Hard]
**Answer:** Yes, but since Spring Boot 2.1, bean overriding is disabled by default to prevent accidental overrides which cause obscure bugs. If two beans have the same name, the application will fail to start with a `BeanDefinitionOverrideException`. You can explicitly allow it by setting `spring.main.allow-bean-definition-overriding=true` in `application.properties`.

### Q29. How does CGLIB proxying work for `@Configuration` classes? [Hard]
**Answer:** By default, classes annotated with `@Configuration` are proxied by CGLIB. This ensures that calling a `@Bean` method directly from another `@Bean` method within the same configuration class does *not* instantiate a new object. The proxy intercepts the method call, checks the Spring container, and returns the cached singleton instance instead. You can disable this by setting `proxyBeanMethods = false` for performance optimization ("Lite mode").

### Q30. Explain what "Lite" Configuration mode is in Spring. [Hard]
**Answer:** If you annotate a class with `@Component` (instead of `@Configuration`) and define `@Bean` methods inside it, or if you use `@Configuration(proxyBeanMethods = false)`, Spring processes these beans in "Lite" mode. It means no CGLIB proxy is created for the class. Direct inter-bean method calls will result in standard Java behavior (creating new instances) rather than retrieving singletons from the IoC container. This improves startup time and reduces memory usage for microservices that don't need inter-bean method calls.
