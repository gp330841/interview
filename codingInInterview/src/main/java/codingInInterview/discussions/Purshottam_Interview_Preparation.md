# Purshottam Interview Experience — Java Backend Preparation

This document consolidates the interview questions and preparation answers across Java, Spring Boot, design patterns, SQL performance, Agile, CI/CD, production monitoring, and team leadership.

> **Preparation note:** Treat these as model answers. Adjust versions, tools, project details, and examples to match your actual experience. Do not claim to have used a tool or pattern unless you can explain how you used it.

---

## 1. Java and Spring Boot

### Q1. How would you upgrade an application to the latest Java and Spring Boot versions?

**Interview answer**

I would approach the upgrade incrementally rather than upgrading Java and Spring Boot simultaneously without validating compatibility.

1. Assess the current Java and Spring Boot versions, dependencies, build plugins, and third-party libraries.
2. Review the target Spring Boot version's supported Java versions and migration guide.
3. Upgrade the Java and build toolchain, then update Spring Boot and compatible dependencies.
4. Resolve deprecated APIs, package changes, configuration changes, and dependency conflicts.
5. Run unit, integration, regression, and performance tests.
6. Deploy to a lower environment, validate metrics and logs, and then roll out gradually to production with a rollback plan.

For example, moving from Java 8 to Java 17 may also involve upgrading Spring Boot, Hibernate, testing libraries, and database drivers.

**Important compatibility notes**

- Spring Boot 2.7 supports Java 17.
- Spring Boot 3 requires Java 17 or later.
- Spring Boot 3 uses Spring Framework 6 and migrates relevant Java EE APIs from `javax.*` to `jakarta.*`.
- Check the compatibility matrix for the exact target versions rather than assuming every latest version works with every dependency.

### Q2. Java 8 vs Java 11 vs Java 17

| Feature | Java 8 | Java 11 | Java 17 |
|---|---|---|---|
| Release type | LTS | LTS | LTS |
| Lambda expressions | Introduced | Supported | Supported |
| Stream API | Introduced | Supported | Supported |
| Standard HTTP Client | No | Yes (`java.net.http`) | Yes |
| Local variable type inference (`var`) | No | Yes | Yes |
| Text blocks | No | No | Yes |
| Records | No | No | Yes |
| Sealed classes | No | No | Yes |
| Garbage collectors | G1 available | G1 default; ZGC experimental | G1 default; production ZGC available |

This table highlights major milestones, not every feature. Some features were available in preview form before being finalized.

**Why upgrade from Java 8 to Java 17?**

- Access newer language features such as records, text blocks, and sealed classes.
- Benefit from JVM and garbage-collection improvements.
- Receive security and maintenance updates through a supported runtime.
- Improve developer productivity and maintainability.
- Use modern Spring Boot versions and their ecosystem.
- Reduce long-term technical debt from legacy dependencies.

### Q3. Why do we need to upgrade Spring Boot when moving to Java 17?

We do not always need to upgrade Spring Boot merely because Java is upgraded. The target framework version must support the selected Java runtime.

For example:

- Spring Boot 2.7 supports Java 17.
- Spring Boot 3.x requires Java 17 or later.
- Spring Boot 3 introduces Spring Framework 6 and Jakarta namespace migration.

If an application remains on an older framework version, it may miss security fixes, compatibility improvements, and ecosystem support. The right decision depends on the existing framework version, support policy, and application dependencies.

### Q4. Why does Java introduce new features?

Java introduces features to solve practical development problems while preserving backward compatibility as much as possible.

Examples:

- **Lambda expressions and Streams:** More expressive data processing and functional-style programming.
- **Records:** Concise data-carrier classes.
- **Text blocks:** Easier handling of multiline SQL, JSON, and other text.
- **Virtual threads (Java 21):** More scalable thread-per-request programming for suitable I/O-heavy workloads.
- **Pattern matching:** Less boilerplate when checking and extracting types.

A good interview point: new features should be adopted when they improve readability, maintainability, performance, or developer productivity—not just because they exist.

### Q5. Which JUnit version do you use, and why?

The original question may mean **JUnit** when it says “Which unit used in project?”

A suitable answer, if the project uses JUnit 5:

> I use JUnit 5 with Mockito for unit testing Java and Spring Boot applications. JUnit 5 provides a flexible testing model, parameterized tests, and extensions, while Mockito helps isolate the unit under test by mocking external dependencies.

