# The Complete Generative AI & System Design Reference Handbook

This handbook serves as the single source of truth for Generative AI engineering, covering the deep mathematical foundations of Large Language Models (LLMs), inference parameters, Retrieval-Augmented Generation (RAG) system design, Agentic state machines, and JVM-specific execution mechanics.

---

## 1. Deep Architecture & Mathematical Foundations

### The Transformer Block (Decoder-Only Architecture)
Most modern text-generation models (e.g., Llama, GPT, Mistral) utilize a **Decoder-Only Transformer** architecture. Unlike Encoder-Decoder networks (like original T5), the decoder-only model predicts tokens sequentially from left to right, employing **Masked Self-Attention** to prevent looking at future tokens.

```
Input Tokens --> Input Embeddings + Positional Embeddings (RoPE)
                         │
              ┌──────────▼──────────┐
              │  Pre-LayerNorm      │
              └──────────┬──────────┘
                         ├────────────────────────┐
              ┌──────────▼──────────┐             │
              │ Masked Self-Attention│            │ (Residual Connection)
              └──────────┬──────────┘             │
                         │<───────────────────────┘
              ┌──────────▼──────────┐
              │  LayerNorm          │
              └──────────┬──────────┘
                         ├────────────────────────┐
              ┌──────────▼──────────┐             │
              │  Feed-Forward (SwiGLU)            │ (Residual Connection)
              └──────────┬──────────┘             │
                         │<───────────────────────┘
                         ▼
                    Next Logits
```

---

### A. Sub-word Tokenization Algorithms
Raw text is tokenized into integer IDs representing sub-word units. The choice of tokenization algorithm changes how the model handles vocabulary size, out-of-vocabulary (OOV) tokens, and numerical precision.

#### 1. Byte-Pair Encoding (BPE)
* **Used by:** GPT-4, Llama.
* **Mechanism:** 
  1. Starts with a vocabulary of individual bytes (256 characters) and special tokens.
  2. Iteratively scans the corpus, counts the most frequent adjacent byte pairs (e.g., `['t', 'h']`), and merges them into a new token (`'th'`).
  3. Repeats this process until reaching the target vocabulary size (e.g., 32,000 to 100,000+ tokens).
* **Interview Trade-off:** High vocabulary size reduces sequence length (saving compute), but increases the embedding layer's parameter weight size.

#### 2. WordPiece
* **Used by:** BERT.
* **Mechanism:** Similar to BPE, but instead of choosing the most frequent byte pairs, it merges pairs that maximize the likelihood of the language model corpus according to a probabilistic model.

---

### B. Embedding Vector Space & Distance Metrics
An embedding translates a token ID into a point in an $N$-dimensional space ($\mathbb{R}^d$, where $d \approx 1536$ to $4096$).

$$\mathbf{v} = \text{Embedding}(t) \in \mathbb{R}^d$$

#### Semantic Distance Calculations
During vector search (retrieval), the similarity between query vector $\mathbf{q}$ and document chunk vector $\mathbf{d}$ is computed using:

| Metric | Formula | Use Cases / Characteristics |
|--------|---------|-----------------------------|
| **Cosine Similarity** | $$\cos(\theta) = \frac{\mathbf{q} \cdot \mathbf{d}}{\|\mathbf{q}\| \|\mathbf{d}\|} = \frac{\sum_{i=1}^d q_i d_i}{\sqrt{\sum q_i^2}\sqrt{\sum d_i^2}}$$ | Meaures direction, ignoring vector magnitude. Best for varying document lengths. |
| **Dot Product** | $$\mathbf{q} \cdot \mathbf{d} = \sum_{i=1}^d q_i d_i$$ | Mathematically cheap. Highly accurate **only** if vectors are normalized to unit length ($\|\mathbf{v}\| = 1$), making it equivalent to Cosine. |
| **L2 (Euclidean) Distance** | $$d_{L2}(\mathbf{q}, \mathbf{d}) = \sqrt{\sum_{i=1}^d (q_i - d_i)^2}$$ | Measures straight-line distance. Sensitive to variations in document length and overall token density. |

---

### C. Self-Attention Mechanics
Self-Attention allows every token in a sequence to dynamically adjust its contribution to every other token based on context.

```
Token Embedding (X) ───┬──> Query Projection (Wq) ──> Q ──┐
                       ├──> Key Projection (Wk) ───> K ──┼──> Attention Matrix (Softmax(QK^T / sqrt(d))) ──> Multiplied by V
                       └──> Value Projection (Wv) ──> V ──┘
```

