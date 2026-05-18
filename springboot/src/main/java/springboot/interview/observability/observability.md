# Spring Boot - Observability (Logging, Tracing & Metrics)

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Correlating Logs Across Distributed Systems**: A user complains that their checkout failed. Your application spans 5 microservices. Instead of manually searching through 5 different log files for the user's ID, you use **Distributed Tracing (Micrometer Tracing)**. A unique `traceId` is automatically injected into the MDC (Mapped Diagnostic Context) of the very first request and passed to every downstream service. By simply searching Kibana or Datadog for that single `traceId`, you instantly see the exact path the request took and exactly which microservice threw the `NullPointerException`.
2. **Dynamic Alerting with Metrics**: You want to page the on-call engineer only when things are actually broken. You use **Micrometer Metrics** to record the duration of every HTTP request (`@Timed`). Prometheus scrapes these metrics. You configure a Grafana alert: "If the 99th percentile response time for `/api/checkout` exceeds 2 seconds for more than 5 minutes, send an alert to PagerDuty." This is much more reliable than parsing raw text logs for the word "timeout".

---

## Beginner Level Questions

### Q1. What are the three pillars of Observability? [Easy]
**Answer:** 
1. **Logs**: Discrete events occurring within an application (e.g., "User logged in", "Database connection failed").
2. **Metrics**: Aggregated numerical data measured over intervals of time (e.g., CPU usage %, total HTTP 500 errors per minute).
3. **Traces**: The lifecycle of a single request as it propagates through a distributed system.

### Q2. What is the default logging framework in Spring Boot? [Easy]
**Answer:** Spring Boot uses **Logback** as the default logging implementation, wrapped by the **SLF4J** (Simple Logging Facade for Java) API.

### Q3. Why should you use SLF4J instead of `System.out.println()`? [Easy]
**Answer:** `System.out.println` is synchronous, slow, cannot be easily formatted or redirected to files, and cannot be dynamically turned on/off. SLF4J provides severity levels (INFO, DEBUG), asynchronous logging for performance, log rotation, and formatted output.

### Q4. What are the standard logging levels in Spring Boot? [Easy]
**Answer:** From least severe to most severe: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `FATAL`. By default, Spring Boot only prints `INFO`, `WARN`, and `ERROR` messages.

### Q5. How do you change the logging level for a specific package in `application.properties`? [Easy]
**Answer:** You use the `logging.level.<package-name>` property.
**Code Example:**
```properties
logging.level.org.springframework.web=DEBUG
logging.level.com.myapp.service=TRACE
```

### Q6. How do you write logs to a file instead of just the console? [Easy]
**Answer:** By setting the `logging.file.name` or `logging.file.path` property in `application.properties`. If configured, Spring Boot automatically creates the file and handles basic log rotation (e.g., 10MB per file).

### Q7. What is Micrometer? [Easy]
**Answer:** Micrometer is an open-source vendor-neutral metrics facade (it does for metrics what SLF4J does for logging). It provides a simple API to record metrics and can export them to various monitoring systems (Prometheus, Datadog, New Relic) just by changing the dependency on the classpath.

### Q8. What is the difference between a Counter and a Timer metric? [Easy]
**Answer:**
* **Counter**: Tracks values that only ever increase (e.g., total number of processed orders).
* **Timer**: Tracks the frequency of an event *and* how long it takes (e.g., the duration of HTTP requests), allowing you to calculate average and maximum latencies.

### Q9. What is Distributed Tracing? [Easy]
**Answer:** In a microservices architecture, a single user click might trigger calls to 5 different services. Distributed tracing connects these separate service logs together so you can see the entire journey of the request from start to finish.

### Q10. What tool replaced Spring Cloud Sleuth in Spring Boot 3? [Easy]
**Answer:** **Micrometer Tracing**. Spring Cloud Sleuth was deprecated and its core tracing capabilities were moved directly into the Micrometer project to integrate more tightly with Spring Boot 3's native observability features.

---

## Intermediate Level Questions