Common choices:

- **JUnit 5 / JUnit Jupiter:** Test cases.
- **Mockito:** Mocking dependencies.
- **`@WebMvcTest`:** MVC/controller slice tests.
- **`@DataJpaTest`:** JPA repository slice tests.
- **`@SpringBootTest`:** Broader application integration tests.
- **Testcontainers:** Integration testing against real infrastructure such as databases or Kafka.

Use the actual versions and libraries from your project when answering.

### Q6. What is Spring Batch? When would you use it?

Spring Batch is a framework for processing large volumes of data through reliable, restartable batch jobs.

Its main concepts are:

- **Job:** The complete batch process.
- **Step:** An individual stage within a job.
- **ItemReader:** Reads input data.
- **ItemProcessor:** Validates or transforms data.
- **ItemWriter:** Writes processed data.
- **JobRepository:** Stores job and step execution metadata.
- **Chunk processing:** Reads and processes a group of items, then writes them within a transaction.

Example: processing one million payment records from a database, validating each record, and writing results in chunks of 500.

```java
@Bean
Step paymentStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<Payment> reader,
        ItemProcessor<Payment, Payment> processor,
        ItemWriter<Payment> writer) {

    return new StepBuilder("paymentStep", jobRepository)
            .<Payment, Payment>chunk(500, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
}
```

**Why use Spring Batch instead of a simple scheduler?**

It provides execution metadata, restartability, chunk transactions, skip/retry mechanisms, and operational visibility. A scheduler triggers work; Spring Batch manages the batch-processing lifecycle.

---

## 2. Design Patterns

### Q7. What are the four main categories of design patterns?

The classic Gang of Four (GoF) patterns are grouped into **three categories, not four**.

| Category | Purpose | Examples |
|---|---|---|
| Creational | Object creation | Singleton, Factory Method, Builder, Abstract Factory, Prototype |
| Structural | Object composition | Adapter, Decorator, Facade, Proxy, Composite, Bridge, Flyweight |
| Behavioral | Object interaction | Strategy, Observer, Template Method, Command, State, Chain of Responsibility, Iterator, Mediator, Memento, Visitor, Interpreter |

If an interviewer explicitly asks for four categories, clarify whether they mean another classification system. The classic GoF classification has 23 patterns across three categories.

### Q8. Which design patterns have you used most frequently?

A suitable answer for a Spring Boot backend developer:

1. **Builder:** Constructing objects with many optional fields, such as DTOs or configuration objects.
2. **Strategy:** Supporting different business rules or processing algorithms behind a common interface.
3. **Factory:** Centralizing object creation when the concrete implementation depends on input or configuration.
4. **Singleton:** Sharing a single instance of a stateless service or component.
5. **Template Method:** Reusing a fixed processing workflow while allowing specific steps to vary.
6. **Chain of Responsibility:** Processing a request through a sequence of handlers or filters.
7. **Proxy:** Intercepting method calls for transactions, security, or lazy loading.

Choose only patterns you can explain with an actual example from your project.

### Q9. Which design patterns are used in Spring?

- **Singleton:** Spring beans are singleton-scoped by default—one instance per bean definition per application container.
- **Factory:** `BeanFactory` and `ApplicationContext` provide mechanisms for creating and managing beans.
- **Proxy:** Spring AOP uses proxies for concerns such as transaction management and method security.
- **Template Method:** `JdbcTemplate` and `RestTemplate` encapsulate common workflows while reducing repetitive infrastructure code.
- **Strategy:** Different implementations of an interface can be injected and selected according to business requirements.
- **Front Controller:** Spring MVC's `DispatcherServlet` centrally dispatches incoming requests to the appropriate handlers.
- **Observer:** Application events support publisher/listener interactions.
- **Adapter:** Adapter-style integrations help connect different interfaces.

Some examples are framework implementation patterns or architectural concepts rather than explicit GoF implementations.

---

## 3. SQL and Database Performance

### Q10. How would you investigate a slow SELECT query?

**Interview answer**

I would start with evidence rather than immediately adding an index.

