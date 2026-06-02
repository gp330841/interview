# Microservices Patterns

This section covers structural patterns used to solve database orchestration, read/write scaling, and routing in a microservices ecosystem.

---

## 1. Saga Pattern
In microservices, each service has its own database. Distributing transactions across databases using Two-Phase Commit (2PC) blocks databases and limits throughput. **Saga solves this by executing a sequence of local transactions.**

```mermaid
flowchart LR
    subgraph Happy Path
        T1[Order Service] -->|Create Order| T2[Payment Service]
        T2 -->|Charge Card| T3[Inventory Service]
        T3 -->|Reserve Items| Success[Complete Order]
    end
    subgraph Compensation Path
        C1[Order Service] -->|Create Order| C2[Payment Service]
        C2 -->|Charge Card| C3[Inventory Service]
        C3 -->|OutOfStock Fail| Compensate2[Refund Card]
        Compensate2 --> Compensate1[Cancel Order]
    end
```

### Saga Orchestration vs Choreography
* **Choreography:** Each service publishes events. Other services listen and react. (Simple to start, hard to track flows).
* **Orchestration:** A central controller manages the steps, executes actions, and coordinates rollback transactions (Compensating Transactions) if any step fails. (Clear dependencies, slightly complex controller).

---

## 2. CQRS (Command Query Responsibility Segregation)
CQRS splits the write path (Commands) and the read path (Queries) into separate database models.

```mermaid
flowchart TD
    Client -->|Command: Write/Update| WriteDB[(Write Database)]
    WriteDB -->|Sync Events| ReadDB[(Read Database)]
    Client -->|Query: Read| ReadDB
```

* **Write DB:** Optimized for normalization and transaction updates.
* **Read DB:** Optimized for queries (e.g. read replicas, Elasticsearch, or fully de-normalized documents).
* **Sync:** Synchronized asynchronously using event streams (Eventual Consistency).

---

## Interview Q&A Corner

> [!IMPORTANT]
> **Q: What is the main challenge of CQRS?**
> A: **Eventual Consistency.** Because database updates are synchronized asynchronously, a user who updates their profile (Write DB) might refresh the page and read from the Read DB before synchronization completes, seeing stale data.
> *Mitigations:* Force critical read paths (e.g., checkout pages) to read directly from the Write DB, or implement caching strategies.