### Q11. What is MDC (Mapped Diagnostic Context)? [Medium]
**Answer:** MDC is a thread-local map provided by SLF4J. It allows you to store contextual information (like a `userId` or `transactionId`) at the beginning of a request. The logging framework automatically appends this data to every log statement executed by that thread, making it incredibly easy to filter logs by a specific user.

### Q12. How do you configure advanced logging rules (e.g., different formats for console vs. file, time-based rotation)? [Medium]
**Answer:** While `application.properties` handles basic setup, for advanced features you must create a `logback-spring.xml` file in the `src/main/resources` directory. Spring Boot automatically detects this file and applies the detailed Appender, Layout, and RollingPolicy configurations.

### Q13. How do you dynamically change logging levels at runtime without restarting the application? [Medium]
**Answer:** By using the Spring Boot Actuator `/actuator/loggers` endpoint. You can send an HTTP POST request to `/actuator/loggers/com.myapp.service` with a JSON body `{"configuredLevel": "DEBUG"}` to instantly enable debug logging for troubleshooting.

### Q14. What are Tags (or Dimensions) in Micrometer? [Medium]
**Answer:** Tags allow you to slice and dice your metrics. Instead of creating a separate counter for `orders.successful` and `orders.failed`, you create one counter `orders.total` and add a tag `status=success` or `status=failed`. In your dashboard (like Grafana), you can easily filter, group, or sum the metric based on these tags.

### Q15. Explain how Trace IDs and Span IDs work together. [Medium]
**Answer:**
* **Trace ID**: A unique identifier generated when the request first hits the system (e.g., at the API Gateway). It is passed to every downstream service in HTTP headers. It binds the whole journey together.
* **Span ID**: A unique identifier for a specific operation *within* the trace (e.g., one Span for the Database call, one Span for the Kafka publish). The first span is the "Root Span", and subsequent spans link back to their parent span.

### Q16. How are Trace IDs propagated between Microservices? [Medium]
**Answer:** Through HTTP headers (or Kafka record headers). The W3C Trace Context is the modern standard, using the `traceparent` and `tracestate` headers. Spring Boot automatically injects these headers into outgoing `RestTemplate` or `WebClient` calls and extracts them from incoming HTTP requests.

### Q17. How do you export traces to a visualizer like Zipkin? [Medium]
**Answer:**
1. Add the `micrometer-tracing-bridge-brave` (or OpenTelemetry bridge) dependency.
2. Add the `zipkin-reporter-brave` dependency to export to Zipkin.
3. Configure the Zipkin server URL in `application.properties`: `management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans`.

### Q18. What is the `@Timed` annotation? [Medium]
**Answer:** It is a Micrometer annotation that can be placed on methods or classes. It automatically creates a Timer metric that tracks the count, max time, and total time of the method executions. It requires the `TimedAspect` bean to be registered in your configuration.

### Q19. How do you log the actual SQL statements executed by Hibernate? [Medium]
**Answer:** You can set `spring.jpa.show-sql=true` in properties, but this prints to standard out (bypassing SLF4J). The correct way is to set the logging level: `logging.level.org.hibernate.SQL=DEBUG`. To see the actual parameters bound to the SQL query, you also add `logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE`.

### Q20. What is a Gauge metric, and when would you use it? [Medium]
**Answer:** A Gauge represents a value that can go both up and down, like the speedometer in a car. You use it for things like "Current Active Sessions", "Current Thread Pool Size", or "Current Memory Usage". You do not explicitly increment/decrement a gauge; instead, you pass a function or reference to an object that Micrometer can poll to get the current value.

---

## Advanced Level Questions

### Q21. Explain the difference between Push and Pull-based metric systems. [Hard]
**Answer:**
* **Pull-based (Prometheus)**: The monitoring server actively scrapes (makes HTTP GET requests to) your Spring Boot application's `/actuator/prometheus` endpoint at intervals. If your app goes down, Prometheus immediately knows because the scrape fails. Better for internal networks.
* **Push-based (Datadog, InfluxDB)**: The Spring Boot application actively batches and POSTs metrics to the monitoring server over the internet. Better for ephemeral tasks (like AWS Lambdas or short-lived batch jobs) that don't live long enough to be scraped.

