# Transactions & Concurrency Control Interview Questions

## 1. What are Transaction Isolation Levels? Explain them.
Isolation levels define the degree to which a transaction must be isolated from the data modifications made by other concurrent transactions. The SQL standard defines four levels (from lowest to highest isolation):
*   **Read Uncommitted**: A transaction can read data that has not yet been committed by other transactions. (Prone to Dirty Reads).
*   **Read Committed**: A transaction can only read data that has been committed. (Prone to Non-Repeatable Reads). This is the default in many databases like PostgreSQL and SQL Server.
*   **Repeatable Read**: Guarantees that if a transaction reads the same row twice, it will get the same value, even if other transactions commit changes in the meantime. (Prone to Phantom Reads). Default in MySQL (InnoDB).
*   **Serializable**: The highest level. Transactions are executed in a way that the outcome is equivalent to executing them sequentially, one after the other. Prevents all concurrency phenomena but has the lowest concurrency/performance.

## 2. Explain Dirty Reads, Non-repeatable Reads, and Phantom Reads.
*   **Dirty Read**: Transaction A reads a value modified by Transaction B before Transaction B commits. If B rolls back, A has read data that "never existed."
*   **Non-Repeatable Read**: Transaction A reads a row. Transaction B modifies or deletes that row and commits. Transaction A reads the same row again and gets a different result.
*   **Phantom Read**: Transaction A executes a query returning a set of rows. Transaction B inserts a new row that matches Transaction A's query criteria and commits. If Transaction A executes the same query again, it gets a different set of rows (the "phantom" row appears).

## 3. How do locks work in a database? (Shared vs. Exclusive Locks)
Locks are mechanisms used to ensure data consistency during concurrent access.
*   **Shared Lock (S-Lock)**: Acquired for read operations (`SELECT`). Multiple transactions can hold shared locks on the same resource simultaneously. Prevents other transactions from acquiring exclusive locks.
*   **Exclusive Lock (X-Lock)**: Acquired for write operations (`INSERT`, `UPDATE`, `DELETE`). Only one transaction can hold an exclusive lock on a resource at a time. It blocks both shared and exclusive locks from other transactions.

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
