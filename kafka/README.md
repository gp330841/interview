# Apache Kafka Deep-Dive Q&A

This document compiles detailed questions, scenarios, and comprehensive answers for Apache Kafka concepts, based on the outlines in [Kafka.text](file:///Users/yogeshwarpatel/Workspace/interview/kafka/Kafka.text).

---

## 1. Consumer Lag & Backpressure

* **Scenario:** Your consumer group is lagging behind heavily in production.
* **Questions:**
  * How do you identify the root cause of lag?
  * What metrics/tools will you check (e.g., offsets, throughput)?
  * How would you fix it without downtime?
* **What to expect:**
  * Lag monitoring (Burrow, Kafka Exporter, Dynatrace if integrated)
  * Partition vs consumer count mismatch
  * Slow processing / I/O bottleneck
  * Batch processing vs real-time tuning

### Answer
1. **Identifying Root Cause:**
   * **Downstream I/O Bottlenecks:** The consumer spends too much time processing each record (e.g., waiting on slow SQL database queries, HTTP API timeouts, or sync file writes).
   * **Resource Contention:** The consumer host is CPU/Memory bound, or the JVM is suffering from long, stop-the-world Garbage Collection (GC) pauses.
   * **Partition vs Consumer Mismatch:** There are fewer partitions than consumers (leaving some consumers idle) or too many partitions per consumer, overloading a single consumer instance.
   * **Sudden Traffic Spike:** The production rate has temporarily outpaced the maximum consumption capacity of the consumer group.
2. **Metrics & Tools to Check:**
   * **Tools:** `kafka-consumer-groups.sh` CLI utility, **Burrow** (for lag pattern evaluations), or Prometheus + Grafana populated by the **Kafka Exporter**.
   * **Metrics:** 
     * `records-lag` (per partition) and `records-lag-max` (the maximum lag across all partitions in the group).
     * `records-consumed-rate` (consumer throughput) compared against broker `BytesInPerSec` / `MessagesInPerSec`.
     * `jvm.gc.pause` to check for GC pauses on the consumer application.
3. **Fixing Without Downtime:**
   * **Horizontal Scaling:** Increase the number of consumers in the group. *Note:* You can only scale consumers up to the number of partitions. If you need more, you must first increase the partition count of the topic and then scale up the consumers.
   * **Vertical / Batch Tuning:**
     * Increase `max.poll.records` to fetch larger batches of records if downstream processing can handle them efficiently.
     * Tune consumer fetch settings: `fetch.min.bytes` and `fetch.max.wait.ms` to batch network reads.
   * **Asynchronous Processing:** Re-architect the consumer internal loop to delegate processing of fetched records to an internal worker thread pool (e.g., using `ExecutorService` or Actor systems) while keeping the main poll loop running to avoid triggering partition rebalances. Offsets must be managed carefully (only committed once all tasks in a batch are complete).

---

## 2. Partition Strategy

* **Scenario:** You have an Orders topic with uneven load (some partitions overloaded).
* **Questions:**
  * Why does this happen?
  * How would you redesign partitioning?
  * What key would you choose?
* **Expected depth:**
  * Key-based partitioning issues (hot keys)
  * Custom partitioner vs random
  * Trade-off: ordering vs scalability

### Answer
1. **Why It Happens:**
   * **Hot Keys:** Using the default MurmurHash-based key partitioner when a few specific keys have highly skewed event distributions (e.g., a massive client or "VIP" seller generating 80% of the orders).
   * **Low Cardinality Key:** Selecting a key with too few distinct values (like `countryCode` or `orderStatus`) causes many unrelated keys to hash onto the same partition.
2. **Redesigning Partitioning:**
   * **Increase Key Cardinality:** Change the partition key to a highly unique ID such as `orderId` or `transactionId`.
   * **Salting the Key:** Append a random suffix (a "salt") to keys known to be hot (e.g., `client123_0`, `client123_1`). This spreads client123's orders across multiple partitions, though the consumer will have to re-sequence them if ordering is critical.
   * **Custom Partitioner:** Implement a custom `Partitioner` class that detects hot keys and distributes them dynamically across a wider range of partitions, while routing normal keys standardly.
   * **Null Key (Round-Robin/Sticky):** If order-level sequence guarantees are unnecessary, set the key to `null` to let the default partitioner distribute load evenly using a sticky round-robin strategy.
3. **Key Choice Recommendation:**
   * Choose `orderId` or `transactionId` to ensure optimal load distribution. If ordering is required per client account, use `accountId` but monitor for load skew.

---

## 3. Message Ordering Guarantee

* **Scenario:** Your system requires strict ordering for a user’s transactions.
* **Questions:**
  * How will Kafka ensure ordering?
  * What happens if you increase partitions later?
  * Can ordering break?
* **Expected answer:**
  * Ordering only within partition
  * Same key → same partition
  * Increasing partitions breaks ordering guarantees

### Answer
1. **How Kafka Ensures Ordering:**
   * Kafka only guarantees order **within a single partition**, not globally across the entire topic.
   * By choosing a consistent partition key (such as `userId` or `accountId`), Kafka ensures all transactions for a specific user go to the **same partition**.
   * On the producer side, you must configure:
     * `enable.idempotence=true` (which handles sequence numbers on the broker).
     * `max.in.flight.requests.per.connection=5` (or `1` if idempotence is disabled) to prevent out-of-order writes on retries.
     * `acks=all` to guarantee durable writes.
2. **Increasing Partitions Later:**
   * Increasing partitions modifies the hashing pool size (formula: `hash(key) % partition_count`).
   * When partitions are added, a key `K` that previously hashed to partition 1 might now hash to partition 3. New transactions will be written to partition 3, while older messages may still be sitting in partition 1 or are in flight, breaking the strict sequence.
3. **How Ordering Can Break:**
   * Changing partition count (re-partitioning).
   * Producer retrying failed writes without setting `enable.idempotence=true`.
   * Consumer processing logic using multi-threaded pools to process single partition records asynchronously without sorting/pinning by key.

---

## 4. Retry & Failure Handling

* **Scenario:** Consumer fails while processing a message.
* **Questions:**
  * How do you retry?
  * What is DLQ (Dead Letter Queue)?
  * How to avoid infinite retry loops?
* **Expected:**
  * Retry topics / exponential backoff
  * DLQ pattern
  * Idempotent consumers

### Answer
1. **How to Retry:**
   * **Blocking Retry:** Keep retrying the processing of the current record locally within the consumer. *Trade-off:* This blocks processing of all subsequent messages in that partition. Recommended only for transient, short-lived errors.
   * **Non-blocking Retry (Retry Topics):** Publish the failed record to a dedicated retry topic (e.g., `orders-retry-5m`), commit the offset on the main topic, and continue. A separate consumer group processes the retry topic, potentially using a delay.
   * **Exponential Backoff:** If the retry fails, route it through multiple retry topics with escalating backoffs (e.g., `orders-retry-15m`, `orders-retry-1h`) before dropping it to the DLQ.
2. **Dead Letter Queue (DLQ):**
   * A DLQ is a Kafka topic (e.g., `orders-dlq`) designated for unprocessable messages ("poison pills") that fail after exhausting all retry policies. Developers can inspect, fix, and manually replay these messages later.
3. **Avoiding Infinite Loops:**
   * Maintain a retry count in the Kafka record headers (e.g., `x-retry-count`). Once the limit is breached, bypass retries and route directly to the DLQ.
   * Implement class-specific exception handling: Fail-fast and immediately send permanent errors (e.g. JSON parsing errors) straight to the DLQ, only retrying transient network/resource errors.

---

## 5. Exactly Once vs At Least Once

* **Scenario:** Your payment system cannot process duplicate transactions.
* **Questions:**
  * Which delivery semantics will you use?
  * How will you implement it?
* **Expected:**
  * Idempotent producer + transactions
  * Consumer-side deduplication

### Answer
1. **Delivery Semantics:**
   * **Exactly-Once Semantics (EOS)** is required.
2. **Implementation Strategy:**
   * **Producer Configuration:**
     * `enable.idempotence=true` (prevents duplicates caused by network retries).
     * Configure a unique `transactional.id` on the producer.
   * **Transactional Operations:**
     * Coordinate transactions using `beginTransaction()`, `sendOffsetsToTransaction()`, and `commitTransaction()` to execute producing records and committing consumption offsets as an atomic transaction.
   * **Consumer Configuration:**
     * Set `isolation.level=read_committed` so consumers only see committed transactional messages.
   * **Downstream Deduplication:**
     * EOS only covers Kafka-to-Kafka boundaries. If the consumer writes to an external system (e.g., a SQL database), you must implement idempotent operations (e.g., database unique constraints on transaction UUIDs or `upsert` queries) to avoid duplicate records if the consumer crashes before offset commits.

---

## 6. ISR Shrink Scenario

* **Scenario:** One broker goes down, ISR shrinks.
* **Questions:**
  * What happens internally?
  * What if ISR < min.insync.replicas?
  * What happens with acks=all?
* **Expected:**
  * Producer gets error if ISR < min.insync.replicas
  * Data safety vs availability trade-off

### Answer
1. **Internal Mechanism:**
   * When a replica broker goes down, it stops sending fetch requests to the partition leader.
   * If the replica stays out of sync for longer than `replica.lag.time.max.ms`, the leader removes the replica from the In-Sync Replicas (ISR) list.
   * The leader updates the metadata controller (ZooKeeper or KRaft metadata), which propagates the new ISR state to all brokers and clients.
2. **If ISR < min.insync.replicas:**
   * The partition becomes **unavailable for writes** that require acknowledgment from all in-sync replicas (`acks=all` / `acks=-1`).
   * The broker returns a `NotEnoughReplicasException` (or `NotEnoughReplicasAfterAppendException`) to the producing client.
   * Consumers can still read existing data from the leader.
3. **Behavior with acks=all:**
   * If `acks=all` is set:
     * If the current `ISR` size is equal to or greater than the `min.insync.replicas` threshold, the write succeeds.
     * If the current `ISR` size falls below `min.insync.replicas`, the write fails, rejecting updates to safeguard data durability at the cost of partition availability.

---

## 7. High Throughput Optimization

* **Scenario:** System needs to process 1M messages/sec.
* **Questions:**
  * How do you scale Kafka?
  * Producer tuning?
  * Broker tuning?
* **Expected:**
  * Increase partitions
  * Batch.size, linger.ms tuning
  * Compression (snappy/lz4)
  * Horizontal scaling

### Answer
1. **Scaling Kafka Infrastructure:**
   * **Partition Count:** Set a high partition count to partition the topic's data across multiple brokers, enabling a larger number of parallel consumer instances.
   * **Horizontal Scaling:** Add more brokers to spread partition leadership, disk IO, and network interfaces.
2. **Producer Tuning:**
   * **Batching:** Increase `batch.size` (e.g., to 64KB or 128KB) and set `linger.ms` (e.g., 10-50ms) to allow the producer to pool messages into larger TCP payloads.
   * **Compression:** Enable fast compression formats like `compression.type=snappy` or `lz4` to reduce network and disk storage footprint.
   * **Acks:** Set `acks=1` if minimal data loss is acceptable for latency, though `acks=all` remains the safest default.
   * **Buffer Size:** Set a larger `buffer.memory` so the producer doesn't block local threads when Kafka is under high load.
3. **Broker Tuning:**
   * Rely on SSD or RAID 10 storage with page cache configurations.
   * Increase system thread handling: `num.network.threads` and `num.io.threads`.
   * Allocate proper heap size (usually 6-8GB) leaving the rest of system memory for the OS Page Cache.

---

## 8. Rebalancing Impact

* **Scenario:** Consumers frequently join/leave causing rebalancing.
* **Questions:**
  * What happens during rebalance?
  * Why is it problematic?
  * How to reduce impact?
* **Expected:**
  * Stop-the-world pause
  * Use cooperative rebalancing
  * Sticky assignor

### Answer
1. **What Happens:**
   * The group coordinator revokes current partition assignments from group members and re-evaluates partition distribution based on the active consumer list.
2. **Why It Is Problematic:**
   * **Latency/Lag Spikes:** Eager rebalances trigger a "stop-the-world" pause where all consumers stop reading, causing lag to accumulate.
   * **Duplicate Work:** If consumers fail to commit offsets before partition revocation, their replacements will read and process duplicate records.
3. **Reducing Rebalance Impact:**
   * **Cooperative Rebalancing:** Use `CooperativeStickyAssignor` (enabled by default in modern versions) to incrementally move partitions, leaving unaffected consumers active.
   * **Static Membership:** Assign unique `group.instance.id` tags to consumer instances. If a consumer restarts within `session.timeout.ms`, its partition remains assigned, bypassing the rebalance entirely.
   * **Heartbeat & Poll Tuning:** Ensure `max.poll.interval.ms` is long enough to handle large message batch processing times, avoiding false-positive death detections.

---

## 9. Data Loss Scenario

* **Scenario:** Messages are missing in production.
* **Questions:**
  * Where could data loss happen?
  * How to prevent it?
* **Expected:**
  * acks=0 / acks=1 risk
  * Unclean leader election
  * Producer retries disabled

### Answer
1. **Where Data Loss Occurs:**
   * **Producer configurations:**
     * `acks=0` (producer doesn't wait for acknowledgment).
     * `acks=1` (leader acknowledges write, but crashes before replicating to followers).
     * `retries=0` (transient network issues discard messages).
   * **Broker configurations:**
     * `unclean.leader.election.enable=true` (allows out-of-sync followers to take over as leaders, deleting un-replicated commits on the old leader).
   * **Consumer configurations:**
     * `enable.auto.commit=true` (offsets are committed automatically before processing completes; if the consumer crashes mid-processing, those records are lost to the consumer).
2. **Prevention Strategy:**
   * Set `acks=all` on the producer.
   * Set `min.insync.replicas=2` (paired with a replication factor of 3).
   * Set `unclean.leader.election.enable=false`.
   * Enable `enable.idempotence=true` and allow retries on the producer.
   * Disable auto-commit on the consumer (`enable.auto.commit=false`), manually committing offsets only *after* success downstream.

---

## 10. Schema Evolution (Kafka + Avro)

* **Scenario:** You need to change message schema.
* **Questions:**
  * How do you ensure backward compatibility?
  * What happens if consumer is not updated?
* **Expected:**
  * Schema Registry
  * Compatibility modes (backward/forward/full)

### Answer
1. **Ensuring Compatibility:**
   * Use a **Schema Registry** (e.g., Confluent Schema Registry) to manage schema versions.
   * Enforce compatibility configurations:
     * **BACKWARD:** Consumers with the new schema can read old schema messages (e.g., adding an optional field with a default value).
     * **FORWARD:** Consumers with the old schema can read new schema messages (e.g., removing a field that had a default value).
     * **FULL:** Schema is both backward and forward compatible.
2. **If Consumer Is Not Updated:**
   * If a producer upgrades to a backward-compatible schema, the un-updated consumer continues processing historical fields without issues.
   * If compatibility rules are violated or not enforced, the consumer's deserializer throws a `SerializationException`, causing a poison pill that blocks consumer partition processing.

---

## 11. Multi-DC / Disaster Recovery

* **Scenario:** Your Kafka cluster goes down completely.
* **Questions:**
  * How do you design DR?
  * What tools?
* **Expected:**
  * MirrorMaker 2
  * Active-active vs active-passive

### Answer
1. **DR Design:**
   * **Active-Passive (Warm Standby):** The primary cluster handles all traffic. A secondary DR cluster is kept in sync. If the primary fails, the DNS/traffic is failed over to the passive cluster.
   * **Active-Active:** Both clusters actively process regional traffic, replicating topic streams bidirectionally. Care must be taken to prevent endless routing loops (using topic prefixes).
2. **Replication Tools:**
   * **MirrorMaker 2 (MM2):** Bundled engine using Kafka Connect to replicate data, consumer offsets, and topic configurations.
   * **Confluent Replicator / Cluster Linking:** Enterprise alternatives providing low-latency, kernel-level cluster replication.

---

## 12. Kafka vs Traditional Queue (Deep Insight)

* **Scenario:** Why Kafka over RabbitMQ for event streaming?
* **Expected:**
  * Pull vs push
  * Retention vs deletion
  * Replay capability

### Answer
* **Pull vs Push:** RabbitMQ pushes messages to consumers (can overload them if not carefully throttled). Kafka uses a pull model where consumers request batches of records at their own speed, handling backpressure naturally.
* **Retention vs Deletion:** RabbitMQ deletes messages immediately upon acknowledgment. Kafka retains messages on disk according to time/size policies, allowing multiple consumers to read the same stream.
* **Replay Capability:** Kafka consumers can reset offsets and replay historical data (ideal for recovery, analytics, or auditable trails). RabbitMQ lacks replay capabilities.
* **Scalability:** Kafka is built for sequential file append and leverages page cache optimization, providing significantly higher write throughput.

---

## 13. Exactly Once Internals (Advanced Deep Dive)

* **Scenario:** Explain how Kafka achieves exactly-once.
* **Expected:**
  * Idempotent producer (producer ID + sequence)
  * Transactions (write + offset commit atomically)

### Answer
* **Idempotent Producer:**
  * The broker assigns a unique **Producer ID (PID)**.
  * Every batch sent is marked with a **Sequence Number**.
  * The broker tracks the highest sequence number written per PID and ignores duplicate writes.
* **Transactional Coordinator:**
  * The producer coordinates with a Transaction Coordinator broker, writing state transitions to the `__transaction_state` topic.
  * When a transaction commits, the coordinator writes a commit marker (control record) to target partitions.
  * Consumers running under `isolation.level=read_committed` buffer uncommitted records locally, only exposing records to the application when the commit marker is encountered.

---

## 14. Compaction vs Retention

* **Scenario:** You want latest state per key.
* **Questions:**
  * What will you use?
  * How does compaction work?
* **Expected:**
  * Log compaction keeps latest key

### Answer
1. **Selection:**
   * Use **Log Compaction** (`cleanup.policy=compact`).
2. **How Compaction Works:**
   * The partition log is split into the **Clean** segment (compacted) and the **Dirty** segment (new logs).
   * A background Log Cleaner thread scans dirty segments and removes older offsets for keys that already exist in the clean segment.
   * To delete a key entirely, the producer writes a message with a `null` payload (called a **tombstone** marker). The cleaner eventually purges the tombstone after `delete.retention.ms`.

---

## 15. Consumer Offset Management

* **Scenario:** Consumer crashes after processing but before committing offset.
* **Questions:**
  * What happens?
  * How to handle duplicates?
* **Expected:**
  * At-least-once behavior
  * Idempotent processing

### Answer
1. **What Happens:**
   * The coordinator triggers a rebalance and assigns the partition to another consumer.
   * The new consumer reads the last committed offset from `__consumer_offsets` and starts reading from there.
   * Because the crashed consumer failed to commit, the new consumer re-reads and processes the already-processed message, leading to **At-Least-Once** behavior.
2. **Handling Duplicates:**
   * Build **idempotent consumers**:
     * Use unique database keys (e.g., `PRIMARY KEY (transaction_id)`) to fail or ignore duplicate writes.
     * Maintain an external distributed cache (e.g. Redis) checking if a message UUID has already been successfully processed.

---

## 🔴 Expert-Level Trick Questions

### 16. "If partitions < consumers, what happens?"
* **Answer:**
  * The extra consumers will remain **idle** (they will not be assigned any partitions) since Kafka guarantees that a partition is assigned to a maximum of one consumer in a consumer group to enforce order.

### 17. "If consumers < partitions?"
* **Answer:**
  * Some active consumers will be assigned **multiple partitions** to process the load.

### 18. "Can Kafka guarantee global ordering?"
* **Answer:**
  * ❌ **No.** Kafka only guarantees ordering within a single partition. Global ordering across multiple partitions is not supported.

### 19. "What happens if leader dies before follower sync?"
* **Answer:**
  * **Data loss is possible.**
  * If `unclean.leader.election.enable=false`, the partition goes offline until an in-sync replica recovers. No data is lost, but partition writes are blocked.
  * If `unclean.leader.election.enable=true`, a out-of-sync replica is elected as leader, resulting in the loss of un-replicated records.

### 20. "Why not use single partition for ordering?"
* **Answer:**
  * A single partition limits your topic's maximum scaling potential. The throughput is limited to what a single broker partition and a single consumer thread can process, creating a scalability bottleneck.

---

## 21. How do you choose partition key?

* **Expected answer:**
  * Partition key depends on ordering and load distribution.
* **Example:**
  * `accountRequestId` → ordering per account request
  * `clientId` → ordering per client
  * `advisorId` → may cause hot partitions if some advisors are very active
* **Trade-off:**
  * Ordering requirement vs scalability

### Answer
* **Cardianlity and Distribution:** Select a key that has high cardinality to distribute data evenly across all partitions (e.g., `orderId`, `userId`).
* **Ordering Requirements:** If records must be processed in sequence (e.g., financial transactions for a single account), you must group them under the same partition key (e.g., `accountId`).
* **Skew Avoidance:** If grouping by a business entity (like `clientId` or `advisorId`) is required, analyze if any single entity produces disproportionately high traffic (creating a hot partition). If skew is present, apply key salting techniques to spread the load.
