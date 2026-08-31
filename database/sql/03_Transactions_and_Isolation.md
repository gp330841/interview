# Transactions & Concurrency Control Interview Questions

## 1. What are Transaction Isolation Levels? Explain them.
Isolation levels define the degree to which a transaction must be isolated from the data modifications made by other concurrent transactions. The SQL standard defines four levels (from lowest to highest isolation):
*   **Read Uncommitted**: A transaction can read data that has not yet been committed by other transactions. (Prone to Dirty Reads).
*   **Read Committed**: A transaction can only read data that has been committed. (Prone to Non-Repeatable Reads). This is the default in many databases like PostgreSQL and SQL Server.
*   **Repeatable Read**: Guarantees that if a transaction reads the same row twice, it will get the same value, even if other transactions commit changes in the meantime. (Prone to Phantom Reads). Default in MySQL (InnoDB).
*   **Serializable**: The highest level. Transactions are executed in a way that the outcome is equivalent to executing them sequentially, one after the other. Prevents all concurrency phenomena but has the lowest concurrency/performance.

```sql
-- Setting Transaction Isolation Level
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
START TRANSACTION;
-- ... SQL statements ...
COMMIT;
```

## 2. Explain Dirty Reads, Non-repeatable Reads, and Phantom Reads.
*   **Dirty Read**: Transaction A reads a value modified by Transaction B before Transaction B commits. If B rolls back, A has read data that "never existed."
*   **Non-Repeatable Read**: Transaction A reads a row. Transaction B modifies or deletes that row and commits. Transaction A reads the same row again and gets a different result.
*   **Phantom Read**: Transaction A executes a query returning a set of rows. Transaction B inserts a new row that matches Transaction A's query criteria and commits. If Transaction A executes the same query again, it gets a different set of rows (the "phantom" row appears).

## 3. How do locks work in a database? (Shared vs. Exclusive Locks)
Locks are mechanisms used to ensure data consistency during concurrent access.
*   **Shared Lock (S-Lock)**: Acquired for read operations (`SELECT`). Multiple transactions can hold shared locks on the same resource simultaneously. Prevents other transactions from acquiring exclusive locks.
*   **Exclusive Lock (X-Lock)**: Acquired for write operations (`INSERT`, `UPDATE`, `DELETE`). Only one transaction can hold an exclusive lock on a resource at a time. It blocks both shared and exclusive locks from other transactions.

```sql
-- Explicitly requesting a Shared Lock (S-Lock)
SELECT * FROM Products WHERE ProductID = 10 FOR SHARE;

-- Explicitly requesting an Exclusive Lock (X-Lock)
SELECT * FROM Products WHERE ProductID = 10 FOR UPDATE;
```

## 4. What is a Deadlock? How do databases handle them?
A deadlock occurs when two or more transactions are waiting for each other to release locks, creating a cycle of dependencies where none can proceed.
**Example**: 
1. Tx A locks Table 1, wants Table 2.
2. Tx B locks Table 2, wants Table 1.
**Handling**: Databases have deadlock detectors that periodically check for cycles in the wait graph. When a deadlock is detected, the database engine aborts (rolls back) one of the transactions (the "victim") to break the cycle and allow the other to complete. The application must catch the deadlock error and retry the transaction.

## 5. What is MVCC (Multi-Version Concurrency Control)?
MVCC is a popular technique used by databases (like PostgreSQL, MySQL/InnoDB, Oracle) to achieve high concurrency. 
Instead of using locks to block readers while writers are writing, MVCC keeps multiple versions of a row.
*   When a transaction reads data, it reads a snapshot of the database at the time the transaction started.
*   Writers don't block readers, and readers don't block writers.
*   This largely eliminates the need for read locks, drastically improving performance in read-heavy workloads while maintaining isolation.

## 6. What is Pessimistic vs Optimistic Locking?
*   **Pessimistic Locking**: Assumes conflicts are likely. Acquires locks on data as soon as it is read and holds them until the transaction completes. Good for high-contention environments but reduces concurrency.
*   **Optimistic Locking**: Assumes conflicts are rare. Does not lock data when reading. Instead, when updating, it checks if the data has been modified by another transaction since it was read (often using a version number or timestamp column). If it has changed, the transaction aborts and must be retried. Good for read-heavy, low-conflict environments.

```sql
-- Pessimistic Locking Example
SELECT Balance FROM Accounts WHERE ID = 1 FOR UPDATE;
UPDATE Accounts SET Balance = Balance - 100 WHERE ID = 1;

-- Optimistic Locking Example (Using a Version Column)
-- Step 1: Read data and version
-- SELECT Balance, Version FROM Accounts WHERE ID = 1; (Assume Version = 5)

-- Step 2: Attempt update only if version hasn't changed
UPDATE Accounts SET Balance = Balance - 100, Version = Version + 1 
WHERE ID = 1 AND Version = 5;
-- If affected rows == 0, another transaction changed the data. Retry needed.
```

