# Scaling, Replication, and Performance Tuning Interview Questions

## 1. How do you optimize a slow-running SQL query?
Steps to optimize:
1.  **Analyze the Execution Plan**: Use `EXPLAIN` or `EXPLAIN ANALYZE` to see how the database engine executes the query (table scans, index usage, join types).
2.  **Ensure Proper Indexing**: Add missing indexes on columns used in `WHERE`, `JOIN`, and `ORDER BY` clauses. Use covering indexes where applicable.
3.  **Avoid `SELECT *`**: Retrieve only the columns you actually need.
4.  **Rewrite Queries**: Avoid correlated subqueries; replace them with `JOIN`s or CTEs. Avoid using functions on indexed columns in the `WHERE` clause (e.g., `WHERE YEAR(date_column) = 2023`), as it prevents index usage (Sargability).
5.  **Update Statistics**: Ensure database statistics are up-to-date so the query optimizer makes good choices.

## 2. What is the N+1 Query Problem and how do you solve it?
A common performance issue in ORMs (Object-Relational Mappers like Hibernate or Entity Framework).
It occurs when a framework executes 1 query to retrieve a list of $N$ parent entities, and then executes $N$ additional separate queries to retrieve the child entities for each parent.
*   **Solution**: Use Eager Loading (fetching the related entities in the initial query using a `JOIN`) or Batch Fetching (using an `IN` clause to fetch all related children in one go).

## 3. What is Connection Pooling?
Opening and closing database connections is an expensive, time-consuming operation. 
Connection pooling maintains a cache of open database connections that can be reused by future requests. When an application needs a connection, it borrows one from the pool, uses it, and then returns it to the pool instead of closing it.

## 4. Explain Database Replication. (Master-Slave vs. Master-Master)
Replication involves copying data from one database server to another to ensure high availability and improve read performance.
*   **Master-Slave (Active-Passive)**: Writes only go to the Master node. The Master asynchronously or synchronously replicates changes to one or more Slave nodes. Slaves handle read queries. If Master fails, a Slave is promoted to Master.
*   **Master-Master (Active-Active)**: Application can read and write to any node. Changes are replicated bi-directionally. It is much harder to implement due to conflict resolution (e.g., what happens if two users update the same record on different nodes simultaneously?).

## 5. What is Database Partitioning vs. Sharding?
Both are techniques to break large databases into smaller, more manageable pieces.
*   **Partitioning (Vertical or Horizontal)**: Divides a large table into smaller physical pieces within the **same** database instance. Usually transparent to the application. (e.g., Partitioning a sales table by year).
*   **Sharding**: A type of horizontal partitioning where data is distributed across **multiple** independent database servers (nodes). Each shard holds a subset of the data. The application layer (or a proxy) must contain logic to determine which shard to query based on a Shard Key. Sharding allows infinite horizontal scaling but makes joins across shards extremely difficult.

## 6. What is Consistent Hashing? (Used in Distributed Databases)
In a sharded environment, if you use simple modulo hashing (`hash(id) % N_servers`) to determine data location, adding or removing a server changes the modulo for almost all keys, requiring massive data movement.
**Consistent Hashing** solves this by mapping both data keys and server nodes to a conceptual circle. A key is assigned to the first server found by moving clockwise on the circle. When a node is added or removed, only a small fraction of data (from the immediate neighbor) needs to be remapped, minimizing data migration overhead.
