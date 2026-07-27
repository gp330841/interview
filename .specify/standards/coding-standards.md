# Engineering & Coding Standards

This document defines mandatory coding standards for Java, Spring Boot, and system design implementations generated or edited within this workspace.

---

## 1. Core Design & SOLID Principles
- **Single Responsibility Principle (SRP)**: Each class must have exactly one reason to change.
- **Open/Closed Principle (OCP)**: Software entities must be open for extension, closed for modification.
- **Interface Segregation Principle (ISP)**: Create small, focused interfaces rather than monolithic ones.
- **Dependency Inversion Principle (DIP)**: Depend upon abstractions, not concrete implementations.

## 2. Concurrency & Thread-Safety Rules
1. **Thread-Safe Data Structures**: Prefer `ConcurrentHashMap`, `AtomicLong`, `AtomicReference`, `CopyOnWriteArrayList` over manually synchronized collection wrappers.
2. **Immutability**: Domain models and transfer objects should be `record` types or final immutable classes whenever possible.
3. **Locking Discipline**: Keep lock scopes as small as possible. Never invoke external or blocking calls inside synchronized blocks or locks.

## 3. Clean Code & Exception Handling
- **No Null Pointer Exceptions**: Validate parameter inputs using `Objects.requireNonNull()` or Spring `@NonNull`.
- **Custom Exception Hierarchy**: Throw explicit domain exceptions (`ResourceNotFoundException`, `RateLimitExceededException`) rather than generic `RuntimeException`.
- **Zero Placeholders**: Never leave `// TODO`, `// implementation here`, `null` dummy returns, or empty catch blocks in production code.

## 4. Logging & Diagnostics
- Use SLF4J (`@Slf4j` or `LoggerFactory.getLogger()`).
- Log structured parameters (`log.info("Processing orderId={}, userId={}", orderId, userId)`).
- Never log sensitive credentials, secret tokens, or full PII payloads.