1. **Reproduce the problem:** Identify the query, parameters, execution frequency, response time, and expected latency.
2. **Inspect the execution plan:** Look for table scans, expensive joins, sorts, key lookups, incorrect cardinality estimates, and expensive operators.
3. **Check indexes:** Verify whether existing indexes match filtering, joining, and ordering conditions.
4. **Inspect statistics:** Outdated statistics can lead to poor execution plans.
5. **Measure I/O and CPU:** In SQL Server, inspect logical reads, CPU time, execution duration, and Query Store history.
6. **Review query design:** Check unnecessary columns, functions on indexed columns, implicit conversions, correlated subqueries, and N+1 query patterns.
7. **Check concurrency:** Investigate blocking, deadlocks, lock waits, and long-running transactions.
8. **Validate the fix:** Compare execution plans, logical reads, CPU, and latency before and after the change.

Example:

```sql
-- Often less index-friendly
SELECT *
FROM Orders
WHERE YEAR(OrderDate) = 2026;

-- Prefer a range predicate
SELECT OrderId, OrderDate, CustomerId
FROM Orders
WHERE OrderDate >= '20260101'
  AND OrderDate <  '20270101';
```

An index on `OrderDate` can be more effectively used with the second query, assuming the index and execution plan are otherwise appropriate.

### Q11. Which indexes have you used?

Explain each index according to its purpose rather than simply listing types.

| Index type | Typical use |
|---|---|
| Clustered | Defines the row storage order in a SQL Server clustered table |
| Non-clustered | Speeds up lookups without defining the table's clustered storage order |
| Composite | Supports queries filtering on multiple columns |
| Covering | Includes all required query columns to reduce additional lookups |
| Unique | Enforces uniqueness and can improve lookup performance |
| Filtered | Indexes only rows matching a particular condition |

Example for a notification audit table:

```sql
CREATE NONCLUSTERED INDEX IX_NotificationAudit_Status_NextRetry
ON NotificationAudit (Status, NextRetryAt)
INCLUDE (NotificationAuditId, RetryCount);
```

This may help a scheduler find eligible notification records. The final index should match the real query predicates, ordering, selectivity, and workload.

### Q12. Clustered vs non-clustered index

| Clustered index | Non-clustered index |
|---|---|
| Determines the table's row-storage order | Maintains a separate index structure |
| Normally one per table | Multiple per table are possible |
| Leaf level contains the table rows | Leaf level contains index keys and row locators, possibly included columns |
| Efficient for appropriate range scans | Efficient for selective lookups and covering queries |
| Changing key values can be expensive | Additional indexes increase storage and write overhead |

In SQL Server, a table can have one clustered index or be a heap with no clustered index. A primary key does **not** automatically have to be clustered; SQL Server's default depends on the table's existing structure and index definition.

**Follow-up: Why can adding too many indexes slow down INSERT, UPDATE, and DELETE?**

Every relevant index must also be maintained during writes, consuming CPU, I/O, memory, and storage.

---

## 4. Spring Boot, DevOps, and Production Monitoring

### Q13. What are the main benefits of Spring Boot compared with plain Spring?

Spring Boot builds on the Spring Framework and reduces the amount of infrastructure configuration needed to create and run applications.

- **Auto-configuration:** Configures components based on classpath dependencies and application settings.
- **Starter dependencies:** Simplifies dependency management, such as `spring-boot-starter-web`.
- **Embedded servers:** Supports running web applications with embedded Tomcat, Jetty, or other supported servers.
- **Production-ready features:** Spring Boot Actuator exposes health, metrics, and other operational endpoints.
- **Externalized configuration:** Supports configuration through properties, YAML, environment variables, and other sources.
- **Simplified deployment:** Makes it easier to package and run an application as an executable JAR or container.

Example: in plain Spring, servlet infrastructure and dependency integration may require explicit configuration. In Spring Boot, a web starter and appropriate configuration can provide a working web application with much less setup.

Spring Boot does not replace Spring; it simplifies using the Spring ecosystem.

### Q14. What pipeline steps are necessary for continuous delivery?

Continuous delivery means the application can be released to production reliably at any time, subject to the organization's release approval process.

1. **Source control:** Commit, pull request, and code review.
2. **Build:** Compile Java and resolve dependencies.
3. **Quality gates:** Static analysis, linting, and quality checks.
4. **Automated tests:** Unit, integration, API, and regression tests.
5. **Security checks:** Dependency, secret, and container scanning.
6. **Package and publish:** Build an immutable JAR or container image.
7. **Deploy to staging:** Apply configuration and database migrations.
8. **Validate:** Smoke tests, health checks, and acceptance tests.
9. **Release:** Approval if required, then controlled production rollout.
10. **Monitor and recover:** Metrics, logs, alerts, and rollback procedures.

