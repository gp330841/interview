# 03. Docker Volumes & Storage

This guide covers persistent data management in Docker, comparing Bind Mounts, Named Volumes, Anonymous Volumes, and `tmpfs` mounts, alongside host file permission traps and performance considerations.

---

## 1. The 4 Docker Storage Options

Containers are ephemeral by default: when a container is deleted, all data written to its top writable layer is permanently lost. Docker provides 4 ways to persist or isolate storage:

```
+-------------------------------------------------------------------------+
|                                HOST OS                                  |
|                                                                         |
|  +---------------------+   +---------------------+   +---------------+  |
|  |    BIND MOUNT       |   |    NAMED VOLUME     |   |  TMPFS MOUNT  |  |
|  | /path/on/host/conf  |   | /var/lib/docker/    |   |  Host System  |  |
|  |                     |   | volumes/my-data/_data|  |  RAM (Memory) |  |
|  +----------+----------+   +----------+----------+   +-------+-------+  |
+-------------|-------------------------|----------------------|----------+
              |                         |                      |
              v                         v                      v
+-------------+-------------------------+----------------------+----------+
|                       CONTAINER FILESYSTEM                              |
| /app/config                /var/lib/redis/data           /tmp/cache     |
+-------------------------------------------------------------------------+
```

| Type | Stored On Host | Managed By Docker | Performance | Primary Use Case |
| :--- | :--- | :--- | :--- | :--- |
| **Bind Mount** | Anywhere on host filesystem | No (user specifies exact host path) | Fast on Linux; slower on macOS/Win Docker Desktop | Mounting source code, host configs (`prometheus.yml`) |
| **Named Volume** | Dedicated Docker storage (`/var/lib/docker/volumes/`) | Yes (`docker volume` CLI) | High performance native speed across all platforms | Production databases (PostgreSQL, Redis, Kafka) |
| **Anonymous Volume** | Docker storage directory (random hash name) | Yes | High performance | Temporary isolation; preventing host overrides of container paths |
| **`tmpfs` Mount** | Host System RAM (memory only) | Yes | Blazing fast (RAM speed) | Sensitive credentials, volatile temporary scratch space |

---

## 2. Storage Deep Dive & Code References

### A. Bind Mounts (Host File / Directory Mapping)
A bind mount maps a specific file or directory on the host directly into the container.

```yaml
# Excerpt from springboot/docker-compose.yml
prometheus:
  image: prom/prometheus:v2.50.1
  volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml
```
- **Why use Bind Mount here?**: Changes made to `prometheus.yml` on the host machine are instantly reflected inside the Prometheus container without rebuilding the Docker image.

> 🔗 **Code Reference**: Inspect [springboot/docker-compose.yml](file:///Users/yogeshwarpatel/Workspace/interview/springboot/docker-compose.yml#L79) to see bind mounting of configuration files.

### B. Named Volumes (Docker-Managed Persistence)
Named volumes are isolated from host directory structures and fully managed by Docker daemon.

```yaml
# Docker Compose syntax for named volumes
services:
  postgres:
    image: postgres:16-alpine
    volumes:
      - postgres-data:/var/lib/postgresql/data

volumes:
  postgres-data:
    driver: local
```

---

## 3. Storage Permissions & Non-Root User Traps

A common production issue occurs when running containers as non-root users (`USER spring:spring`) alongside volume mounts:

### The Problem:
- Host directory mounted via Bind Mount is owned by Host User UID `1000`.
- Container runs under user `spring` with UID `101`.
- Container attempts to write to `/application/logs` and fails with `Permission Denied`.

### Senior Engineering Solutions:
1. **Fix Host Directory Ownership**:
   ```bash
   # Align host directory owner with container user UID
   sudo chown -R 101:101 ./logs
   ```
2. **Init-Container / Entrypoint Chown Pattern**:
   Run a lightweight shell entrypoint script as root that executes `chown -R spring:spring /data` before stepping down context via `exec su-exec spring "$@"`.

---

## 4. Volume CLI Commands for Daily Use

```bash
# Create a named volume
docker volume create redis-data

# List all volumes on the system
docker volume ls

# Inspect volume details (see real host mount path /var/lib/docker/volumes/...)
docker volume inspect redis-data

# Remove a specific volume
docker volume rm redis-data

# Clean up all dangling (unattached) volumes to free disk space
docker volume prune -f
```

---

## 🎯 Senior Engineer Interview Q&A

### Q1: What is the difference between a Bind Mount and a Named Volume, and when would you choose one over the other?
**Answer:**
- **Bind Mounts**: Depend on the host's explicit directory structure (e.g., `/home/user/app/config`). They are ideal for local development (live code reloading, mounting config files like `prometheus.yml`). However, they are non-portable across environments and can suffer performance degradation on macOS/Windows Docker Desktop due to virtual filesystem synchronization overhead (gRPC Fused / VirtioFS).
- **Named Volumes**: Completely managed by Docker inside `/var/lib/docker/volumes/`. They are portable, isolated from host path changes, support volume drivers (such as AWS EBS or NFS plugins), and deliver maximum native I/O performance. They are the standard choice for production database persistence.

---

### Q2: How do you prevent sensitive data (like secret keys or passwords) from being written to disk inside a container?
**Answer:**
Use **`tmpfs` mounts**. A `tmpfs` mount mounts a segment of host system RAM into the container filesystem. Data written to a `tmpfs` directory is never written to disk or the container writable layer; it resides strictly in memory and is automatically erased when the container stops.
```bash
docker run -d --tmpfs /tmp/secrets:rw,noexec,nosuid my-app
```
