# Spring AI Module (gen-ai-spring-ai)

Purpose

A focused Spring Boot demo showing how to integrate Spring AI's ChatClient, memory advisors, and safe mock fallbacks for local development.

Goals

- Demonstrate ChatClient usage, message memory advisors, and safe function-calling patterns.
- Provide an opinionated local dev setup that works without secret keys (mock mode).
- Show how to wire persistent ChatMemoryStore implementations (Redis/Postgres) for production.

Quick start

1. (Optional) export OPENAI_API_KEY
   export OPENAI_API_KEY="YOUR_OPENAI_KEY"
2. From repo root:
   mvn -pl ai/gen-ai-spring-ai -T 1C clean package
   mvn -pl ai/gen-ai-spring-ai spring-boot:run

Default port & endpoints

- Port: 8081
- Chat endpoint: GET /api/ai/spring/chat?message=Hello
- Health: GET /actuator/health

Configuration & profiles

- application-local.yml enables mock ChatModel when OPENAI_API_KEY is missing.
- application-prod.yml expects a real provider and production-grade settings (timeouts, rate-limiting).

Developer tips

- To test conversation memory, use the sessionId query param where supported.
- Replace InMemoryChatMemory with RedisChatMemoryStore for horizontal scaling.

Security

- Never commit API keys. Use environment variables, Vault, or GitHub Secrets in CI.

References

- See the module codemap in this README and the top-level ai/README for cross-module commands and tracing tips.


This module is a dedicated Java Spring Boot project demonstrating the integration of **Spring AI** (1.0.0-M6) using standard Spring patterns.

---

## 🛠️ Key Technologies & Abstractions

### 1. ChatModel Abstraction
Spring AI defines `org.springframework.ai.chat.model.ChatModel` as the standard interface to interact with LLMs. Auto-configuration maps application properties matching `spring.ai.openai.*` to configure `OpenAiChatModel` beans.

### 2. Fluent ChatClient
The `ChatClient` is Spring AI's modern fluent API to construct prompts, set system messages, define template parameters, and execute chat generation loops.

### 3. AOP Advisors (Interceptors)
Spring AI uses **Advisors** to intercept prompt executions.
* **`MessageChatMemoryAdvisor`:** Automatically fetches historical chat messages from an `InMemoryChatMemory` window and appends them to the current prompt payload, enabling conversation history.

---

## 📂 Code Map

* [SpringAiApplication.java](src/main/java/com/interview/ai/SpringAiApplication.java): Standard Spring Boot application entry point.
* [SpringAiConfig.java](src/main/java/com/interview/ai/config/SpringAiConfig.java): Instantiates the `ChatClient` and handles mock `ChatModel` fallbacks if the OpenAI API Key is missing.
* [SpringAiController.java](src/main/java/com/interview/ai/controller/SpringAiController.java): Exposes REST endpoints to test chat interactions.

---

## 🚀 Running the Project

### Step 1: Set OpenAI API Key
```bash
export OPENAI_API_KEY="your-openai-api-key"
```
*Note: If no key is set, the application defaults to a mock response mode to prevent boot crashes.*

### Step 2: Build & Run
Run from the root workspace directory:
```bash
mvn clean package -pl ai/gen-ai-spring-ai
mvn spring-boot:run -pl ai/gen-ai-spring-ai
```

---

## 📡 API Testing Endpoint

### Spring AI (Chat with History)
Exposes the Spring AI `ChatClient` mapped to port **8081**:
```bash
curl "http://localhost:8081/api/ai/spring/chat?message=Hello! I am Yogeshwar"
# Response: "[MOCK SPRING AI RESPONSE] Hello! Your query was: "Hello! I am Yogeshwar"..."
```