**CI vs continuous delivery vs continuous deployment**

- **Continuous integration (CI):** Changes are integrated and validated automatically.
- **Continuous delivery:** Every validated change is kept deployable; production release may require approval.
- **Continuous deployment:** Every change that passes the required gates is automatically deployed to production.

Mention the actual tools used in your environment, such as Jenkins or GitHub Actions, Maven or Gradle, Docker, and Kubernetes, only where applicable.

### Q15. What information would you collect from a running microservice or pod, and how?

I would collect four primary categories of telemetry, commonly organized using the **four golden signals**.

| Category | What to monitor |
|---|---|
| Latency | Request duration, p50/p95/p99 response time |
| Traffic | Requests per second, throughput, concurrent requests |
| Errors | HTTP 5xx, exception rate, failed messages |
| Saturation | CPU, memory, thread pools, connection pools, queue depth |

Also monitor JVM and dependency metrics:

- Heap and non-heap memory, garbage-collection pauses, and allocation rates.
- JVM thread counts and deadlocks.
- Database connection-pool utilization and query latency.
- Kafka consumer lag, processing failures, and retry counts.
- Redis latency, cache hit rate, and connection errors.
- Pod restarts, OOM kills, CPU throttling, and readiness/liveness probe failures.

**How to collect them**

- **Spring Boot Actuator + Micrometer:** Expose application health and metrics.
- **Prometheus:** Scrape metrics.
- **Grafana:** Visualize dashboards and trends.
- **OpenTelemetry:** Instrument distributed traces and propagate trace context.
- **Jaeger or Tempo:** Store and visualize traces, depending on the chosen stack.
- **Centralized logging:** Collect structured logs using an organization-approved logging platform.
- **Alerting:** Alert on SLO violations, sustained error rates, elevated latency, and resource exhaustion.

Correlate logs, metrics, and traces using trace IDs and request IDs. Avoid using highly variable values such as user IDs as metric labels because they create excessive cardinality.

---

## 5. Agile: Scrum vs Kanban

### Q16. Kanban vs Scrum

| Aspect | Scrum | Kanban |
|---|---|---|
| Work model | Fixed-length sprints | Continuous flow |
| Planning | Sprint planning | Replenishment and prioritization |
| Roles | Defined accountabilities, including Product Owner and Scrum Master | No mandatory prescribed roles |
| Work limits | Sprint capacity and backlog commitment | Explicit WIP limits |
| Changes | Scope changes are managed without jeopardizing the Sprint Goal | Priorities can change as capacity becomes available |
| Delivery | Often reviewed at the end of a sprint, but increments can be delivered earlier | Items delivered continuously |
| Metrics | Sprint Goal progress; velocity as a planning aid | Cycle time, throughput, work-item age |

### Does your project use Agile? What exact framework?

A suitable answer, if your team uses Scrum:

> Yes, we follow Agile principles and use Scrum. We conduct sprint planning, daily stand-ups, backlog refinement, sprint reviews, and retrospectives. We track work and defects in Jira, and we use retrospectives to identify improvements.

Do not claim Scrum ceremonies or tools that your actual team does not use. If your team uses a flow-based process, describe its Kanban practices instead.

---

## 6. Leadership and Behavioral Questions

### Q17. A developer refuses to accept a bug reported by a tester, saying it is a feature. What would you do?

**Interview-ready answer**

My first priority would be to understand the disagreement objectively rather than deciding who is right based on their role.

1. Ask the tester to demonstrate the issue with clear reproduction steps, expected behavior, and actual behavior.
2. Review the requirement, acceptance criteria, API contract, and relevant business rules with the developer.
3. If the requirement is ambiguous, involve the Product Owner or Business Analyst to clarify expected behavior.
4. Make a decision based on the agreed requirements and document it in the defect or user story.
5. If the disagreement becomes personal, discuss it privately and keep the team focused on solving the problem rather than assigning blame.

To prevent recurrence, I would improve acceptance criteria, encourage three-amigos discussions involving the developer, tester, and analyst, and agree on a common Definition of Done.

**Important distinction:** If the behavior is outside the agreed scope, it may be a change request rather than a defect. Classification should be based on requirements and product intent, not personal preference.

### Q18. As a Scrum team lead, the customer requests a small but important change without discussing it with the analyst. What would you do?