1. **Projection:**
   The input token representations matrix $X \in \mathbb{R}^{L \times d}$ is projected into Query ($Q$), Key ($K$), and Value ($V$) matrices using trained weight matrices $W_q, W_k, W_v \in \mathbb{R}^{d \times d_k}$:
   $$Q = X W_q, \quad K = X W_k, \quad V = X W_v$$

2. **Attention Scoring:**
   Compute the similarity dot product between Queries and Keys, scale it to avoid vanishing gradients, and apply a softmax activation to create a probability distribution:
   $$\text{Attention}(Q, K, V) = \text{softmax}\left(\frac{Q K^T}{\sqrt{d_k}}\right) V$$
   * **Scaling Factor ($\sqrt{d_k}$):** For higher dimensions, the dot products grow large, pushing the softmax function into regions with extremely small gradients. Dividing by $\sqrt{d_k}$ stabilizes training.

3. **Causal Masking (Decoder Only):**
   To prevent tokens from looking at future tokens, we set future scores to $-\infty$ before applying softmax:
   $$M_{i,j} = \begin{cases} 0 & \text{if } j \le i \\ -\infty & \text{if } j > i \end{cases}$$
   $$\text{Attention}(Q, K, V) = \text{softmax}\left(\frac{Q K^T}{\sqrt{d_k}} + M\right) V$$

---

### D. Positional Encoding: Sinusoidal vs. Rotary (RoPE)
Self-Attention is permutation-invariant—it treats order-independent tokens the same. Models must inject word order data.

#### 1. Sinusoidal Positional Encoding (Absolute)
Original transformer added absolute sine/cosine vectors to embeddings:
$$PE_{(pos, 2i)} = \sin\left(\frac{pos}{10000^{2i/d}}\right)$$
* **Limitation:** Does not scale well to sequences longer than those seen during training because absolute coordinates change completely.

#### 2. Rotary Position Embedding (RoPE) (Relative)
* **Used by:** Llama-3, Mistral.
* **Mechanism:** Instead of adding a vector, RoPE rotates the Query and Key vectors in 2D planes by an angle proportional to their sequence position.
* **Mathematics:** For a 2D slice of Query vector $\mathbf{q} = [q_1, q_2]^T$ at position $m$:
  $$R_{\Theta, m}^d \mathbf{q} = \begin{pmatrix} \cos(m\theta) & -\sin(m\theta) \\ \sin(m\theta) & \cos(m\theta) \end{pmatrix} \begin{pmatrix} q_1 \\ q_2 \end{pmatrix}$$
* **Why it works:** The inner product between Query at $m$ and Key at $n$ depends solely on the relative distance $(m - n)$:
  $$(R_m \mathbf{q}) \cdot (R_n \mathbf{k}) = \mathbf{q}^T R_{n-m} \mathbf{k}$$
  This relative formulation allows models to generalize context sizes (e.g., extending Llama-3 context from 8k to 128k using RoPE scaling factors).

---

## 2. Token Sampling & Generation Mathematics

LLMs output "logits"—raw, unnormalized log-probability scores for every token in the vocabulary. The generation process transforms these logits into actual token selections.

```
Raw Logits (z_i) ──> Temperature Scaling (z_i / T) ──> Top-K/Top-P Filtering ──> Softmax Normalization ──> Random Sample
```

### A. Temperature Scaling
Temperature ($T \in (0, \infty)$) scales the logits prior to applying the Softmax function:

$$P(x_i) = \frac{e^{z_i / T}}{\sum_j e^{z_j / T}}$$

* **Case $T \rightarrow 0$ (Argmax / Deterministic):**
  The highest logit dominates completely. The system always selects the single most probable token. Good for APIs requiring high consistency (e.g., Code writing, SQL queries, JSON generation).
* **Case $T = 1.0$ (Normal):**
  Tokens are selected exactly according to the model's trained probability distribution.
* **Case $T > 1.0$ (High Entropy / Creative):**
  Logits are squashed closer together. Low-probability tokens become significantly more likely to be selected, increasing creativity but risking hallucinations and grammatical errors.

---

### B. Top-K and Top-P (Nucleus) Filtering
To avoid selecting completely nonsensical tokens, we filter the distribution before sampling:

1. **Top-K:**
   Only consider the $K$ most probable tokens. All other tokens have their probability set to $0$.
