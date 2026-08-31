# Technical Interview Preparation Portal

This repository serves as a centralized hub for engineering and architecture interview preparation, containing coding solutions, deep-dive architectural guides, systems engineering notes, and hands-on designs.

---

## 📚 Study Guides & Topic Dashboards

Explore the deep-dive directories below for targeted interview preparation:

| Topic | Guide | Key Focus / Highlights |
| :--- | :--- | :--- |
| **System Design** | [High Level Design (HLD)](high-level-design/README.md) | Scalability, Load Balancers, Sharding, CAP/PACELC, Real-world cases (Twitter, Uber, Netflix). |
| **Object-Oriented Design** | [Low Level Design (LLD)](low-level-design/README.md) | Design Patterns, Clean Code principles, SOLID, and class structure diagrams. |
| **Cloud Computing** | [AWS Cloud Guide](aws-cloud/README.md) | Networking, Compute, Storage, Security, Serverless, Integration, and Scenario Questions. |
| **Databases** | [Database Systems](database/README.md) | SQL (MySQL) vs NoSQL (MongoDB), wired-tiger internals, indexing, MVCC, and replication. |
| **Backend Frameworks** | [Spring Boot & Devops](springboot/README.md) | Spring MVC/Boot internals, Docker containment, Kubernetes orchestration configs. |
| **Core Language** | [Java Concepts](java-concepts/README.md) | Concurrency, JVM/GC tuning, Collections framework internals, Lambdas, and serialization. |
| **Artificial Intelligence** | [Artificial Intelligence (AI)](ai/README.md) | Generative AI, Java/Python LLM integration, prompt engineering, and agent systems. |

---

## 📁 Repository Structure

*   `codingInInterview/` - Core DSA solutions, coding challenges, and problem-solving logs.
*   `problems/java/` - Java-based implementations for standard data structures and algorithmic problems.
*   `problems/text/` - Textual notes, explanations, and non-coding responses to interview questions.

---

## Monorepo (Maven) usage
This repository is organized as a Maven multi-module project (root pom.xml with <modules/> entries). Useful commands:

- Build and install all modules: mvn -T 1C clean install
- Build a specific module and its dependencies: mvn -pl <module> -am clean install
- Run tests for all modules: mvn test
- Run the springboot module locally: mvn -pl springboot spring-boot:run

Notes:
- Consider adding the Maven Wrapper to ensure consistent Maven version for contributors (e.g., run a local maven wrapper generation command like `mvn -N io.takari:maven:wrapper`).
- CI should run a multi-module install (e.g., `mvn -T 1C clean install`).
- A convenient bootstrap script is available at `./scripts/bootstrap.sh` to run a parallel multi-module build locally.

