# Java Generative AI Reference Manual (gen-ai-java)

This module is a production-grade playground and learning directory demonstrating Generative AI patterns in Java. It showcases how to design, build, and debug stateful AI applications using **Spring AI**, **LangChain4j**, and a custom **Java State Graph Engine** (recreating LangGraph in Java), all instrumented for tracing via **LangSmith**.

---

## 1. Spring AI Architecture & Advisor Interceptors

Spring AI integrates LLM models using standard Spring abstractions, utilizing Spring Boot auto-configuration, dependency injection, and fluent builder APIs.

```
[HTTP Request] ──> Controller ──> ChatClient.prompt() 
                                      │
                         ┌────────────▼────────────┐
                         │   Advisor Interceptors  │
                         └────────────┬────────────┘
                                      ├─────────────────────────┐
                         ┌────────────▼────────────┐            │
                         │MessageChatMemoryAdvisor │            │
                         │ (Load historical chat)  │            │
                         └────────────┬────────────┘            │
                                      ├─────────────────────────┤
                         ┌────────────▼────────────┐            │
                         │QuestionAnswerAdvisor    │            │
                         │ (Inject vector context) │            │
                         └────────────┬────────────┘            │
                                      │<────────────────────────┘
                         ┌────────────▼────────────┐
                         │   ChatModel.call()      │ (Send assembled prompt)
                         └─────────────────────────┘
```

### Key Architectural Concepts
1. **ChatModel Abstract Layer:**
   Spring AI defines `org.springframework.ai.chat.model.ChatModel` as the unified interface for interacting with LLMs. Auto-configuration maps properties prefixing `spring.ai.openai.*` into `OpenAiChatModel` instances.
2. **ChatClient (Fluent API):**
   `ChatClient` provides a builder-based interface to build prompts dynamically.
3. **Advisors (Aspect-Oriented Interceptors):**
   Advisors are interceptors that wrap the execution of LLM calls to modify the input prompt or output response.
   * **`MessageChatMemoryAdvisor`:** Intercepts the request, fetches past messages from the configured `ChatMemory` store, and appends them to the prompt context.
   * **`QuestionAnswerAdvisor` (RAG Advisor):** Intercepts the query, executes a similarity search against the registered `VectorStore`, formats matching chunks into a context string, and injects it into the prompt templates.

---

## 2. LangChain4j Architecture & Dynamic Proxy Interception

LangChain4j uses a declarative paradigm modeled after Spring Data JPA's repository pattern. You define an interface, and the framework instantiates the active execution client.

```
1. CustomerSupportAgent agent = AiServices.builder(CustomerSupportAgent.class)...build();
                                           │
                                           ▼
             Dynamically creates Class: $Proxy14 implements CustomerSupportAgent
                                           │
                                           ▼
2. agent.answer("Is flight LH123 delayed?") ──> Intercepted by InvocationHandler.invoke()
                                           │
                                           ▼
3. Scans method annotations:
   * @SystemMessage: Extracts system boundaries.
   * @UserMessage: Binds variables to template.
   * Scans @Tool classes -> Generates List<ToolSpecification> schemas.
                                           │
                                           ▼
4. Fetches chat history from TokenWindowChatMemory using @MemoryId.
                                           │
                                           ▼
5. Invokes ChatLanguageModel.generate(messages, toolSpecifications)
```

### The Tool Invocation Loop (Function Calling)
If the LLM determines that answering the query requires a Java method execution:

```
[LLM Response] ──> ToolExecutionRequest (method: "getBookingStatus", args: "LH123")
                          │
             ┌────────────▼────────────┐
             │ Jackson ObjectMapper     │ (Deserializes JSON args to Java types)
             └────────────┬────────────┘
                          ▼
             ┌────────────▼────────────┐
             │ reflection Method.invoke │ (Executes BookingTools.getBookingStatus)
             └────────────┬────────────┘
                          ▼
             ┌────────────▼────────────┐
             │ ToolExecutionResultMsg  │ (Wraps return string "DELAYED")
             └────────────┬────────────┘
                          ▼
             Loop back to LLM with updated Message History
```

---

## 3. Custom Java State Graph (LangGraph in Java)

Because there is no official Java port of LangGraph (which is Python/JS-only), this project implements a clean, generic **State Graph Engine** using pure Java functional interfaces.

