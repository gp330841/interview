# Amazon DynamoDB Deep Dive

This module covers the advanced partition management, indexing strategies, consistency models, and single-table design concepts of Amazon DynamoDB.

---

## 1. Under the Hood: Partition Allocation & Limits

DynamoDB is a distributed, fully-managed NoSQL database. To scale horizontally, it splits data across physical storage units called **Partitions**.

### A. Partition Allocation Calculation
DynamoDB determines the number of partitions required for your table based on storage volume and provisioned throughput capacity. A single partition has the following hard limits:
*   **Max Storage**: **10 GB** of data.
*   **Max Write Capacity**: **1,000 WCUs** (1 WCU = 1 KB write per second).
*   **Max Read Capacity**: **3,000 RCUs** (1 RCU = 4 KB strongly consistent read per second).

DynamoDB calculates the number of partitions ($N$) using:

$$N = \max\left(\frac{\text{Total Storage}}{10\text{ GB}}, \frac{\text{Total RCUs}}{3,000} + \frac{\text{Total WCUs}}{1,000}\right)$$

*   *Example*: If you provision a table with 6,000 RCUs and 2,000 WCUs, and it stores 8 GB of data:
    *   Throughput partitions: $\frac{6000}{3000} + \frac{2000}{1000} = 2 + 2 = 4$ partitions.
    *   Storage partitions: $\frac{8}{10} = 0.8 \rightarrow 1$ partition.
    *   *Result*: DynamoDB allocates $\max(1, 4) = 4$ partitions. Your provisioned throughput is distributed evenly across these partitions (each partition gets 1,500 RCUs and 500 WCUs).

### B. Hot Partitions & Partition Key Sharding
If your application directs a disproportionate volume of traffic to a single partition key (e.g., a flash sale on a popular item ID or a celebrity's user ID), that specific partition will exceed its 1,000 WCU or 3,000 RCU limit.
*   *Symptom*: SQS/Client receives `ProvisionedThroughputExceededException` (throttling), even if the table's *total* capacity is underutilized.
*   *Mitigation (Write Sharding)*: Add a random or calculated suffix to the partition key during writes (e.g., `ITEM_123` becomes `ITEM_123.0` through `ITEM_123.9`). Reads must query all 10 shard keys and aggregate results.

---

## 2. Reads, Writes, and Indexing

### A. Read Consistency Levels
1.  **Eventually Consistent Reads (Default)**: Returns data from one of the three replica nodes. It might reflect stale data if a write is still propagating.
    *   *Cost*: **0.5 RCU** per 4 KB read.
2.  **Strongly Consistent Reads**: Queries all replica nodes and returns the latest committed data.
    *   *Cost*: **1 RCU** per 4 KB read.
3.  **Transactional Reads / Writes (`TransactGetItems` / `TransactWriteItems`)**: All-or-nothing operations spanning up to 100 items or 4 MB of data. Uses two-phase commit internally.
    *   *Cost*: **2 RCUs / WCUs** per 4 KB / 1 KB.

### B. LSI vs. GSI
Indexes allow querying data using attributes other than the table's primary key.

| Feature | Local Secondary Index (LSI) | Global Secondary Index (GSI) |
| :--- | :--- | :--- |
| **Partition Key** | Must be the **same** as the table's partition key. | Can be **different** from the table's partition key. |
| **Sort Key** | Must be different from the table's sort key. | Can be any attribute. |
| **When to Create** | **Only at table creation**. Cannot be added later. | **Any time** (can add, update, or delete on the fly). |
| **Throughput Capacity** | Shares the provisioned RCU/WCU of the parent table. | Requires its own provisioned RCU/WCU. |
| **Consistency** | Supports Strongly or Eventually Consistent reads. | Supports **Eventually Consistent reads only**. |
| **Size Limit** | Local secondary indexes are limited to **10 GB** per partition key. | No size limit. |

---

## 3. Advanced Design Patterns & Features

### A. DynamoDB Streams
Captures time-ordered write activity (Create, Update, Delete) on your DynamoDB table.
*   **Classic Streams**: Stores logs for **24 hours**. Native integration with AWS Lambda to process changes (e.g., triggering email on user sign-up).
*   **Kinesis Data Streams Integration**: Routes logs directly to a Kinesis stream. Data can be stored for up to **365 days** and loaded into S3 or Redshift via Firehose.

### B. Time to Live (TTL)
*   **Mechanism**: Automatically deletes expired items based on a timestamp attribute (epoch time in seconds).
*   *Cost*: **Free**. Deletions are performed asynchronously by AWS background processes (usually within 48 hours of expiration) and **do not consume your table's write capacity**.

### C. Single-Table Design
In relational databases, you normalize data and join tables. In DynamoDB, joining tables is not supported.
*   **Concept**: Storing multiple distinct entity types (e.g., Users, Orders, Products) in a **single table**.
*   **Overloaded Keys**: Table columns are named generically (e.g., Partition Key = `PK`, Sort Key = `SK`).
*   *Example*:
    *   User entity: `PK = USER#123`, `SK = PROFILE`
    *   Order entity: `PK = USER#123`, `SK = ORDER#9876`
*   *Benefit*: Allows fetching a user and all their recent orders in a **single query request** (using `Query` on `PK = USER#123`), achieving single-digit millisecond latency without joins.

### D. Optimistic Locking
To prevent concurrent writes from overwriting each other, use DynamoDB's conditional writes.
*   **Mechanism**: Add a version attribute (e.g., `version_num`) to your items.
*   **Write Command**:
    ```json
    {
      "UpdateExpression": "SET val = :newVal, version_num = version_num + 1",
      "ConditionExpression": "version_num = :expectedVersion"
    }
    ```
*   If another process updated the item in the meantime, the `ConditionExpression` fails, raising a `ConditionalCheckFailedException`. The application catches the exception, re-reads the fresh item, and retries.

---

## 4. Standard Interview Questions

#### Q1: If a GSI has insufficient Write Capacity Units (WCUs) compared to the main table, what happens to writes on the main table?
If the GSI cannot handle the write rate of the main table, DynamoDB will apply **backpressure** and throttle writes on the **main table**, even if the main table has plenty of WCUs.
*   *Why?* GSI replication is asynchronous, but DynamoDB protects the system from replication lag growing infinitely by slowing down main table writes. Always ensure GSIs have equal or greater WCU capacity than the base table.

#### Q2: When would you use Scan vs. Query in DynamoDB?
*   **Query**: Searches based on the Partition Key and optionally filters via the Sort Key. Highly efficient because it targets a specific partition.
*   **Scan**: Traverses the entire table, reading every physical partition. Extremely inefficient and expensive.
*   *Rule*: **Avoid Scans in production code**. Use Queries and Secondary Indexes instead.

#### Q3: How do you handle relationships (like many-to-many) in DynamoDB?
Use **Adjacency Lists** in a Single-Table Design.
*   Create an index where the partition key is the target entity (e.g., `USER#123`) and the sort key represents the relationship (e.g., `GROUP#456`).
*   To represent a User belonging to multiple Groups and a Group containing multiple Users, write two records (a link record for both directions) in the table, or use a GSI to invert the primary keys (`PK` becomes `SK`, `SK` becomes `PK`).