## 7. MVCC internals and visibility
- MVCC stores multiple historical versions (tuples) of rows. Each version has transactional metadata (timestamp/txid, xmin/xmax in Postgres).
- A reader picks the newest version visible to its snapshot; writers create new versions without blocking readers.
- Garbage collection (VACUUM in Postgres, purge in InnoDB) removes expired versions — monitor GC lag to avoid bloat and long-running snapshots.
- Write amplification: heavy update workloads under MVCC can increase storage and IO due to version churn.

## 8. Lock granularity, escalation & intention locks
- Granularity: row-level (fine), page/extent, table-level (coarse). Row locks maximize concurrency but have higher metadata overhead.
- Lock escalation: some DBs (SQL Server) escalate many row locks to a table lock to reduce bookkeeping; tune thresholds and queries to avoid unwanted escalation.
- Intention locks (e.g., Intention Shared/Exclusive) allow locking protocols that mix table and row locks safely.

## 9. Optimistic retry strategies & backoff
- Retry loop pattern (application-side): read → compute → attempt conditional update → if 0 rows affected, sleep/backoff and retry up to N times.
- Backoff policies: constant small delay, exponential backoff with jitter, or randomized micro-sleeps for hot keys.
- Idempotency: design operations to be idempotent or detect duplicate retries (use idempotency keys) to avoid double effects.

## 10. Snapshot isolation vs Repeatable Read (DB-specific nuances)
- SQL standard REPEATABLE READ differs across engines:
  * PostgreSQL's REPEATABLE READ implements true Snapshot Isolation (no phantoms within a transaction snapshot). SERIALIZABLE provides additional serializability checks.
  * MySQL/InnoDB REPEATABLE READ historically implements snapshot isolation with gap locks to avoid phantoms (depending on setting).
- Understand your DB's concrete semantics — tests or official docs help avoid surprises.

## 11. Database-specific behavior (quick notes)
- PostgreSQL:
  * MVCC via xmin/xmax, snapshots, no read locks for SELECT by default.
  * SERIALIZABLE uses predicate locks & may raise serialization errors that require retries.
- MySQL / InnoDB:
  * MVCC with undo logs; gap locks used under REPEATABLE READ or when using FOR UPDATE with range scans.
  * Locking behavior varies by isolation and query patterns (index usage matters).
- SQL Server:
  * Row/page/table locks with optional Read Committed Snapshot Isolation (RCSI) which uses row versions.
  * Lock escalation policy configurable; deadlock detection built-in.
- Oracle:
  * Uses read consistency (undo-based), writers don't block readers; `SELECT ... FOR UPDATE` acquires row locks.

## 12. Deadlock avoidance & mitigation techniques
- Acquire locks in a consistent global order (sort keys) to prevent cycles.
- Keep transactions short and do minimal work while holding locks (avoid user prompts inside transactions).
- Use lower isolation where safe (Read Committed) to reduce locking pressure.
- Implement retry-on-deadlock with exponential backoff and limits; capture and log deadlock victims to analyze patterns.

## 13. Monitoring, metrics & troubleshooting
- Important metrics: lock wait time, number of lock timeouts/deadlocks, long-running transactions, MVCC version count, undo/undo tablespace growth, transaction ID wraparound warnings (Postgres).
- Tools: `pg_stat_activity`, `pg_locks` (Postgres); `SHOW ENGINE INNODB STATUS`, `information_schema.innodb_trx` (MySQL); SQL Server DMVs for locks.
- Regularly profile slow queries and analyze wait stats to identify contention hotspots.

## 14. Practical patterns & anti-patterns
- Use optimistic locking for low write contention, e.g., user profile edits, counters with low concurrency.
- Use pessimistic locks for critical sections with high write contention, or when external resources must be coordinated (e.g., payment gateway state transitions).
- Avoid SELECT N+1 patterns and long-running cursors inside transactions.
- Prefer LIMIT/ORDER + indexed predicates for range updates to reduce scanned rows and lock footprint.

## 15. Short interview-ready answers
- When to use optimistic vs pessimistic: "Use optimistic when conflicts are rare and retries are cheap; use pessimistic when conflicts are frequent or the cost of retry is high (financial transactions)."
- Why MVCC helps: "MVCC provides non-blocking reads by snapshotting row versions, improving read throughput at the cost of storage and GC overhead."

---

If you want, can also add sample code for application-level retry loops (Java + Spring/JPA) and a short checklist for DB configuration (innodb_lock_wait_timeout, max_prepared_transactions, vacuum settings).