### Core Architecture Classes
* **[AgentState.java](src/main/java/com/interview/ai/langgraph/AgentState.java):** Captures the mutable state variables (conversation messages, thread session ID) that are modified by graph nodes.
* **[StateGraph.java](src/main/java/com/interview/ai/langgraph/StateGraph.java):** A builder API using functional interfaces to register execution nodes (`StateGraph.Node<T>`) and conditional routers (`StateGraph.ConditionalEdge<T>`).
* **[CompiledGraph.java](src/main/java/com/interview/ai/langgraph/CompiledGraph.java):** Houses the main execution loop:
  ```java
  public T execute(T initialState) {
      String currentNode = entryPoint;
      T state = initialState;
      while (currentNode != null && !currentNode.equals("__end__")) {
          state = nodes.get(currentNode).apply(state);
          if (conditionalEdges.containsKey(currentNode)) {
              currentNode = conditionalEdges.get(currentNode).apply(state);
          } else {
              currentNode = edges.get(currentNode);
          }
      }
      return state;
  }
  ```
* **[LangGraphService.java](src/main/java/com/interview/ai/langgraph/LangGraphService.java):** Assembles these constructs into a ReAct execution flow:
  1. Entry point is the `agent` node. It queries the model and binds tool specifications.
  2. The conditional router `routeNext` checks if the last message contains tool requests. If yes, it routes to `tools`. If no, it routes to `__end__`.
  3. The `tools` node executes the requested methods, appends results, and routes back to `agent` to complete the loop.

---

## 4. Step-by-Step RAG Pipeline (`SimpleRagService`)

This service illustrates the step-by-step architecture of a Retrieval-Augmented Generation pipeline using local text file ingestion:

```
[Ingestion Step]
faq.txt ──> Loaded via Resource ──> Document Splitter (recursive: 150 chars, 30 overlap)
                                                │
                                    ┌───────────▼───────────┐
                                    │ TextSegment list      │
                                    └───────────┬───────────┘
                                                ▼
                                    ┌───────────▼───────────┐
                                    │ EmbeddingModel.embed  │ (Generate vectors)
                                    └───────────┬───────────┘
                                                ▼
                                    InMemoryEmbeddingStore.add(vector, chunk)

[Query Step]
User Query ──> EmbeddingModel.embed ──> InMemoryEmbeddingStore.findRelevant(vector, limit: 2)
                                                │
                                    ┌───────────▼───────────┐
                                    │ Relevant Chunks       │
                                    └───────────┬───────────┘
                                                ▼
                                    Inject context into Prompt ──> ChatLanguageModel
```

* **Fallback Design:** When the OpenAI API key is missing, the service falls back to a mock embedding and a regex-based string locator to ensure the pipeline runs out-of-the-box.

---

## 🚀 Setup & Execution Guide

### Step 1: Set Environment Variables
```bash
export OPENAI_API_KEY="your-openai-api-key"

# For LangSmith Tracing:
export LANGCHAIN_TRACING_V2="true"
export LANGCHAIN_API_KEY="your-langsmith-api-key"
export LANGCHAIN_PROJECT="gen-ai-java-learning"
```

### Step 2: Build and Run
Execute the following commands from the root directory of the workspace:
```bash
mvn clean package -pl ai/gen-ai-java
mvn spring-boot:run -pl ai/gen-ai-java
```

---

## 📡 API Testing Endpoint Commands

Use `curl` to verify all integrated systems:

### 1. Spring AI (Basic Chat with History)
Verifies `ChatClient` with `MessageChatMemoryAdvisor` state persistence:
```bash
curl "http://localhost:8080/api/ai/spring/chat?message=Hello! I am Yogeshwar"
# Response: "Hello Yogeshwar! How can I help you today?"

curl "http://localhost:8080/api/ai/spring/chat?message=What is my name?"
# Response: "Your name is Yogeshwar."
```

### 2. LangChain4j Agent (With Automatic Tool Calling)
Verifies `@SystemMessage` boundaries and `@Tool` method reflection invocations:
```bash
curl "http://localhost:8080/api/ai/langchain/agent?message=Is booking LH123 delayed?"
# Response: "Yes, LH123 is currently delayed. The status is: DELAYED..."
```

### 3. Retrieval-Augmented Generation (RAG)
Verifies document loading, splitter chunking, and similarity search context injections:
```bash
curl "http://localhost:8080/api/ai/rag/query?question=What is the return policy?"
# Response: "According to the FAQ, items must be returned within 30 days of purchase..."
```

### 4. Custom Java State Graph Agent (Memory + Routing Loop)
Verifies graph execution loops and conditional edge routers running inside the JVM:
```bash
curl "http://localhost:8080/api/ai/langgraph/chat?sessionId=session-123&message=Is flight LH123 delayed?"
# Response: "I queried the booking database. Flight LH123 status: DELAYED..."

curl "http://localhost:8080/api/ai/langgraph/chat?sessionId=session-123&message=What flight code did I ask about?"
# Response: "You asked about flight LH123."
```
