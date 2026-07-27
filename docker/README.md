# Comprehensive Docker & Containerization Master Guide

Welcome to the **Docker Master Guide**. This modular repository section covers everything from core container architecture to advanced networking, persistent storage, multi-stage build optimization, Docker Compose orchestration, and production troubleshooting.

This guide is designed for **daily engineering reference** and **senior software engineering interview preparation**.

---

## 🗺️ Learning Roadmap

| Module | Title | Primary Topics Covered | Practical Application |
| :--- | :--- | :--- | :--- |
| [01_Basics_and_Architecture.md](file:///Users/yogeshwarpatel/Workspace/interview/docker/01_Basics_and_Architecture.md) | **Basics & Architecture** | Docker Engine, Client vs Daemon, Namespaces, Cgroups, Copy-on-Write (CoW), Overlay2 | Understanding containers vs VMs, process isolation |
| [02_Networks.md](file:///Users/yogeshwarpatel/Workspace/interview/docker/02_Networks.md) | **Container Networking** | Bridge, Host, Overlay, Macvlan, Embedded DNS (`127.0.0.11`), Dual Listeners | Inter-container routing, Kafka network design |
| [03_Volumes_and_Storage.md](file:///Users/yogeshwarpatel/Workspace/interview/docker/03_Volumes_and_Storage.md) | **Volumes & Persistent Storage** | Bind Mounts, Named Volumes, Anonymous Volumes, `tmpfs`, UID/GID Permissions | Database persistence, live config reloading |
| [04_Dockerfile_Deep_Dive.md](file:///Users/yogeshwarpatel/Workspace/interview/docker/04_Dockerfile_Deep_Dive.md) | **Dockerfile Engineering** | Instructions (`COPY` vs `ADD`, Exec vs Shell form), Multi-stage builds, Layer caching (`layertools`) | Building small, secure sub-second Spring Boot images |
| [05_Docker_Compose_Deep_Dive.md](file:///Users/yogeshwarpatel/Workspace/interview/docker/05_Docker_Compose_Deep_Dive.md) | **Docker Compose Orchestration** | Spec 3.8, Service dependencies (`depends_on`), Healthchecks, Env variable fallbacks | 5-Service stack orchestration (App+Redis+Kafka+Prom+Grafana) |
| [06_Production_and_Troubleshooting.md](file:///Users/yogeshwarpatel/Workspace/interview/docker/06_Production_and_Troubleshooting.md) | **Production Operations & Hardening** | Non-root security, Signals (`SIGTERM`), Memory tuning (`MaxRAMPercentage`), Distroless vs Alpine, Debugging | Resolving OOM Kills, debugging live production containers |

---

## 🏗️ Real-World Reference Project

Throughout this guide, theoretical concepts are directly mapped to our production-grade Spring Boot 3.4.2 system setup:

- 📄 **Multi-Stage Dockerfile**: [springboot/Dockerfile](file:///Users/yogeshwarpatel/Workspace/interview/springboot/Dockerfile)
- 📄 **Multi-Service Docker Compose**: [springboot/docker-compose.yml](file:///Users/yogeshwarpatel/Workspace/interview/springboot/docker-compose.yml)
- 📄 **Spring Boot Architecture Guide**: [springboot/DOCKER_README.md](file:///Users/yogeshwarpatel/Workspace/interview/springboot/DOCKER_README.md)

---

## 💡 How to Use This Guide

- **For Daily Development**: Use command cheat-sheets and practical code snippets in each module.
- **For Interviews**: Focus on the **Senior Engineer Q&A** section at the end of each module, which highlights critical edge cases, failure modes, and architectural trade-offs.