### Q22. How do you solve the MDC context loss problem in asynchronous programming (`@Async` or `CompletableFuture`)? [Hard]
**Answer:** `ThreadLocal` variables (like MDC) do not automatically transfer to child threads or thread pools. If you spawn a new thread, the `traceId` will be missing in the logs.
* **Solution**: You must use decorators. Spring provides `TaskDecorator` for `@Async` where you copy the MDC map from the parent thread and set it on the child thread. Micrometer Tracing provides `ContextSnapshot` tools to automatically propagate tracing contexts across thread boundaries.

### Q23. What is OpenTelemetry (OTel)? [Hard]
**Answer:** OpenTelemetry is a massive industry standard (CNCF project) aiming to provide a single set of APIs, libraries, and agents to generate, collect, and export all three pillars of observability (Logs, Metrics, Traces). Spring Boot 3 natively supports OpenTelemetry as a backend bridge for Micrometer.

### Q24. How do you structure logs for Centralized Log Aggregation (like ELK/Splunk)? [Hard]
**Answer:** Raw text logs are very difficult to parse using Regex in Elasticsearch. The best practice is to configure Logback to output logs as **JSON arrays**. You use the `logstash-logback-encoder` library. This ensures that MDC values (like `traceId`), timestamps, and severity levels are distinct JSON fields, making querying in Kibana blazing fast and 100% accurate.

### Q25. What is the Observer Effect in monitoring, and how does Spring handle it? [Hard]
**Answer:** The Observer Effect is when the act of monitoring a system alters its performance (e.g., generating millions of trace spans slows the application down). Spring handles this using **Sampling**. By default, it might only keep and export 10% of traces (`management.tracing.sampling.probability=0.1`). This provides statistically significant data without killing application performance or overwhelming the Zipkin server.

### Q26. How do you trace database queries in Spring Boot 3? [Hard]
**Answer:** Standard JDBC doesn't know about Micrometer trace contexts. You must use a proxy. The **Datasource Micrometer** library (or P6Spy) wraps your standard `DataSource`. It intercepts the JDBC calls, creates a new Span representing the database query execution time, and attaches it to the active HTTP request trace, allowing you to see exactly how long the SQL took within the overall request.

### Q27. Explain Percentiles vs. Averages in metrics. Why are percentiles better? [Hard]
**Answer:** Averages mask outliers. If 99 requests take 10ms, and 1 request takes 10,000ms, the average is 109ms (which looks fine). Percentiles sort the data. A p99 of 10,000ms tells you exactly that the worst 1% of your users are experiencing terrible 10-second latencies. Micrometer Timers can be configured to publish percentiles automatically (`management.metrics.distribution.percentiles.http.server.requests=0.95,0.99`).

### Q28. How do you implement High Cardinality tag protection? [Hard]
**Answer:** Cardinality is the number of unique combinations of Tags on a metric. If you tag a metric with a `userId`, and you have 1 million users, you just created 1 million unique metric time-series in Prometheus, which will crash the Prometheus server (Memory OOM). You should *never* tag metrics with unbounded unique IDs. You should only use bounded tags (like `status=200`, `region=us-east`).

### Q29. What is an Exemplar in Prometheus/OpenTelemetry? [Hard]
**Answer:** An Exemplar bridges the gap between Metrics and Traces. When looking at a Grafana graph showing a spike in the p99 latency metric, you normally have to manually search logs/traces for that exact time to figure out *why*. An Exemplar attaches a specific `traceId` directly to the metric data point. Clicking the spike on the graph instantly jumps you to the exact trace causing the slowdown.

### Q30. How do you test Observability configurations? [Hard]
**Answer:** Testing logs and metrics is tricky.
* **Logs**: You can use the `OutputCaptureExtension` in JUnit 5 to capture standard out and assert that specific strings were logged.
* **Metrics**: You can autowire `MeterRegistry` into your `@SpringBootTest`, execute your business logic, and use assertions like `assertEquals(1, meterRegistry.counter("my.custom.counter").count())` to verify the instrumentation works.