2. **Top-P (Nucleus Sampling):**
   Sort the vocabulary by descending probability and select the smallest subset of tokens whose cumulative probability exceeds $P$ (e.g., $P = 0.90$).
   $$\sum_{i \in V_{\text{nucleus}}} P(x_i) \ge P$$
   * **Advantage over Top-K:** Dynamic vocab sizing. In highly confident contexts, the candidate pool shrinks to $1-2$ tokens; in ambiguous contexts, it expands to hundreds.

---

### C. The KV Cache (Key-Value Cache)
Generating a token requires calculating the Key and Value vectors for all past tokens in the sequence. During auto-regressive generation, re-computing these vectors at each step wastes massive compute resources ($O(L^2)$ matrix multiplications).

* **Mechanism:** The **KV Cache** stores the Keys ($K$) and Values ($V$) computed for all previous tokens in memory. At step $t$, we only project and compute $K_t$ and $V_t$ for the single new token, appending them to the cache.
* **System Design Bottleneck (Memory Bound):**
  KV Cache sizes grow linearly with context length and batch size:
  $$\text{KV Cache Size} = 2 \times (\text{layers}) \times (\text{heads}) \times (\text{dimension}) \times (\text{context length}) \times (\text{batch size}) \times 2 \text{ bytes (FP16)}$$
  For a Llama-3-8B model with batch size $32$ and context $8,192$, the KV Cache consumes $\approx 24\text{ GB}$ of GPU VRAM, making LLM inference highly memory-bound rather than compute-bound.

---

## 3. Retrieval-Augmented Generation (RAG) Architecture

RAG supplements prompt context with external data retrieved from a database, bypassing the need for expensive fine-tuning.

```
[Ingestion] Document ──> Structural Parser ──> Chunking ──> Embeddings ──> Vector DB Index
[Query]     User Query ──> Embeddings ──> Similarity Match ──> Reranker ──> Context Prompt ──> LLM
```

### A. Document Chunking Strategies
How you split documents directly impacts the retrieval quality:

* **Fixed-Size Chunking:** Split by character/token count (e.g., 500 characters with 50 character overlap). Fast but breaks semantic sentences.
* **Recursive Character Chunking:** Splits by a list of delimiters sequentially (typically `["\n\n", "\n", " ", ""]`), trying to keep paragraphs and sentences intact within the size limit.
* **Semantic Chunking:** Computes embeddings of consecutive sentences and splits the text when the semantic difference (cosine distance) between sentence $S_i$ and $S_{i+1}$ exceeds a statistical threshold (e.g., 95th percentile of variance).

---

### B. Vector Indexing Algorithms (HNSW vs. IVF-PQ)
Searching raw vectors linearly ($O(N)$) across millions of documents is too slow for real-time APIs. Vector DBs use Approximate Nearest Neighbor (ANN) index structures.

#### 1. HNSW (Hierarchical Navigable Small World)
* **Mechanism:** Builds a multi-layer graph where the top layers have long-range connections (like express highways) and the bottom layers have short-range connections (local roads).
* **Search:** Starts at the top layer, performs a greedy search to find the local exit point, drops down a layer, and repeats.
* **Trade-off:** Fast search speeds ($O(\log N)$) and high accuracy, but consumes massive memory because the index graph must reside entirely in RAM.

```
Layer 2 (Highway)    [Node A] ───────────────────────────────> [Node E]
                        │                                         │
Layer 1 (State Road)  [Node A] ──────────> [Node C] ───────────> [Node E]
                        │                   │                     │
Layer 0 (Local Road)  [Node A] -> [Node B] -> [Node C] -> [Node D] -> [Node E]
```

#### 2. IVF-PQ (Inverted File with Product Quantization)
* **Mechanism:** 
  1. **IVF (Inverted File):** Clusters the vector space into $C$ centroids using K-means. The search only inspects vectors in the nearest centroid clusters.
  2. **PQ (Product Quantization):** Compresses high-dimensional vectors. It splits a 1024-dimension vector into 8 sub-vectors of 128 dimensions, assigns each sub-vector to its nearest centroid in a sub-space codebook, and stores them as an 8-byte array.
* **Trade-off:** Massive reduction in memory storage (up to $97\%$ compression), but suffers from reduced retrieval accuracy.

---

### C. Advanced RAG Retrieval Pipeline

To scale accuracy in production, simple vector matches must be augmented with query processing and post-retrieval steps:

```
User Query ──> Query Rewriter/Expansion ──> Retrieval (BM25 + Vector) ──> Reciprocal Rank Fusion ──> Cross-Encoder Reranker ──> Context
```

