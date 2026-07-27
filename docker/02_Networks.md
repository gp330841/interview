# 02. Docker Container Networking

This guide covers Docker network drivers, container DNS resolution, port publishing vs exposing, iptables routing, and real-world multi-container communication.

---

## 1. Core Docker Network Drivers

Docker provides 5 built-in network drivers:

| Driver | Scope | Description | Use Case |
| :--- | :--- | :--- | :--- |
| **Bridge** | Single Host | Default driver. Creates a virtual bridge software switch on the host. Containers get private IPs. | Standard multi-container applications on a single host. |
| **Host** | Single Host | Removes network isolation between container and host. Container shares host IP & ports. | Maximum network performance; high-throughput proxy services. |
| **None** | Single Host | Disables all networking for the container (only loopback interface exists). | Highly secure isolated offline processing jobs. |
| **Overlay** | Multi Host | Connects multiple Docker daemons across nodes (Swarm/Kubernetes overlay). | Distributed microservices across multiple machines. |
| **Macvlan** | Single Host | Assigns a physical MAC address to a container, making it appear as a physical node on host LAN. | Legacy apps requiring direct connection to physical network switches. |

---

## 2. Default Bridge vs User-Defined Bridge Networks

```
                 USER-DEFINED BRIDGE NETWORK (app-network)
+------------------------------------------------------------------------+
|                                                                        |
|   +-----------------------+                +-----------------------+   |
|   |  springboot-app       |                |  redis                |   |
|   |  IP: 172.28.0.2       |                |  IP: 172.28.0.3       |   |
|   +-----------+-----------+                +-----------+-----------+   |
|               |                                        |               |
|               +----------[ Embedded DNS Server ]-------+               |
|                           (127.0.0.11:53)                              |
|                                                                        |
+-----------------------------------+------------------------------------+
                                    |
                            [ Host veth pairs ]
                                    |
                            [ Host docker0 / br0 ]
                                    | NAT (iptables)
                            [ Host eth0 Interface ]
```

### Critical Difference:
- **Default `bridge` network (`docker0`)**:
  - Containers can only communicate by hardcoded **IP addresses** or legacy `--link` flags.
  - **No automatic DNS resolution**.
- **User-Defined `bridge` network (e.g., `app-network`)**:
  - Provides **automatic internal DNS resolution**. Containers discover each other by container name or service alias (`http://redis:6379` or `http://kafka:29092`).
  - Isolated from external unapproved containers on the same host.

> 🔗 **Code Reference**: See how our [springboot/docker-compose.yml](file:///Users/yogeshwarpatel/Workspace/interview/springboot/docker-compose.yml#L100-L103) explicitly defines a user-defined bridge network named `app-network`!

---

## 3. How Port Publishing (`-p`) Works Under the Hood

When you execute `docker run -p 8080:8080` or specify `ports: - "8080:8080"` in Docker Compose:

1. **`EXPOSE 8080` in Dockerfile**: Merely documentation. It tells developers which port the container application listens on internally. It opens **NO** ports on the host.
2. **`ports: - "8080:8080"`**: Docker manipulates Linux kernel **`iptables`** rules in the `PREROUTING` and `DOCKER` chains.
3. Traffic arriving on Host IP port `8080` is NATed (Network Address Translation) directly to Container IP `172.28.0.2:8080`.

```bash
# View iptables rules created by Docker
sudo iptables -t nat -L DOCKER -n -v
```

---

## 4. Deep Dive: Real-World Kafka Dual-Listener Architecture

In containerized environments, Kafka must handle two distinct network routes:
1. **Container-to-Container**: Spring Boot app communicating inside Docker bridge network.
2. **Host-to-Container**: Local IDE or Kafdrop UI running directly on the host machine.

```yaml
# Excerpt from springboot/docker-compose.yml
kafka:
  image: confluentinc/cp-kafka:7.6.0
  ports:
    - "9092:9092"
  environment:
    KAFKA_LISTENERS: 'PLAINTEXT://0.0.0.0:29092,CONTROLLER://0.0.0.0:29093,PLAINTEXT_HOST://0.0.0.0:9092'
    KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092'
    KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: 'CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT'
```

### Why Dual Listeners are Necessary:
- When a Kafka client connects, the broker responds with its **Advertised Listener** address.
- Inside `app-network`, Spring Boot connects to `kafka:29092`. Kafka advertises `kafka:29092`. Spring Boot resolves `kafka` via Docker DNS.
- On the host OS, a client connects to `localhost:9092`. Kafka advertises `localhost:9092`.
- Without dual listeners, Kafka would advertise `localhost:9092` to the container (which would fail inside the container), or `kafka:29092` to the host (which the host OS cannot resolve).

> 🔗 **Code Reference**: Inspect [springboot/docker-compose.yml](file:///Users/yogeshwarpatel/Workspace/interview/springboot/docker-compose.yml#L55-L56) to see the exact production configuration.

---

## 5. Network CLI Commands for Daily Use

```bash
# List all Docker networks on the host
docker network ls

# Create a custom bridge network with specific subnet
docker network create --driver bridge --subnet 192.168.100.0/24 custom-net

# Inspect network details (see connected containers and assigned IP addresses)
docker network inspect app-network

# Connect a running container to an existing network
docker network connect custom-net springboot-app

# Disconnect a container from a network
docker network disconnect custom-net springboot-app

# Remove an unused network
docker network rm custom-net
```

---

## 🎯 Senior Engineer Interview Q&A

### Q1: Why should you avoid using the default `bridge` network in production?
**Answer:**
1. **No Automatic DNS**: Containers on the default bridge can only reach each other by static IP address or deprecated `--link` flags, making service discovery fragile.
2. **No Isolation**: All containers that do not explicitly specify a network land on default `bridge`, allowing unintended traffic between unrelated containers.
3. **User-Defined Bridges**: User-defined bridge networks provide built-in DNS resolution via `127.0.0.11`, better isolation, and configurable MTU/iptables settings.

---

### Q2: How does Docker container DNS resolution work internally?
**Answer:**
When a container is attached to a user-defined network, Docker configures the container's `/etc/resolv.conf` with a nameserver IP pointing to `127.0.0.11` (Docker's embedded DNS server).  
When the app requests `http://redis:6379`:
1. Query goes to `127.0.0.11:53`.
2. Embedded DNS server checks if `redis` is a valid container name or service alias on the container's network.
3. If matched, it returns the internal container IP (e.g., `172.28.0.3`).
4. If unmatched, it forwards the query to the host's upstream DNS nameservers (e.g., 8.8.8.8).
