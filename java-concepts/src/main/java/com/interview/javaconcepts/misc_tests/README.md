# Testing & Miscellaneous Concepts in Java

This module houses practice test files, basic validation routines, and general practice suites to test your Java knowledge.

---

## 🌟 Core Concepts

Testing is a fundamental component of reliable software engineering. In Java, tests are typically written using unit testing frameworks such as **JUnit** (JUnit 4 or JUnit 5/Jupiter) and assertion libraries like **AssertJ** or **Hamcrest**.

---

## ❓ Frequently Asked Interview Questions

### Q1: What is the difference between `@BeforeEach` and `@BeforeAll` in JUnit 5?
- **`@BeforeEach`**: Runs before *every* test method in the class. Useful to reset test state or re-instantiate shared resources.
- **`@BeforeAll`**: Runs *once* before all test methods in the class. The annotated method must be static. Useful to run expensive database setups or containers.

---

### Q2: What is the difference between Unit Testing and Integration Testing?
- **Unit Testing**: Validates a small unit of code in isolation (typically a single class or method). Dependencies are mocked (e.g., using Mockito). They are fast.
- **Integration Testing**: Validates how multiple units, modules, or systems interact together. Involves real databases, networks, or microservices (e.g., Spring Boot `@SpringBootTest`). They are slower but offer higher confidence.

---

## 🛠️ Code Examples
- **[JavaBasicsTest.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/misc_tests/JavaBasicsTest.java)**: Educational test runner showcasing basic loop controls and arithmetic statements.
- **[PracticeTest.java](file:///Users/yogeshwarpatel/Workspace/interview/java-concepts/src/main/java/com/interview/javaconcepts/misc_tests/PracticeTest.java)**: Playground class used for custom experimentation.
