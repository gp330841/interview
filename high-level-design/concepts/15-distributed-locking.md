# Distributed Locking

Distributed locking coordinates access to shared resources across multiple server nodes.

---

## 1. Implementations

### Redis Redlock Algorithm
Designed by Redis creator. To acquire a lock:
1. Fetch the current time in milliseconds.
2. Try to acquire the lock in all $N$ Redis instances sequentially using a small timeout.
3. If the client acquires the lock in a **majority** ($\ge N/2 + 1$) of nodes, and the elapsed time is less than lock validity time, the lock is acquired.
4. If it fails, unlock all nodes immediately.

### ZooKeeper Lease Locks
ZooKeeper uses **Ephemeral Sequential Nodes**.
1. Client creates a node under `/lock/node_`.
2. Client queries children of `/lock`.
3. If the created node has the lowest sequence number, the client holds the lock.
4. If not, the client sets a **watch** on the node just before its own sequence number and waits.
5. If the node holder crashes, the ephemeral node is deleted automatically by ZK, triggering the watcher.

---

## 2. Lock Mechanism Comparison

| Feature | Redis (Redlock) | ZooKeeper | Relational DB |
|---------|-----------------|-----------|---------------|
| **Speed** | Insanely fast (in-memory) | Moderate | Slow |
| **Failover** | TTL based recovery | Ephemeral connection heartbeats | Manual timeouts |
| **Reliability** | Medium (Clock drift issues) | High (Strict consistency CP) | High |

---

## Interview Q&A Corner

> [!WARNING]
> **Q: What is the main criticism of Redlock by distributed systems experts (like Martin Kleppmann)?**
> A: Redlock relies on **physical system clocks** to calculate lock expiration. In distributed systems, clocks can drift due to NTP synchronization or experience **Stop-the-World Garbage Collection (GC)** pauses. If a GC pause occurs right after acquiring a lock, the lock expires in Redis, but the client resumes thinking it still holds the lock, leading to race conditions.
> *Mitigation:* Use **Fencing Tokens** (monotonically increasing numbers returned with each lock lease) to validate rights before executing database updates.
