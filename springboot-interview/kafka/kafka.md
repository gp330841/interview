# Spring Boot - Apache Kafka Integration

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Asynchronous Order Processing (Event-Driven Architecture)**: When a user places an order on an e-commerce site, the `OrderService` (Spring Boot app) immediately saves the order as "PENDING" and returns a 200 OK to the user (fast response). It then publishes an `OrderPlacedEvent` to a Kafka topic. Behind the scenes, the `InventoryService`, `PaymentService`, and `NotificationService` all independently consume this event from Kafka to deduct stock, process the credit card, and send a confirmation email at their own pace without blocking the user.
2. **High-Throughput Log Aggregation**: A fleet of 50 microservices continuously generates millions of log lines and user click-stream metrics. Writing these directly to a database would overwhelm it. Instead, all microservices act as Kafka Producers, writing data to a Kafka topic. A separate, dedicated Spring Boot consumer application reads this data in massive batches, compresses it, and bulk-inserts it into Elasticsearch or a Data Warehouse.

---

## Beginner Level Questions

### Q1. What is Apache Kafka? [Easy]
**Answer:** Apache Kafka is an open-source distributed event streaming platform. It is designed to handle massive volumes of real-time data efficiently. It acts as a highly scalable, fault-tolerant message broker between applications.

### Q2. What is the difference between RabbitMQ and Kafka? [Easy]
**Answer:**
* **RabbitMQ**: A traditional message broker (Smart Broker / Dumb Consumer). Messages are pushed to consumers and deleted from the queue once acknowledged. Best for complex routing and job queues.
* **Kafka**: A distributed append-only log (Dumb Broker / Smart Consumer). Messages are appended to a log and *kept* for a retention period (e.g., 7 days). Consumers pull data and track their own offsets. Best for massive data streaming, event sourcing, and log aggregation.

### Q3. Explain Topics, Producers, and Consumers in Kafka. [Easy]
**Answer:**
* **Topic**: A logical category or "folder" where records (messages) are published.
* **Producer**: An application that writes (publishes) data to a topic.
* **Consumer**: An application that reads (subscribes to) data from a topic.

### Q4. What is a Kafka Broker? [Easy]
**Answer:** A single Kafka server is called a Broker. A Kafka Cluster is composed of multiple Brokers working together to distribute data, provide redundancy, and handle high throughput.

### Q5. How do you integrate Kafka into a Spring Boot application? [Easy]
**Answer:** You include the `spring-kafka` dependency. Spring Boot's auto-configuration will automatically set up `KafkaTemplate` (for producing) and listener containers (for consuming) based on properties defined in `application.properties`.

### Q6. What is `KafkaTemplate`? [Easy]
**Answer:** It is a high-level Spring abstraction (similar to `RestTemplate` or `JdbcTemplate`) that provides convenience methods for sending messages to Kafka topics.

### Q7. How do you send a simple message to a Kafka topic using Spring Boot? [Easy]
**Answer:** You inject `KafkaTemplate<String, String>` and call the `send()` method.
**Code Example:**
```java
kafkaTemplate.send("my-topic", "Hello Kafka!");
```

### Q8. What is `@KafkaListener`? [Easy]
**Answer:** It is a method-level annotation that marks a method to be the target of a Kafka message listener. Spring automatically creates a consumer thread in the background that continuously polls the specified topic and executes this method whenever a new message arrives.

### Q9. How do you consume a message from a Kafka topic? [Easy]
**Answer:**
**Code Example:**
```java
@KafkaListener(topics = "my-topic", groupId = "my-group")
public void listen(String message) {
    System.out.println("Received: " + message);
}
```

### Q10. What is a Consumer Group? [Easy]
**Answer:** A Consumer Group (`groupId`) is a cluster of consumers that work together to consume a topic. Kafka ensures that each message in a topic is delivered to *only one* consumer instance within the same group. This is how Kafka scales message processing (competing consumers).

---

## Intermediate Level Questions

### Q11. What are Partitions in Kafka? [Medium]
**Answer:** A Topic is divided into multiple Partitions. Partitions allow a single topic to be distributed across multiple brokers, breaking the size limit of a single server. Partitions are the fundamental unit of parallelism in Kafka.

### Q12. How does a Producer decide which Partition to send a message to? [Medium]
**Answer:** By default, it looks at the message "Key".
* **If Key is null**: It uses a Round-Robin strategy to distribute messages evenly across all partitions.
* **If Key is provided**: It hashes the key (e.g., `hash(orderId) % num_partitions`). Messages with the same key will *always* go to the exact same partition.

### Q13. Why is the Message Key important? [Medium]
**Answer:** Kafka only guarantees message ordering *within a single partition*, not across the entire topic. By providing a key (like a User ID or Order ID), you guarantee that all events for that specific entity go to the same partition, ensuring they are processed in the exact order they occurred.

### Q14. What is a Consumer Offset? [Medium]
**Answer:** An offset is a unique sequential ID assigned to every message within a partition. The consumer uses the offset to keep track of exactly which messages it has already read. Kafka stores these offsets in an internal topic called `__consumer_offsets`.

### Q15. How do you serialize/deserialize Java Objects (DTOs) in Spring Kafka? [Medium]
**Answer:** Kafka transmits bytes. You configure the Producer to use `JsonSerializer` and the Consumer to use `JsonDeserializer` (provided by Spring Kafka) in `application.properties`.
```properties
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
```

### Q16. What happens if a `@KafkaListener` method throws an Exception? [Medium]
**Answer:** By default, Spring Kafka catches the exception, logs it, and *moves on* to the next message (acknowledging the failed one). If you don't handle it, the message is permanently lost. You must configure an `ErrorHandler` (like `DefaultErrorHandler` in newer versions) to retry the message or send it to a Dead Letter Topic (DLT).

