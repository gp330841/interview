# SQS & SNS Messaging Systems Deep Dive

This module covers the advanced architecture of Amazon SQS (Simple Queue Service) and Amazon SNS (Simple Notification Service) for event-driven microservices.

---

## 1. Amazon SQS Internals

SQS is a highly reliable, distributed message queuing service.

### A. Distributed Queue Architecture
SQS does not store messages on a single server. Instead, it replicates messages across multiple redundant servers in the AWS region to ensure durability.
*   *Symptom*: When polling a Standard queue, SQS queries a subset of these servers and returns a batch of messages. Because of this distributed layout, SQS Standard cannot guarantee strict ordering or single-delivery.

### B. Standard vs. FIFO Queues
*   **Standard Queue**:
    *   *Throughput*: Nearly unlimited API calls/sec.
    *   *Delivery*: At-least-once. A message is occasionally delivered twice because of distributed replication.
    *   *Ordering*: Best-effort. Messages can arrive out of sequence.
*   **FIFO (First-In-First-Out) Queue**:
    *   *Throughput*: Limited to 300 transactions/sec (or 3,000/sec with batching).
    *   *Delivery*: Exactly-once processing.
    *   *Ordering*: Strict ordering within specific logical groups.

### C. FIFO Partitioning: Message Group IDs vs. Deduplication IDs
To run FIFO queues successfully in production, you must understand these two parameters:
*   **Message Group ID**: The tag that specifies the logical partition key for the message.
    *   *How it works*: SQS FIFO guarantees that messages with the **same Message Group ID are processed sequentially** in order. However, messages with **different Message Group IDs can be processed in parallel** by separate consumers.
    *   *Interview Tip*: If you set the Message Group ID to a static value (e.g., `GROUP_1`) for all messages, you limit your queue processing throughput to a single consumer stream (300 tps). Instead, use dynamic keys like `CustomerId` or `OrderId` to maximize concurrent processing.
*   **Message Deduplication ID**: The token used to prevent duplicate messages from being processed.
    *   *How it works*: If a publisher sends two messages with the same Deduplication ID to SQS within a **5-minute deduplication window**, SQS will accept the second message but discard it (returning success to the sender without writing it to the queue).

### D. Visibility Timeout
When a consumer receives a message from SQS, the message remains in the queue but is hidden from other consumers. The duration of this state is the **Visibility Timeout**.

```
[Message Sent] ---> [ Queue ]
                      |
                   [Received by Consumer A] (Hides message for 30s)
                      |
         +------------+------------+
         |                         |
  (Processes < 30s)         (Processes > 30s)
  Consumer A deletes        Message becomes visible.
  message from queue.       Consumer B receives & processes.
                            Result: Duplicate processing!
```

*   **The Bug**: If processing a message takes 45 seconds but your visibility timeout is set to 30 seconds, SQS will make the message visible again. Another consumer will pick it up, resulting in **duplicate processing**.
*   **The Solution**: Set the visibility timeout to 6 times your maximum processing time. Additionally, your consumer code can call the `ChangeMessageVisibility` API dynamically to extend the timeout if it detects a long-running transaction.

### E. Short Polling vs. Long Polling
*   **Short Polling (Default)**: SQS samples a subset of its physical servers and returns immediately, even if it finds zero messages. This leads to high API call counts and increased costs.
*   **Long Polling (`ReceiveMessageWaitTimeSeconds > 0`, up to 20s)**: SQS queries all servers and waits for a message to arrive before returning.
    *   *Benefits*: Reduces empty responses (saving money), decreases CPU polling loops in your client code, and returns messages faster.

---

## 2. Amazon SNS Internals

SNS is a managed, push-based Pub/Sub messaging service.

### A. Subscription Filter Policies
By default, every subscriber to an SNS topic receives every message published to that topic.
*   **Filter Policies**: JSON structures defined on a subscription that inspect message attributes. SNS evaluates these attributes and only pushes matching messages to the subscriber.
*   *Benefit*: Simplifies consumer code and eliminates execution cost.
    *   *Example*: An Inventory service subscription to the `OrderEvents` topic might filter on `{"event_type": ["order_placed"]}`, ignoring `order_viewed` or `cart_abandoned` events.

### B. Raw Message Delivery
By default, SNS wraps the message payload in a JSON metadata envelope containing parameters like `TopicArn`, `MessageId`, and `Timestamp`.
*   **Raw Message Delivery**: When enabled, SNS strips the JSON metadata wrapper and delivers the exact plaintext payload to the destination (e.g., SQS or HTTP endpoint). This is highly useful for forwarding payloads to systems that cannot parse SNS metadata envelopes.

### C. SNS FIFO Topics
SNS supports FIFO topics, enabling end-to-end ordering.
*   **FIFO Integration**: You can publish messages to an SNS FIFO topic, which routes them to SQS FIFO queue subscribers. It guarantees ordering and deduplication throughout the entire publish-subscribe pipeline.

---

## 3. Standard Interview Questions

#### Q1: What is an SQS Dead-Letter Queue (DLQ) and what is a Redrive Policy?
*   **DLQ**: A separate SQS queue where messages are routed after failing to process successfully a specified number of times.
*   **Redrive Policy**: The configuration on the source queue that defines:
    1.  The target DLQ ARN.
    2.  The `maxReceiveCount` (the number of times a message is delivered and returned to the queue before SQS moves it to the DLQ).
*   *Purpose*: Prevents corrupted messages (poison pills) from looping indefinitely and consuming processing cycles.

#### Q2: How do you implement a fan-out pattern with SQS and SNS?
Attach multiple SQS queues as subscribers to a single SNS Topic.
*   When a message is published to the SNS Topic, SNS replicates and pushes the message to all SQS queues simultaneously.
*   Each SQS queue acts as a private buffer for a distinct microservice (e.g., one queue for Billing, one for Shipping, one for Analytics), allowing them to process the event independently and at their own pace.

#### Q3: Under what circumstances can SQS FIFO deliver duplicate messages?
SQS FIFO guarantees exactly-once delivery within its 5-minute deduplication window. However, duplicates can occur if:
1.  The publisher fails to provide a unique `MessageDeduplicationId` and fails to enable content-based deduplication.
2.  The publisher retries sending a message *after* the 5-minute deduplication window has expired.
3.  The consumer processes the message but crashes before calling the `DeleteMessage` API, causing the visibility timeout to expire and SQS to deliver it again.