#### 1. Sparse vs. Dense Retrieval (Hybrid Search)
* **Sparse Retrieval (BM25):** Matches keyword terms exactly. Robust for technical terminology, serial numbers, and codes.
* **Dense Retrieval (Embeddings):** Matches conceptual meaning. Robust for synonyms and semantic intents.
* **Merging (RRF):** Combine rankings using **Reciprocal Rank Fusion (RRF)**:
  $$RRF(d) = \sum_{m \in M} \frac{1}{k + r_m(d)}$$
  where $r_m(d)$ is the rank of document $d$ in system $M$ (typically $k \approx 60$).

#### 2. Reranking (Cross-Encoder Models)
Bi-Encoder models (standard embedding search) embed queries and documents independently. They are fast but miss token-to-token cross-attention interactions.
* **Cross-Encoder Rerankers:** Pass the concatenated `[Query, Document]` string into a single model, calculating full cross-attention. This produces highly accurate relevance scores but is computationally expensive, so it is used to rerank only the top 10-25 candidates retrieved via fast Bi-Encoder vector search.

---

### D. RAG Bottlenecks & Solutions
1. **Lost in the Middle:**
   * *Problem:* LLMs pay the most attention to the absolute beginning and end of the prompt context. Crucial retrieved facts placed in the middle of long prompts are often ignored.
   * *Solution:* Limit context size, sort retrieved documents by relevance so the most critical are at the absolute borders, or use reranking models to prune useless chunks.
2. **Stale Vector Indexes:**
   * *Problem:* Real-time updates to databases are not reflected in the vector index due to slow batch embedding pipelines.
   * *Solution:* Use event-driven CDC (Change Data Capture) pipelines to trigger instant incremental indexing of new records.

---

## 4. Agentic Frameworks & State Machines

Agentic workflows transition control flow decisions from hardcoded logic to the LLM itself, enabling loop executions and planning.

### A. The ReAct (Reason + Action) Pattern
The core agentic loop combines reasoning and tool execution in steps:

```
User Prompt ──> [Thought] (Reasoning) ──> [Action] (Tool Choice) ──> [Observation] (Tool Output) ──> [Thought] ──> Final Answer
```

* **Execution Loop:**
  1. **Thought:** The agent reasons about what to do next based on the input history.
  2. **Action:** The agent decides to call a registered tool with parameters.
  3. **Observation:** The environment executes the tool and returns the output string.
  4. **Repeat:** The agent reads the tool output, updates its thought process, and either calls another tool or outputs the final response.

---

### B. State Graphs (LangGraph Model)
State Graphs define agents as stateful graphs containing:
* **State:** A mutable data structure passed to all nodes (e.g., a message history list or query metadata).
* **Nodes:** Code execution points. Can be LLM call wrappers or local functions (tools).
* **Edges:** Defines routing.
  * *Standard Edges:* Always route node $A \rightarrow B$.
  * *Conditional Edges:* A function reads the state and evaluates where to route next (e.g., if LLM indicates tool call, route to tool executor node, else route to termination node).
* **Checkpointers:** Automatically save state snapshots at every step. This supports **Human-in-the-Loop** pausing (awaiting human verification before resuming execution) and time-travel debugging.

---

## 5. Java Gen AI System Design (Spring AI & LangChain4j)

Developing AI applications in the JVM introduces unique architectural considerations regarding reflection, type safety, memory, and multi-tenant scaling.

### A. Dynamic Proxy Interception & Declarative Code
In LangChain4j, developers define interfaces annotated with AI metadata:

```java
public interface CustomerSupportAgent {
    @SystemMessage("You are a helpful travel assistant.")
    String chat(String message);
}
```

#### Under the Hood: JVM Execution
1. **Instantiation:** `AiServices.builder(Class)` creates an instance at runtime using JDK Dynamic Proxies:
   ```java
   CustomerSupportAgent agent = (CustomerSupportAgent) Proxy.newProxyInstance(
       classLoader,
       new Class<?>[] { CustomerSupportAgent.class },
       new InvocationHandler() { ... }
   );
   ```
2. **Invocation:** Calling `agent.chat(message)` redirects execution to the proxy's `InvocationHandler.invoke()` method:
   * **Reflection Metadata Scan:** The handler reads the interface method's annotations (like `@SystemMessage`).
   * **Payload Assembly:** It retrieves conversation history from the registered `ChatMemory` store, appends the new query, and compiles a list of `ChatMessage` objects.
   * **Execution:** It makes HTTP calls to the LLM provider via the configured `ChatLanguageModel`.
   * **Type Conversions:** If the return type is a Java record or enum, it intercepts the model's text output and uses JSON parsers (e.g., Jackson) to map it back to Java objects.

