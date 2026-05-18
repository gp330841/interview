# Spring Boot - Microservices

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **API Gateway for Centralized Routing**: A streaming platform (like Netflix) has hundreds of microservices (User Service, Video Service, Billing Service). Instead of mobile apps talking to all these directly, they talk to a single Spring Cloud Gateway. The gateway handles authentication, rate limiting, and routes requests (e.g., `/api/videos/**` to the Video Service). If the Video Service moves to a different IP, the mobile app doesn't care; it only knows the gateway URL.
2. **Circuit Breaker for Fault Tolerance**: An E-commerce checkout service needs to call an external Payment Gateway API. If the Payment API goes down or becomes extremely slow, the Checkout service's threads will block, eventually crashing the entire application. Using Resilience4j Circuit Breaker, if the Payment API fails 5 times in a row, the circuit "opens" and immediately returns a fallback response ("Payment currently unavailable") without even trying to hit the broken API, saving the Checkout service from crashing.

---

## Beginner Level Questions

### Q1. What are Microservices? [Easy]
**Answer:** Microservices are an architectural style where a single large application is built as a suite of small, independent services. Each service runs in its own process, communicates via lightweight mechanisms (like HTTP REST or messaging), and is built around a specific business capability.

### Q2. What is a Monolith architecture? [Easy]
**Answer:** It is the traditional way of building software where all components (UI, business logic, database access) are combined into a single, tightly coupled, and unified codebase and deployed as a single unit (e.g., a massive `.war` or `.jar` file).

### Q3. What is Spring Cloud? [Easy]
**Answer:** Spring Cloud is a suite of tools built on top of Spring Boot that provides solutions for common distributed system patterns (e.g., configuration management, service discovery, circuit breakers, intelligent routing).

### Q4. What is Service Discovery? [Easy]
**Answer:** In microservices, services are constantly starting up, shutting down, or moving to different IPs (especially in Kubernetes/Docker). Service Discovery is a registry (like a phonebook) where services register their IP and port upon startup. Other services ask the registry to find them by name (e.g., "find `payment-service`") instead of hardcoding IPs.

### Q5. Name a popular Service Registry used with Spring Cloud. [Easy]
**Answer:** **Eureka** (created by Netflix). Consul and Zookeeper are also popular alternatives.

### Q6. What is an API Gateway? [Easy]
**Answer:** An API Gateway acts as the single point of entry into a microservice architecture. It sits between the clients and the internal microservices, handling routing, composition, authentication, and cross-cutting concerns.

### Q7. What tool does Spring Cloud provide for API Gateways? [Easy]
**Answer:** **Spring Cloud Gateway**. (Historically, Netflix Zuul was used, but it is now deprecated in favor of Spring Cloud Gateway, which is built on the reactive WebFlux stack).

### Q8. What is a Circuit Breaker? [Easy]
**Answer:** A Circuit Breaker is a design pattern used to prevent cascading failures in a distributed system. If a downstream service is failing or unresponsive, the circuit breaker trips (opens), and subsequent calls immediately fail or execute a fallback method, rather than waiting for timeouts and exhausting resources.

### Q9. Name a popular Circuit Breaker library used with Spring Boot. [Easy]
**Answer:** **Resilience4j**. (Historically, Netflix Hystrix was used, but it is now in maintenance mode).

### Q10. What is Distributed Tracing? [Easy]
**Answer:** When a single user request travels through 5 different microservices, it becomes very difficult to track errors or performance bottlenecks. Distributed Tracing assigns a unique "Trace ID" to the initial request and passes it along to every downstream service, allowing you to visualize the entire path of the request across the architecture.

---

## Intermediate Level Questions

### Q11. Explain how Eureka Client and Server work together. [Medium]
**Answer:** 
1. You deploy a Spring Boot app annotated with `@EnableEurekaServer` (The Registry).
2. You deploy microservices annotated with `@EnableEurekaClient`.
3. The clients register themselves with the server using their `spring.application.name`.
4. The clients send "heartbeats" (usually every 30 seconds) to the server to prove they are alive.
5. If the server misses several heartbeats, it removes the client from the registry.

### Q12. What is Client-Side Load Balancing? [Medium]
**Answer:** Instead of having a physical load balancer (like Nginx) sit between two services, the calling service acts as the load balancer. It queries Eureka for all available IPs of the target service (e.g., 3 instances of `payment-service`), and uses an algorithm (like Round Robin) to pick one IP to send the request to.

### Q13. How do you implement Client-Side Load Balancing in Spring Boot? [Medium]
**Answer:** By using **Spring Cloud LoadBalancer** (which replaced Netflix Ribbon). You can use it implicitly by annotating a `RestTemplate` or `WebClient` bean with `@LoadBalanced`.

### Q14. What is Feign (OpenFeign)? [Medium]
**Answer:** It is a declarative web service client. Instead of writing boilerplate `RestTemplate` code to call another microservice, you simply create a Java Interface, annotate it with `@FeignClient(name = "payment-service")`, and write abstract methods mirroring the target REST API. Spring automatically generates the implementation and handles the HTTP calls and load balancing.

### Q15. How do you centralize configuration in Microservices? [Medium]
**Answer:** Using **Spring Cloud Config Server**. Instead of having `application.properties` scattered across 50 different microservices, the Config Server reads configuration from a central Git repository. The microservices (Config Clients) connect to the Config Server on startup to fetch their specific properties.

### Q16. How do you refresh Spring Cloud Config properties without restarting the microservice? [Medium]
**Answer:** You annotate the beans that use the properties with `@RefreshScope`. Then, after updating the Git repository, you send an HTTP POST request to the microservice's `/actuator/refresh` endpoint. Spring will reload the properties and recreate the `@RefreshScope` beans on the fly.

