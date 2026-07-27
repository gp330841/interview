# 06. Production Operations, Security & Troubleshooting

This guide covers production container hardening, JVM cgroup memory tuning, signal propagation, distroless debugging, and real-world troubleshooting playbooks for senior engineers.

---

## 1. Production Security Hardening Matrix

| Security Rule | Risk Without Mitigation | Hardening Implementation |
| :--- | :--- | :--- |
| **Non-Root Execution** | Container Escape RCE grants root access to host node | Add unprivileged user `USER spring:spring` in Dockerfile |
| **Read-Only Root Filesystem** | Malware writes persistent exploit binaries into container `/app` or `/bin` | Set `read_only: true` in Compose; mount `tmpfs` for temporary `/tmp` |
| **Drop Linux Capabilities** | Kernel exploits leverage default capabilities (`CAP_NET_RAW`, `CAP_SYS_CHROOT`) | Set `cap_drop: [ALL]` in Compose spec |
| **No Privileged Mode** | `--privileged` disables ALL isolation, giving full host kernel hardware access | Never use `privileged: true` in production |
| **No Docker Socket Mounting** | Mounting `/var/run/docker.sock` gives container full host daemon control | Never mount Docker socket unless building dedicated CI agents |

### Production Docker Compose Security Snippet:
```yaml
services:
  app:
    image: springboot-app:1.0
    user: "10001:10001"
    read_only: true
    tmpfs:
      - /tmp:rw,noexec,nosuid,size=64m
    security_opt:
      - no-new-privileges:true
    cap_drop:
      - ALL
```

---

## 2. JVM Cgroup Memory Tuning & OOM Killer Analysis

### The `OOMKilled` Exit Code 137 Anatomy:
When a container exceeds its allocated RAM limit (e.g., `-m 512m`), the Linux kernel Out-Of-Memory (OOM) Killer immediately terminates the process inside the container. The container exits with **Exit Code 137** (`128 + 9 (SIGKILL)`).

```
                      CONTAINER MEMORY LIMIT (512 MB)
+--------------------------------------------------------------------------+
|  JAVA HEAP (MaxRAMPercentage = 75% -> ~384MB)                           |
|  - Active Objects, Spring Beans, Cache                                   |
+--------------------------------------------------------------------------+
|  NON-HEAP OVERHEAD (Remaining 25% -> ~128MB)                              |
|  - Metaspace (Class metadata)                                             |
|  - Thread Stacks (1MB per thread x 100 threads = 100MB!)                  |
|  - GC Native Memory & Direct Byte Buffers (Netty/Kafka I/O)                |
|  - Code Cache                                                            |
+--------------------------------------------------------------------------+
  ===>>> IF TOTAL HEAP + NON-HEAP > 512MB  ==>  KERNEL OOM KILLS (EXIT 137)
```

### Senior Engineer JVM Configuration Rules:
1. **Never set `-Xmx` equal to the total container memory limit**.
2. **Use Dynamic RAM Percentages**:
   ```dockerfile
   ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+UseG1GC"
   ```
3. Leaves 25% overhead (~128MB on 512MB container) for Metaspace, Thread Stacks, and GC native structures.

---

## 3. Base Image Trade-Off Analysis

| Base Image | Size | C-Standard Library | Pros | Cons |
| :--- | :--- | :--- | :--- | :--- |
| **`eclipse-temurin:25-jre-alpine`** | ~150MB | `musl libc` | Extremely small footprint | `musl` memory allocator can be slower under heavy multi-threading; potential DNS quirks |
| **`gcr.io/distroless/java21`** | ~180MB | `glibc` | Minimal attack surface (no shell, no package manager) | Harder to debug (no `bash`, `curl`, or `ls` available inside container) |
| **`eclipse-temurin:25-jre-jammy`** | ~250MB | `glibc` (Debian/Ubuntu) | Maximum compatibility with native Java libraries (Netty epoll) | Larger image size |

---

## 4. Production Troubleshooting Playbook

### Scenario A: Container Keeps Crashing (`CrashLoopBackOff`)
```bash
# 1. Check exit code of crashed container
docker inspect springboot-app --format='{{.State.ExitCode}}'

# 2. View last 100 lines of container logs (including stderr)
docker logs --tail 100 springboot-app

# 3. Check if container was killed by OOM Killer
docker inspect springboot-app --format='{{.State.OOMKilled}}'
```

### Scenario B: High CPU / Memory Leak in Live Container
```bash
# 1. Check real-time resource utilization
docker stats springboot-app

# 2. Execute thread dump inside running container
docker exec -it springboot-app jcmd 1 Thread.print > thread_dump.txt

# 3. Trigger heap dump for memory leak analysis
docker exec -it springboot-app jcmd 1 GC.heap_dump /tmp/heap_dump.hprof
```

### Scenario C: Debugging Distroless / Shell-less Containers
If a container has no shell (`/bin/sh` missing in Distroless):
```bash
# Use Docker 24+ container debug command to attach ephemeral debug container
docker debug springboot-app
```

---

## 🎯 Senior Engineer Interview Q&A

### Q1: How do you identify why a container exited with code 137, and how do you fix it?
**Answer:**
Exit Code 137 indicates the container process received `SIGKILL` (signal 9), which almost always means the Linux kernel OOM Killer terminated it for exceeding cgroup memory limits (`docker inspect --format='{{.State.OOMKilled}}'`).  
**Fix Strategy**:
1. Check container RAM limit in Compose/K8s (`limits.memory: 512Mi`).
2. Adjust JVM memory flags to restrict heap to 75% of limit (`-XX:MaxRAMPercentage=75.0`).
3. If off-heap Metaspace or thread stack allocation is leaking, analyze thread dump counts or increase container RAM ceiling.

---

### Q2: What happens during `docker stop` vs `docker kill`?
**Answer:**
- **`docker stop`**: Sends `SIGTERM` (signal 15) to PID 1 inside the container, initiating graceful shutdown. It starts a grace period timer (default 10 seconds). If the process exits cleanly before the timer expires, Docker returns success. If the timer expires, Docker sends `SIGKILL` (signal 9).
- **`docker kill`**: Sends `SIGKILL` (signal 9) immediately to PID 1, abruptly terminating the process without giving it any opportunity to run cleanup hooks or finish active transactions.
