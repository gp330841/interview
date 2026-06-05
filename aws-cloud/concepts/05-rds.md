# RDS & Amazon Aurora Deep Dive

This module covers the database engine architectures, replication mechanics, and storage models of Amazon Relational Database Service (RDS) and Amazon Aurora.

---

## 1. Amazon RDS Architecture

RDS provides managed relational database engines (PostgreSQL, MySQL, MariaDB, SQL Server, Oracle).

### A. High Availability vs. Scale: Multi-AZ vs. Read Replicas
Understanding the difference between synchronous failover and asynchronous scaling is a baseline database architecture requirement.

```
+-------------------------------------------------------------------------+
|                              Amazon RDS                                 |
|                                                                         |
|         [Availability Zone 1]              [Availability Zone 2]        |
|                                                                         |
|       +-----------------------+          +-----------------------+      |
|       |  Primary DB Instance  |          |  Standby DB Instance  |      |
|       +-----------------------+          +-----------------------+      |
|                   |                                  ^                  |
|                   +-----(Sync Block Replication)-----+                  |
|                   |                                                     |
|            (Async DB Engine replication)                                |
|                   v                                                     |
|       +-----------------------+                                         |
|       |   Read Replica DB     |                                         |
|       +-----------------------+                                         |
+-------------------------------------------------------------------------+
```

*   **Multi-AZ (Disaster Recovery)**:
    *   *Mechanism*: Writes to the primary DB instance are synchronously replicated at the storage volume level to a passive standby instance in a different AZ.
    *   *Failover*: If the primary AZ experiences a power outage or hardware failure, RDS automatically flips the CNAME record of the DB endpoint to point to the standby instance. This failover takes **60-120 seconds**.
*   **Read Replicas (Read Scaling)**:
    *   *Mechanism*: Uses the native asynchronous replication of the database engine (e.g., PostgreSQL streaming replication, MySQL binlog) to replicate data to one or more active read replicas.
    *   *Replication Lag*: Because replication is asynchronous, read replicas can drift behind the primary database (replication lag) under heavy write loads. A client reading from a replica might get stale data (violating read-your-own-writes consistency).

### B. Snapshots and Backup Mechanics
*   **Automated Backups**: RDS takes daily full storage volume snapshots during your backup window and captures transaction logs (write-ahead logs / binary logs) to S3 every 5 minutes. This enables **Point-in-Time Recovery (PITR)** down to the second.
*   *Performance Impact*: During a snapshot on a single-AZ instance, I/O can be suspended briefly (from seconds to a few minutes). On a Multi-AZ instance, the snapshot is taken from the standby instance, avoiding performance impact on the primary.

---

## 2. Amazon Aurora: Next-Generation Storage & Quorum

Amazon Aurora is a cloud-native relational database engine (compatible with MySQL and PostgreSQL) designed to decouple compute from storage.

### A. Distributed Shared Storage Engine
In traditional RDS, compute and storage are linked. Aurora decouples them.
*   **The Volume**: Your data is stored in a single, virtual, distributed storage volume that automatically scales up to 128 TB.
*   **AZ Duplication**: Aurora replicates your data across **three Availability Zones**, placing **two copies in each AZ** (a total of 6 copies of your data).

### B. The Storage Quorum Model
To ensure absolute write consistency and survival of failures without a centralized coordinator, Aurora uses a quorum voting system:
*   **Write Quorum (4/6)**: To commit a write, **4 out of the 6 storage nodes** must acknowledge writing the update log.
    *   *Resilience*: If an entire AZ goes offline (losing 2 copies), Aurora can still write because 4 nodes remain online.
*   **Read Quorum (3/6)**: To execute a read, Aurora must theoretically read from at least **3 out of the 6 storage nodes** to reconstruct the state using log sequence numbers (LSN).
    *   *Optimization*: Under normal operations, Aurora does not perform network reads to 3 nodes. It tracks which node is up-to-date in memory and reads from that node (or local buffer cache), bypassing read quorum calls.
    *   *Resilience*: If an entire AZ goes offline (2 copies) and an additional node fails (1 copy, 3 total lost), Aurora can still read because 3 nodes remain online.

```
Aurora Quorum Architecture:
[Compute Instance (Read/Write)]
        |
        +-----> Write: Send to 6 nodes (Acknowledge 4/6 to commit)
        |
        v
+-------------------------------------------------------+
|  Virtual Storage Volume (Decoupled Shared Storage)     |
|                                                       |
|   [AZ-1 Nodes: 1, 2]   [AZ-2 Nodes: 3, 4]  [AZ-3: 5,6]|
+-------------------------------------------------------+
```

### C. Aurora Serverless v2
Traditional RDS databases require you to select an instance size (e.g., `db.r6g.large`).
*   Aurora Serverless v2 scales database compute capacity (CPU and Memory) dynamically based on application demand, measured in **Aurora Capacity Units (ACUs)** (1 ACU = ~2GB RAM + associated CPU).
*   *Scale Speed*: Scales in fractions of a second (sub-second scaling) without dropping connections or transactions, making it ideal for highly variable or unpredictable workloads.

### D. Aurora Global Database
*   For cross-region disaster recovery and low-latency reads.
*   *Mechanism*: Uses dedicated replication agents in the storage tier, bypassing the SQL database engine. The storage volume in the primary region replicates logs directly to storage volumes in up to five secondary regions.
*   *Latency*: Replication lag is typically **less than 1 second**.

### E. Fast Failover Mechanics
Because Aurora compute instances mount a shared virtual storage volume, failover does not require data synchronization.
*   If the primary writer instance crashes, an Aurora reader instance is promoted to writer instantly.
*   Failover takes **under 30 seconds** (often under 10 seconds).

---

## 3. Standard Interview Questions

#### Q1: If your application suffers from "stale reads" when querying an RDS Read Replica, how do you handle it in the application layer?
1.  **Read-Your-Own-Writes Routing**: Route critical write-and-read sequences (like user login or profile updates) exclusively to the Primary database endpoint. Route non-critical list views or search queries to the Read Replica.
2.  **Lag Detection**: Monitor the `ReplicaLag` metric in CloudWatch. If replication lag exceeds a threshold (e.g., 5 seconds), stop routing queries to that replica until it catches up.

#### Q2: Explain how Aurora's database recovery is faster than traditional RDS databases after a crash.
*   *Traditional RDS*: After a database crash, the database must play back the entire Write-Ahead Log (WAL) sequentially from the last checkpoint to restore consistency. This can take minutes or hours.
*   *Amazon Aurora*: Aurora's distributed storage nodes perform crash recovery continuously and asynchronously in the background. Because the storage volume is self-healing and reconstructs state using log sequence numbers across the quorum, the compute instance can start up almost instantly without running a long sequential log replay.

#### Q3: When should you choose RDS Multi-AZ vs. Aurora Multi-AZ?
*   *Choose RDS Multi-AZ* if you require database engines not supported by Aurora (e.g., Oracle, SQL Server, MariaDB) or need standard open-source compatibility without vendor lock-in.
*   *Choose Aurora Multi-AZ* for superior write throughput, sub-30-second failovers, up to 15 read replicas (RDS is limited to 5), and decoupling of compute and storage to save costs on large-scale databases.
