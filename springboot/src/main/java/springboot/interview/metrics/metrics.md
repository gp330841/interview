# Spring Boot Micrometer Metrics & Observability Guide

This module covers the core concepts, patterns, and architectural designs behind gathering **metrics** in Spring Boot using **Micrometer**, scraping them using **Prometheus**, and visualizing them on **Grafana**.

---

## 💡 Core Concepts

### 1. What is Micrometer?
Micrometer acts as a **SLF4J for metrics**. It is a vendor-neutral dimensional metrics collection facade that allows you to instrument your JVM-based application code with counters, timers, gauges, and summaries, and export them to different monitoring systems (like Prometheus, Datadog, New Relic) without changing your application code.

### 2. Metric Types

| Metric Type | Description | Use Case |
| :--- | :--- | :--- |
| **Counter** | A monotonically increasing numeric value that can only **go up** (or reset to zero on restart). | Total API visits, login failures, database transactions completed. |
| **Gauge** | A fluctuating numeric value that can go **up or down** dynamically, representing a snapshot of the current state. | Queue depth, active HTTP sessions, CPU usage, thread count. |
| **Timer** | Measures short-duration latency events and tracks their frequency. | REST API response times, downstream Feign client call latency. |
| **DistributionSummary**| Tracks the distribution of events (non-time-based values). | HTTP payload sizes, SQL result set counts. |

---

## 🎯 Top Senior Developer Interview Q&A

### Q1: What is the difference between a dimensional metric system and a hierarchical metric system?
**Answer:**
- **Hierarchical (Legacy)**: Metrics are named using dot-separated paths (e.g. `api.orders.create.success.count`). If you want to segment by user tier or region, you must prefix it to the path (e.g. `api.orders.create.premium.us-east.success.count`), resulting in a combinatorial explosion of metric names and making generic aggregation extremely difficult.
- **Dimensional (Modern - Micrometer/Prometheus)**: Metrics have a single name accompanied by key-value pairs called **Tags** or **Dimensions** (e.g., `orders_processed_total{tier="premium", region="us-east"}`). This allows highly flexible queries to aggregate across dimensions, filter specific tags, and calculate rates seamlessly.

---

### Q2: Why is it bad practice to register a Counter inside a REST controller method using a dynamic tag value (like user ID)?
**Answer:**
This is a critical senior design issue called **High Cardinality**.
Tags are stored as index dimensions in time-series databases (like Prometheus). If you add a tag value with high cardinality (such as `userId`, `email`, or `transactionId`), the database must create a distinct index entry (time-series stream) for every unique ID.
In production, millions of distinct IDs will overload the Prometheus index, causing high memory usage, slow query times, and eventually crashing the monitoring system.
**Rule of Thumb:** Tags must only have a small, predictable set of values (low cardinality, such as `status_code="200/500"`, `tier="premium/free"`, or `method="GET/POST"`).

---

### Q3: How do you register a custom Gauge safely in Spring Boot?
**Answer:**
Unlike Counters and Timers, you do **not** call an increment/record method on a Gauge. Instead, you register the Gauge to observe a dynamic object (like a collection or an `AtomicInteger`).
```java
AtomicInteger queueDepth = new AtomicInteger(0);
Gauge.builder("inventory_queue_depth", queueDepth, AtomicInteger::get)
     .description("Current size of the inventory queue")
     .register(meterRegistry);
```
**Danger:** Micrometer holds a *weak reference* to the observed object. If no other class maintains a strong reference to `queueDepth`, it will be garbage collected, and the Gauge will start reporting `NaN` or disappear. Always ensure the observed target is a long-lived object or stored inside a bean field!

---

### Q4: How do you configure Spring Boot to track API response times automatically?
**Answer:**
Spring Boot Actuator auto-configures micrometer instrumentation for all endpoints. It tracks requests using the metric `http_server_requests_seconds`.
To enable histogram percentiles (so Grafana can plot p95, p99 latencies) and SLA boundaries, add this configuration in `application.properties`:
```properties
management.metrics.distribution.percentiles-enabled.http.server.requests=true
management.metrics.distribution.sla.http.server.requests=100ms,500ms,1000ms
```
This is essential for senior developers to establish **Service Level Indicators (SLIs)** and track SLA compliance.

---

### Q5: What is the purpose of Prometheus Pull vs Push metrics architectures?
**Answer:**
- **Pull (Prometheus standard)**: The Prometheus server periodically scrapes (fetches) metrics from a exposed HTTP endpoint (`/actuator/prometheus`) on the target application. This reduces overhead on the application and centralizes the scraping interval management.
- **Push (e.g., StatsD, InfluxDB)**: The application pushes metric payloads to a central server. This is typically used in short-lived serverless functions (like AWS Lambda) where the container terminates before a pull server can scrape it.
