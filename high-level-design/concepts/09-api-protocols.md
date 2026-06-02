# API Protocols

API protocols define the rules and formats for exchanging data between client-server and microservice layers.

---

## 1. Request-Response Protocols

| Protocol | Data Format | Communication | Pros | Cons |
|----------|-------------|---------------|------|------|
| **REST** | JSON/XML | HTTP/1.1 | Universal, stateless, easy caching | Over/under fetching data |
| **GraphQL** | JSON | HTTP/1.1 | Clients request exact fields | Complex caching, query parsing overhead |
| **gRPC** | Protocol Buffers | HTTP/2 | High performance, streaming, binary | Poor browser support, binary payload hard to debug |

---

## 2. Real-time Communication Protocols

```mermaid
flowchart TD
    subgraph WebSockets
        C1[Client] -->|Handshake HTTP| S1[Server]
        C1 <=>|Bi-directional Persistent TCP Connection| S1
    end
    subgraph Webhooks
        S2[Source Server] -->|HTTP POST Event payload| C2[Destination Server]
    end
```

### Long Polling
* **Behavior:** Client requests data; server holds connection open until new data is available or a timeout occurs, then responds. Client immediately requests again.
* **Cons:** High connection overhead.

### WebSockets
* **Behavior:** Upgrades an HTTP connection to a persistent, bi-directional TCP socket.
* **Ideal for:** Chat applications, stock trading feeds, gaming.

### Webhooks
* **Behavior:** Event-driven callbacks. The source server makes an HTTP POST request to the client's pre-configured URL when an event occurs (e.g. Stripe payment complete).
* **Ideal for:** Third-party system integrations.

---

## Interview Q&A Corner

> [!TIP]
> **Q: When would you use gRPC over REST for microservices?**
> A: Use **gRPC** for internal service-to-service communication because of its speed (binary Protocol Buffers) and multiplexing capabilities over HTTP/2. Use **REST** for public APIs to ensure compatibility with browsers and external client developers.