---

### B. Function Calling / Reflection Loop
When an LLM requests tool execution, the JVM maps and executes code dynamically:

```
1. Scan Class ──> Find @Tool ──> Generate JSON Schema Tool Specification
2. LLM response ──> ToolExecutionRequest (Name, JSON Args)
3. JVM Method Lookup ──> Deserialization ──> Method.invoke(instance, args)
4. Wrap output ──> ToolExecutionResultMessage ──> Return to LLM
```

* **Reflection Performance:** Inspecting annotations at runtime can introduce small overheads. Frameworks optimize this by parsing classes at startup, caching target `java.lang.reflect.Method` descriptors, and using pre-compiled `MethodHandle` calls to execute invocations.

---

### C. Stateful Memory & Concurrency Design
Spring Boot controllers are singletons by default. If multiple users query a single injected agent bean, **conversational memory will be corrupted and shared across users unless carefully isolated.**

#### 1. Session-Scoped Isolation (`@MemoryId`)
To isolate history in multi-tenant backends, register the `@MemoryId` parameter:
```java
public interface CustomerSupportAgent {
    String chat(@MemoryId String sessionId, @UserMessage String message);
}
```
* **Under the Hood:** LangChain4j uses the `sessionId` key to query the configured `ChatMemoryStore` (e.g., checking a database or a localized concurrent map) to fetch the message logs for that user session.

#### 2. Persistent ChatMemoryStore (Distributed Architecture)
Avoid storing history in JVM memory (`InMemoryChatMemory`). If the server restarts or scales horizontally, memory states are lost. Build a custom persistent store backed by a shared database (Redis, Postgres):

```java
@Component
public class RedisChatMemoryStore implements ChatMemoryStore {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisChatMemoryStore(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String key = "chat:session:" + memoryId;
        List<ChatMessage> history = (List<ChatMessage>) redisTemplate.opsForValue().get(key);
        return history != null ? history : new ArrayList<>();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String key = "chat:session:" + memoryId;
        redisTemplate.opsForValue().set(key, messages);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        String key = "chat:session:" + memoryId;
        redisTemplate.delete(key);
    }
}
```

---

## 6. Framework Comparison: LangChain4j vs. Spring AI

| Feature / Metric | LangChain4j | Spring AI |
|------------------|-------------|-----------|
| **Primary Integration** | Standalone Java wrapper modeled on LangChain. Extremely framework-agnostic. | Built strictly inside the Spring Boot ecosystem. Integrates with Spring Data, Boot starters. |
| **Agentic Proxy Support** | Excellent. High-level declarative `AiServices` hide execution complexities. | Uses `ChatClient` with fluent APIs. Agent logic is managed using intercepting `Advisors`. |
| **Model Auto-Configurations** | Manual bean configuration or simple property injection. | Native Spring Boot auto-configuration bindings matching application properties. |
| **Function Calling API** | Standard Java classes with simple `@Tool` annotations. | Uses Java `Function<Request, Response>` interface bindings configured via `@Bean` registries. |
| **Ecosystem Maturity** | Highly mature, wide array of document loaders and vector store integrations. | Rapidly expanding, supported by VMware core Spring development teams. |

---

## 7. Hands-on Project Modules (Directory Map)

To learn these concepts in practice, explore the following sub-projects:

*   **[AI Engineering Basics Module (basics)](basics/README.md):** Features from-scratch Python implementations of fundamental AI primitives: Simple & BPE (Byte-Pair Encoding) tokenizers, custom TF-IDF Vectorizer with smoothing and L2-normalization, Vector Similarity metrics (Cosine, Dot Product, Euclidean Distance), and a mini sparse search engine retriever.
*   **[Java Spring AI Module (gen-ai-spring-ai)](gen-ai-spring-ai/README.md):** Features Spring Boot configuration, ChatClient message memory advisors, and mock fallbacks on port 8081.
*   **[Java LangChain4j & Custom State Graph Module (gen-ai-langchain4j)](gen-ai-langchain4j/README.md):** Features LangChain4j dynamic proxies, custom reflection tool bindings, step-by-step RAG ingestion pipelines, and the custom JVM state graph chatbot on port 8082.
*   **[Python AI Module (LangGraph)](gen-ai-python/README.md):** Features a LangGraph cyclic state graph chatbot with memory checkpointers, tool routing, and streaming command-line execution interfaces.
