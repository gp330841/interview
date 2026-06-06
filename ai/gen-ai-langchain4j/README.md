# LangChain4j & Custom State Graph Module (gen-ai-langchain4j)

This module demonstrates advanced Generative AI orchestration patterns in Java (Spring Boot) using **LangChain4j** and a custom **Java State Graph Engine** (recreating Python's LangGraph architecture in the JVM).

---

## 🛠️ Key Technologies & Abstractions

### 1. LangChain4j Declarative AI Services
LangChain4j maps LLM execution calls to standard Java interfaces using JDK Dynamic Proxies:
* **`AiServices`:** Generates proxies from interfaces decorated with `@SystemMessage` (agent rules) and `@UserMessage` (parameter template bindings).
* **Automatic Function Calling (`@Tool`):** Scans registered beans for `@Tool` methods, constructs JSON Schema specifications to send to the LLM, and invokes matching Java methods via reflection when the LLM generates a tool call request.

### 2. Custom Java State Graph (LangGraph in Java)
Recreates Python's LangGraph state machine model natively in Java:
* **`AgentState`:** Thread-safe state container tracking messages and session IDs.
* **`StateGraph<T>`:** Graph layout configurator allowing node registrations and conditional routers.
* **`CompiledGraph<T>`:** Orchestrates the loop transition states until hitting `"__end__"`.
* **`LangGraphService`:** Builds the graph as a ReAct agent using the model and local tools.

### 3. Step-by-Step RAG (`SimpleRagService`)
Illustrates custom RAG workflows: loads files, splits text into overlapping character chunks, embeds text using `EmbeddingModel`, indexes vectors inside `InMemoryEmbeddingStore`, retrieves top-K matches via similarity queries, and injects context into prompt generations.

---

## 📂 Code Map

* [LangChain4jApplication.java](src/main/java/com/interview/ai/LangChain4jApplication.java): Spring Boot starter.
* [LangChain4jConfig.java](src/main/java/com/interview/ai/config/LangChain4jConfig.java): Configures `ChatLanguageModel`, `ChatMemory`, and proxy agent bindings.
* [CustomerSupportAgent.java](src/main/java/com/interview/ai/service/CustomerSupportAgent.java): Declarative agent interface.
* [BookingTools.java](src/main/java/com/interview/ai/service/BookingTools.java): Mock methods exposing tool functions (`@Tool`).
* [SimpleRagService.java](src/main/java/com/interview/ai/rag/SimpleRagService.java): Complete local RAG pipeline implementation.
* [LangChain4jController.java](src/main/java/com/interview/ai/controller/LangChain4jController.java): REST endpoints exposing the RAG and agent systems.
* **`com.interview.ai.langgraph` package:**
  * [StateGraph.java](src/main/java/com/interview/ai/langgraph/StateGraph.java): Graph topology configurator.
  * [CompiledGraph.java](src/main/java/com/interview/ai/langgraph/CompiledGraph.java): State transition loop engine.
  * [AgentState.java](src/main/java/com/interview/ai/langgraph/AgentState.java): Context memory collector.
  * [LangGraphService.java](src/main/java/com/interview/ai/langgraph/LangGraphService.java): ReAct graph agent service.
  * [LangGraphController.java](src/main/java/com/interview/ai/langgraph/LangGraphController.java): Chat endpoint on port **8082**.

---

## 🚀 Running the Project

### Step 1: Set API Keys & Tracing
```bash
export OPENAI_API_KEY="your-openai-api-key"

# For LangSmith Tracing:
export LANGCHAIN_TRACING_V2="true"
export LANGCHAIN_API_KEY="your-langsmith-api-key"
export LANGCHAIN_PROJECT="gen-ai-langchain4j-learning"
```

### Step 2: Build & Run
Run from the root workspace directory:
```bash
mvn clean package -pl ai/gen-ai-langchain4j
mvn spring-boot:run -pl ai/gen-ai-langchain4j
```

---

## 📡 API REST Endpoints (Port 8082)

Verify all functions using `curl`:

### 1. LangChain4j Agent (With Automatic Tool Calling)
```bash
curl "http://localhost:8082/api/ai/langchain/agent?message=Is booking LH123 delayed?"
# Response: "[MOCK RESPONSE] Flight LH123 is currently DELAYED..."
```

### 2. Retrieval-Augmented Generation (RAG)
```bash
curl "http://localhost:8082/api/ai/rag/query?question=What is the return policy?"
# Response: "[MOCK RAG] Our standard return policy allows customers to return unopened products..."
```

### 3. Custom Java State Graph Agent (Memory + Routing Loop)
```bash
curl "http://localhost:8082/api/ai/langgraph/chat?sessionId=session-123&message=Is flight LH123 delayed?"
# Response: "[Java LangGraph Mock Agent] I queried the booking database. Flight LH123 status: DELAYED..."
```