### Q17. Explain the Half-Open state of a Circuit Breaker. [Medium]
**Answer:** 
* **Closed**: Normal operation. Requests flow through.
* **Open**: Downstream service failed. Requests are blocked immediately.
* **Half-Open**: After a configured wait time, the circuit allows a limited number of "test" requests through. If they succeed, it assumes the downstream service is healthy and switches back to Closed. If they fail, it switches back to Open.

### Q18. How do you implement Distributed Tracing in Spring Boot? [Medium]
**Answer:** Using **Micrometer Tracing** (which replaced Spring Cloud Sleuth in Spring Boot 3) along with a backend storage/visualization system like **Zipkin** or **Jaeger**. It automatically intercepts incoming/outgoing HTTP requests and Kafka messages to inject and extract Trace IDs and Span IDs.

### Q19. What is the difference between a Trace ID and a Span ID? [Medium]
**Answer:** 
* **Trace ID**: A globally unique identifier assigned to a single user request as it travels through the *entire* microservice ecosystem.
* **Span ID**: An identifier for a specific unit of work *within* that trace (e.g., the time spent specifically inside Service A, or the time spent executing a specific DB query). A Trace contains multiple Spans.

### Q20. What is the API Composition pattern? [Medium]
**Answer:** Also known as BFF (Backend For Frontend). When a UI needs data that lives in multiple microservices, it shouldn't make 5 separate network calls. Instead, it makes one call to an API Gateway or a Composer Service, which internally fans out calls to the 5 microservices, aggregates the JSON responses into a single object, and returns it to the UI.

---

## Advanced Level Questions

### Q21. Explain the Saga Pattern. [Hard]
**Answer:** In microservices, you cannot use traditional ACID database transactions (2PC) across multiple databases. The Saga pattern solves distributed transactions by breaking them into a sequence of local transactions. Each service performs its local transaction and publishes an event. The next service listens to that event and performs its transaction.

### Q22. How do you handle failures in a Saga? [Hard]
**Answer:** Using **Compensating Transactions**. If Step 3 of a Saga fails, you cannot simply "roll back" Steps 1 and 2 because they have already committed to their respective databases. Instead, Step 3 publishes a failure event, triggering Step 2 and Step 1 to execute their specific compensating logic (e.g., if Step 1 was "Deduct $50", the compensating transaction is "Refund $50").

### Q23. Choreography vs. Orchestration in Sagas. [Hard]
**Answer:**
* **Choreography**: Decentralized. Services publish and listen to events (Kafka/RabbitMQ) and react independently. Good for simple workflows, hard to track visually.
* **Orchestration**: Centralized. A dedicated "Orchestrator" service tells other services exactly what to do via commands, and tracks the overall state of the transaction. Better for complex workflows.

### Q24. What is the CQRS Pattern? [Hard]
**Answer:** Command Query Responsibility Segregation. It separates the read model (Queries) from the write/update model (Commands) into different databases. For example, writes go to a normalized MySQL database, which publishes events to Kafka. A read service consumes those events and updates a denormalized, read-optimized Elasticsearch database used for fast UI querying.

### Q25. What is the Strangler Fig Pattern? [Hard]
**Answer:** It is a strategy for migrating from a legacy Monolith to Microservices safely. You place an API Gateway in front of the monolith. You write a new microservice for a specific feature (e.g., Billing). You configure the Gateway to route all `/billing` traffic to the new microservice, and everything else to the monolith. You repeat this piece-by-piece until the monolith is "strangled" and can be deleted.

### Q26. How do you implement Bulkheading in Resilience4j? [Hard]
**Answer:** The Bulkhead pattern isolates resources to prevent one failing service from taking down the entire app. For example, if Service A makes calls to Service B and Service C, and B becomes slow, all Tomcat worker threads will get stuck waiting for B, leaving no threads to serve C. A Bulkhead restricts the maximum number of concurrent threads allowed to call Service B. If the limit is reached, it immediately rejects new requests for B, preserving threads for C.

### Q27. What is the Sidecar Pattern? [Hard]
**Answer:** Deploying a separate, helper container (the sidecar) alongside the primary application container within the same Pod (in Kubernetes). The Sidecar handles cross-cutting concerns like logging, proxying traffic, or encrypting communications (like Envoy proxy in a Service Mesh). It prevents bloating the main application code with infrastructure logic.

### Q28. What is a Service Mesh (e.g., Istio)? [Hard]
**Answer:** As you get hundreds of microservices, managing retries, mutual TLS (mTLS) encryption, and tracing inside the Spring Boot code becomes unmanageable. A Service Mesh extracts all these network-level concerns out of the application code entirely. It deploys an invisible Sidecar proxy next to every microservice. Microservices talk to their sidecar, and the sidecars handle all the complex routing and security logic securely.

### Q29. How do you handle Distributed Caching in Microservices? [Hard]
**Answer:** Using a centralized cache server like a Redis Cluster. If Service A caches data in its own local JVM memory, Service B (or a second instance of Service A) cannot see it. By using Redis, all instances share the same cache state.

### Q30. What is the Outbox Pattern? [Hard]
**Answer:** It guarantees reliable message delivery in event-driven microservices. When updating a database and publishing a Kafka message, you cannot do both in one atomic transaction. The Outbox pattern writes the business entity (e.g., Order) AND the event payload (e.g., OrderCreated) to an `outbox` table in the *same local database transaction*. A separate process (like Debezium) asynchronously reads the `outbox` table and pushes the messages to Kafka, ensuring zero message loss even if the app crashes mid-operation.
