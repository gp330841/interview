# High Level Design (HLD) Interview Guide

Welcome to the High Level Design (HLD) repository. This section covers large-scale system scalability, core distributed systems concepts, infrastructure building blocks, and standard real-world HLD systems commonly asked in architecture and engineering interviews.

Each topic contains clear architectural flows, Mermaid diagrams, database schema designs, key bottlenecks, and trade-off analyses.

---

## HLD Concept Dashboard

| ID | Concept | Category | Difficulty | Key Focus / Interview Questions |
|----|---------|----------|------------|---------------------------------|
| 01 | [Scalability, Latency & Throughput](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/01-scalability-latency-throughput.md) | Core Scaling | Easy | Scale-up vs Scale-out, Measuring latency/throughput percentiles (p99) |
| 02 | [Load Balancers](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/02-load-balancers.md) | Infrastructure | Medium | L4 vs L7 routing, health checks, routing algorithms, DNS round-robin |
| 03 | [Caching Strategies](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/03-caching-strategies.md) | Core Scaling | Medium | Cache-aside, write-through, write-behind, cache eviction policies, stampede |
| 04 | [Database Sharding & Partitioning](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/04-database-sharding.md) | Distributed DB | Hard | Sharding keys, horizontal/vertical partitioning, re-sharding problems |
| 05 | [Database Replication](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/05-database-replication.md) | Distributed DB | Hard | Active-Passive, Active-Active, Consensus (Raft/Paxos), Quorum reads/writes |
| 06 | [CAP & PACELC Theorems](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/06-cap-pacelc-theorems.md) | Distributed Systems | Medium | Eventual consistency, network partitions, PACELC tradeoffs (Latency/Consistency) |
| 07 | [Consistent Hashing](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/07-consistent-hashing.md) | Distributed Systems | Hard | Hash ring, virtual nodes, data migration mitigation |
| 08 | [Message Queues & Event Streaming](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/08-message-queues-streaming.md) | Infrastructure | Medium | Event streams vs Queues, Kafka vs RabbitMQ, partitioned logs, offsets |
| 09 | [API Protocols](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/09-api-protocols.md) | API Design | Easy | REST vs GraphQL vs gRPC, WebSockets vs Webhooks vs Long Polling |
| 10 | [Microservices Patterns](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/10-microservices-patterns.md) | Microservices | Hard | CQRS, Saga pattern (Orchestration vs Choreography), API Gateway |
| 11 | [Rate Limiting Algorithms](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/11-rate-limiting-algorithms.md) | Security/Scaling | Medium | Token Bucket, Leaky Bucket, Sliding Window, distributed rate limiting |
| 12 | [CDNs & Edge Servers](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/12-cdn-edge-servers.md) | Infrastructure | Easy | Push vs Pull CDNs, static asset caching, edge compute, cache invalidation |
| 13 | [Disaster Recovery & HA](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/13-disaster-recovery.md) | Infrastructure | Medium | Active-Active vs Active-Passive, RTO/RPO metrics, DNS failover, split-brain |
| 14 | [Distributed Transactions](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/14-distributed-transactions.md) | Distributed Systems | Hard | Two-Phase Commit (2PC), Three-Phase Commit (3PC), Saga Compensation |
| 15 | [Distributed Locking](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/15-distributed-locking.md) | Distributed Systems | Hard | Redis Redlock, ZooKeeper lease locks, database optimistic locking |
| 16 | [SQL vs NoSQL](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/16-sql-vs-nosql.md) | Distributed DB | Easy | Key-value, Document, Columnar, Graph databases, Acid vs Base |
| 17 | [Distributed Search Systems](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/17-distributed-search.md) | Distributed Systems | Medium | Inverted indexes, Elasticsearch architecture, document routing & sharding |
| 18 | [DNS & Anycast Routing](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/18-dns-anycast-routing.md) | Infrastructure | Medium | Root/TLD servers, recursive resolution, Anycast for CDN/DNS |
| 19 | [Distributed File Systems](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/19-distributed-file-systems.md) | Distributed Systems | Medium | GFS/HDFS architecture, NameNode (Metadata) vs DataNodes, chunking |
| 20 | [Security in HLD](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/20-hld-security-practices.md) | Security/Scaling | Medium | OAuth2, JWT, TLS termination, WAF protection, DDoS mitigation |
| 21 | [Design Twitter / X](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/21-design-twitter.md) | Real-world System | Hard | Hybrid newsfeed generation, celebrity fanout on read, push model |
| 22 | [Design Netflix / YouTube](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/22-design-netflix.md) | Real-world System | Medium | Video transcoding pipeline, HLS/DASH streaming, CDN placement |
| 23 | [Design Uber / Lyft](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/23-design-uber.md) | Real-world System | Hard | Quadtree & Uber H3 indexing, driver-rider matching, real-time location |
| 24 | [Design WhatsApp](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/24-design-whatsapp.md) | Real-world System | Hard | WebSocket gateways, offline storage queue, end-to-end encryption, group chats |
| 25 | [Design Google Drive](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/25-design-google-drive.md) | Real-world System | Medium | Chunking & delta sync, deduplication, sync conflict resolution |
| 26 | [Design Web Crawler](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/26-design-web-crawler.md) | Real-world System | Medium | Crawl frontier, robot.txt compliance, DNS cache, deduplication checks |
| 27 | [Design TinyURL](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/27-design-url-shortener.md) | Real-world System | Easy | Base62 encoding, Key Generation Service (KGS), cache/db scaling |
| 28 | [Design Amazon (Flash Sale)](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/28-design-amazon-ecommerce.md) | Real-world System | Hard | Flash sales concurrency, inventory locking, order checkout queues |
| 29 | [Design Search Autocomplete](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/29-design-search-autocomplete.md) | Real-world System | Medium | Trie storage, MapReduce aggregation, query frequency caching |
| 30 | [Design Metrics Monitoring](file:///Users/yogeshwarpatel/Workspace/interview/high-level-design/concepts/30-design-metrics-monitoring.md) | Real-world System | Medium | Prometheus pull vs push, Time-Series Databases, alerting engine |

---

## How to Study HLD

1. **Back-of-the-Envelope Estimation**: Practice estimating DAU, QPS, Storage, Bandwidth, and Memory needs.
2. **Identify Trade-offs**: Never present a single "perfect" solution. Discuss Consistency vs Latency, Relational vs NoSQL, and Push vs Pull.
3. **Avoid Buzzword Soup**: Do not just say "I will use Kafka and Kubernetes." Explain *why* you need them and how they operate.
