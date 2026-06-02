# Database Basics & Architecture Interview Questions

## 1. What is a DBMS and RDBMS?
**DBMS (Database Management System)** is a software that interacts with end-users, applications, and the database itself to capture and analyze data. 
**RDBMS (Relational Database Management System)** is a type of DBMS that stores data in a structured format, using rows and columns (tables). It enforces relationships between tables using primary and foreign keys.

## 2. Explain the ACID properties in detail.
ACID properties guarantee that database transactions are processed reliably:
*   **Atomicity**: "All or nothing". A transaction is treated as a single unit. If any part of it fails, the entire transaction fails, and the database state is left unchanged.
*   **Consistency**: A transaction must bring the database from one valid state to another. It must satisfy all defined rules, constraints, and triggers.
*   **Isolation**: Concurrent execution of transactions leaves the database in the same state that would have been obtained if the transactions were executed sequentially.
*   **Durability**: Once a transaction has been committed, it will remain so, even in the event of power loss, crashes, or errors (usually implemented via transaction logs).

## 3. What is Normalization? Explain the normal forms (1NF, 2NF, 3NF, BCNF).
Normalization is the process of organizing data to reduce redundancy and improve data integrity.
*   **1NF (First Normal Form)**: Each column must have atomic (indivisible) values. No repeating groups or arrays.
*   **2NF (Second Normal Form)**: Must be in 1NF. All non-key attributes must be fully functionally dependent on the entire primary key (no partial dependency).
*   **3NF (Third Normal Form)**: Must be in 2NF. There should be no transitive dependency (non-key attributes should not depend on other non-key attributes).
*   **BCNF (Boyce-Codd Normal Form)**: A stricter version of 3NF. For every non-trivial functional dependency $X \rightarrow Y$, $X$ must be a superkey.

## 4. What is Denormalization and when would you use it?
Denormalization is the deliberate introduction of redundancy into a database to improve read performance. It involves combining tables to reduce the overhead of complex joins.
**When to use:** In read-heavy systems (like Data Warehouses or reporting systems) where read performance is critical, and the cost of maintaining redundant data is acceptable.

## 5. Explain the CAP Theorem.
The CAP theorem states that a distributed data store can only provide two of the following three guarantees simultaneously:
*   **Consistency**: Every read receives the most recent write or an error.
*   **Availability**: Every request receives a (non-error) response, without the guarantee that it contains the most recent write.
*   **Partition Tolerance**: The system continues to operate despite an arbitrary number of messages being dropped (or delayed) by the network between nodes.
*   *Note: In modern distributed systems, Partition Tolerance is a given, so the trade-off is usually between Consistency and Availability (CP vs AP).*

## 6. SQL vs NoSQL databases. When to choose which?
*   **SQL (Relational)**: Structured data, fixed schema, ACID compliance, good for complex queries and joins. Examples: MySQL, PostgreSQL, Oracle.
    *   *Choose when:* Data structure is clear and stable, relationships are important, strict ACID compliance is required (e.g., financial systems).
*   **NoSQL (Non-Relational)**: Unstructured/semi-structured data, dynamic schema, scalable horizontally, eventually consistent (usually). Types include Document (MongoDB), Key-Value (Redis), Column-Family (Cassandra), Graph (Neo4j).
    *   *Choose when:* Handling massive volumes of unstructured data, rapid agile development needing flexible schemas, needing high write throughput and horizontal scaling.