**Interview-ready answer**

I would acknowledge the business importance of the request while making its impact visible before committing to delivery.

First, I would understand the urgency and business value of the change. I would then involve the Business Analyst and Product Owner to clarify the requirement and define acceptance criteria.

Next, I would ask the team to assess the impact on development, testing, dependencies, architecture, and the current Sprint Goal. Even a small change can have hidden implications.

I would present the customer with options:

- Include the change in the current sprint if the team can accommodate it without jeopardizing the Sprint Goal.
- Replace an existing sprint item of comparable priority, if agreed with the Product Owner and team.
- Schedule it for the next sprint if adding it now creates unacceptable delivery risk.

If the change is genuinely urgent, we would follow the team's agreed process for handling urgent work. Any scope adjustment and trade-off would be communicated transparently.

My objective would be to protect delivery commitments without ignoring an important customer need.

**Scrum nuance:** The Product Owner orders the Product Backlog. During a Sprint, changes can be negotiated with the Product Owner as long as they do not endanger the Sprint Goal. A Sprint can be cancelled only by the Product Owner when its goal becomes obsolete.

### Q19. Describe your biggest failure.

This is not a question where the interviewer expects you to claim that you never fail. They want to understand your accountability, judgment, and ability to learn.

Use the STAR structure:

- **Situation:** Describe the project or incident and the business context.
- **Task:** Explain your responsibility.
- **Action:** Explain what you did, including your mistake and how you addressed it.
- **Result:** Describe the impact, recovery, and what changed afterward.

**Example template to adapt to a real experience**

> One significant mistake I made was underestimating the impact of a change on a dependent component. I focused on the immediate implementation and did not validate all downstream scenarios early enough. During testing, we identified an issue that required additional rework and affected our delivery timeline.
>
> I took responsibility, worked with the team to identify the root cause, fixed the issue, and ensured the affected scenarios were tested. Afterward, I improved the way I assess dependencies and introduced more explicit regression checks for similar changes.
>
> The key lesson was that completing the implementation is not the same as delivering a safe change. I now pay more attention to impact analysis, edge cases, and early validation.

Use this only if it reflects something that actually happened. Be ready to explain the specific mistake, its impact, and the measurable improvement.

---

## 7. Quick Revision Questions

Use these as a final self-test before the interview.

### Q20. Why is Java 17 not sufficient by itself to upgrade an old Spring Boot application?

Because framework compatibility, dependencies, Jakarta migration, build plugins, and application code must also be evaluated.

### Q21. Why can an index make a query slower?

It adds storage and write-maintenance overhead. A poorly chosen index may also increase lookup costs or be ignored by the optimizer.

### Q22. Can a Spring singleton bean safely store mutable request-specific state?

Usually not. A singleton is shared across requests, so mutable shared state can introduce race conditions. Prefer stateless services or properly synchronized state where needed.

### Q23. How do you test a database-backed Spring Boot service?

Use unit tests with mocks for business logic, repository integration tests, and broader integration tests against a representative database where necessary.

### Q24. What is the difference between liveness and readiness probes?

- **Liveness:** Determines whether a container should be restarted.
- **Readiness:** Determines whether it should receive traffic.

### Q25. What if a customer insists on adding scope during a sprint?

Clarify the requirement, evaluate the impact, involve the Product Owner, and negotiate scope while protecting the Sprint Goal.

### Q26. How do you measure whether a database optimization worked?

Compare execution plans, logical reads, CPU, query latency, throughput, and production behavior under a representative workload.

---

## 8. Recommended Preparation Priority

Given a Java and Spring Boot backend background, prepare these topics in order:

1. **Java 8 → 17 and Spring Boot migration:** Compatibility, Jakarta migration, testing, and rollback.
2. **SQL performance:** Execution plans, composite/covering indexes, locking, and clustered vs non-clustered indexes.
3. **Design patterns:** Explain Strategy, Factory, Builder, and Proxy using code or project examples.
4. **Production monitoring and CI/CD:** Explain the complete request-to-production lifecycle.
5. **Behavioral and leadership:** Prepare two genuine stories—one about a technical mistake and one about resolving a disagreement or managing a priority change.

The most important improvement is to connect each theoretical answer to a real situation from your work. Interviewers are likely to follow up with:

- Where did you use it?
- Why did you choose it?
- What alternatives did you consider?
- What trade-offs did you evaluate?
- How did you measure the result?