### Q17. What is a Dead Letter Topic (DLT)? [Medium]
**Answer:** If a message repeatedly fails to process (e.g., malformed JSON or database is down) even after multiple retries, instead of blocking the entire partition forever, the consumer routes the "poison pill" message to a separate topic called a DLT for manual inspection later, and continues processing new messages.

### Q18. How do you configure a DLT in Spring Boot? [Medium]
**Answer:** You can manually configure a `DefaultErrorHandler` bean, or you can use the `@RetryableTopic` annotation directly on your listener method.
**Code Example:**
```java
@RetryableTopic(attempts = "3", dltStrategy = DltStrategy.FAIL_ON_ERROR)
@KafkaListener(topics = "orders")
public void processOrder(Order order) { ... }
```

### Q19. How do you consume messages in batches? [Medium]
**Answer:** You set `spring.kafka.listener.type=batch` in properties. Then, change your `@KafkaListener` method signature to accept a `List<T>` instead of a single object. This is significantly faster for database bulk inserts.

### Q20. What is `ackMode` in Spring Kafka? [Medium]
**Answer:** It defines *when* the consumer tells the Kafka broker "I have successfully processed this message" (committing the offset).
* `BATCH` (Default): Commits offset when the entire batch returned by the poll() finishes.
* `RECORD`: Commits offset immediately after the listener method returns for *each* record.
* `MANUAL_IMMEDIATE`: The developer must manually call `Acknowledgment.acknowledge()` inside the listener code.

---

## Advanced Level Questions

### Q21. Explain Zookeeper's role (and KRaft). [Hard]
**Answer:** Traditionally, Kafka used Zookeeper to manage cluster metadata, track broker health, and perform partition leader elections. However, modern Kafka (v2.8+) introduced KRaft (Kafka Raft), which removes the Zookeeper dependency entirely, moving metadata management directly into the Kafka brokers themselves for better scalability.

### Q22. How do you achieve Exactly-Once Semantics (EOS) in Kafka? [Hard]
**Answer:** EOS guarantees that a message is processed exactly once, avoiding duplicates (At-Least-Once) or data loss (At-Most-Once).
1. **Producer**: Set `enable.idempotence=true`. The producer assigns a sequence number to messages, allowing the broker to ignore retried duplicates.
2. **Consumer/Processor**: Use Kafka Transactions (`spring.kafka.producer.transaction-id-prefix`). If reading from Topic A, updating a database, and writing to Topic B, you must wrap all operations in a distributed transaction to ensure either everything commits or everything rolls back.

### Q23. What happens if you have more Consumers in a group than Partitions in a topic? [Hard]
**Answer:** The extra consumers will sit **idle**. A single partition can only be assigned to *one* consumer instance within a specific consumer group. If you have 4 partitions and 5 consumers, 4 consumers will read data, and 1 will do nothing.

### Q24. Explain Consumer Rebalancing. [Hard]
**Answer:** Rebalancing occurs when a consumer joins the group, leaves the group, or crashes. Kafka temporarily pauses consumption, revokes all partition assignments, and recalculates which consumer gets which partitions. Frequent rebalancing (e.g., caused by consumers taking too long to process and timing out via `max.poll.interval.ms`) causes severe latency spikes and duplicate processing.

### Q25. How do you handle a slow consumer that triggers constant rebalances? [Hard]
**Answer:** 
1. Increase `max.poll.interval.ms` (giving the consumer more time to process).
2. Decrease `max.poll.records` (fetching fewer records per poll so the batch finishes faster).
3. Move heavy processing to a separate thread pool outside the Kafka polling thread (but be careful with manual offset commits).

### Q26. How do you dynamically start and stop `@KafkaListener`s at runtime? [Hard]
**Answer:** You assign an `id` to the `@KafkaListener`. You can then inject `KafkaListenerEndpointRegistry` into a controller or service, look up the listener by its ID, and call `.pause()`, `.resume()`, `.start()`, or `.stop()` on the message listener container.

### Q27. What are Kafka Headers and how are they used in Spring? [Hard]
**Answer:** Similar to HTTP headers, Kafka messages can contain key-value pairs (bytes) as metadata, separate from the payload. Spring Kafka uses headers extensively for routing (e.g., identifying the payload class type for JSON deserialization) and distributed tracing (e.g., Sleuth/Zipkin IDs). You can access them using `@Header("my-header") String headerVal`.

### Q28. How do you write Integration Tests for Spring Kafka? [Hard]
**Answer:** Using **Embedded Kafka**. You annotate your test class with `@EmbeddedKafka`. Spring boots up a lightweight, fully functional Kafka broker in memory specifically for the test. You can produce messages and use `Awaitility` or `CountDownLatch` to wait and verify that your `@KafkaListener` processed them.

### Q29. What is the `KafkaTransactionManager`? [Hard]
**Answer:** It integrates Kafka transactions with Spring's `@Transactional` annotation. If a method is annotated with `@Transactional("kafkaTransactionManager")`, the `KafkaTemplate` will only commit the message to the Kafka broker if the method finishes without exceptions. If an exception occurs, the message is aborted (consumers using `isolation.level=read_committed` will never see it).

### Q30. Explain Log Compaction in Kafka. [Hard]
**Answer:** By default, Kafka deletes messages based on time (e.g., 7 days). Log Compaction is an alternative retention policy. Instead of deleting by time, Kafka retains only the *most recent message for a specific Key*. It deletes older messages with the same key. This is incredibly useful for storing the "current state" of a system (e.g., the current price of a stock, where you don't care about the price 5 days ago, only the latest one).
