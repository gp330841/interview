# Python Generative AI Module (LangGraph & LangSmith)

This module demonstrates how to build stateful, cyclic multi-agent applications using **LangGraph** (LangChain's graph orchestration library) and trace execution flows natively in **LangSmith**.

---

## 1. LangGraph Architecture & State Reducer Mechanics

LangGraph models agentic workflows as stateful graphs. Unlike simple linear pipelines, graphs allow execution to loop back (e.g., executing a tool, reviewing the output, and repeating if necessary).

```
                 State (Dict / MessagesState)
                              │
               ┌──────────────▼──────────────┐
               │         agent Node          │ (LLM decides next step)
               └──────────────┬──────────────┘
                              │
              ┌───────────────▼───────────────┐
              │  Conditional Edge (Router)    │
              └───────┬───────────────┬───────┘
                      │               │
             (Tool Calls Present) (No Tool Calls)
                      │               │
             ┌────────▼────────┐      └───────────────┐
             │   tools Node    │                      │
             └────────┬────────┘                      │
                      │                               ▼
            (Loop back to agent)               [__end__ / Output]
```

### Core Stateful Design Patterns
1. **Shared Graph State:**
   Every node receives the current state object, performs operations, and returns a dictionary of updates. LangGraph merges these updates into the shared state.
2. **Message Reducers (`add_messages`):**
   In standard dictionaries, returning a key overwrites its previous value. However, `MessagesState` uses an annotative **Reducer** (the `add_messages` function).
   * **Mechanism:** When a node returns `{"messages": [new_message]}`, LangGraph does not replace the history list. Instead, the reducer appends the new message to the list. If the new message contains an ID matching an existing message, it updates the existing message in place (crucial for tool execution responses).
3. **Checkpointers & Persistence (`MemorySaver`):**
   The `MemorySaver` checkpointer automatically saves snapshots of the graph state at every node transition.
   * **Threading & Multi-Turn Conversations:** By passing a unique `thread_id` in the config, LangGraph fetches the exact historical state checkpoint, allowing stateless servers to host concurrent, stateful chat sessions.

---

## 2. Observability & LangSmith Tracing

By defining tracing variables in your environment, LangChain's callback system automatically instruments execution tracing.

* **Captured Spans:**
  * **LLM Spans:** Tracks prompts, generated tokens, model name, temperature, and exact API completion latencies.
  * **Tool Spans:** Tracks the inputs, outputs, and runtime durations of custom python `@tool` functions.
  * **Graph Node Spans:** Visualizes the exact execution path taken through the graph (e.g. `agent` $\rightarrow$ `tools` $\rightarrow$ `agent` $\rightarrow$ `__end__`).

---

## 3. Code Map

* [chatbot.py](chatbot.py): Assembles the `StateGraph` model, node functions, tool bindings, and checkpointer.
* [main.py](main.py): Initiates the compiled graph, displays the flow diagram, and runs the streaming CLI chat loop.
* [.env.example](.env.example): Template for OpenAI and LangSmith keys.

---

## 🚀 Running the Project

### Step 1: Initialize Virtual Environment & Install Dependencies
Run these commands from the `ai/gen-ai-python/` directory:

```bash
# Create virtual environment
python3 -m venv venv

# Activate virtual environment
source venv/bin/activate

# Install required dependencies
pip install -r requirements.txt
```

### Step 2: Configure Environment
Copy the example file to `.env`:
```bash
cp .env.example .env
```
Open `.env` and fill in your keys:
```
OPENAI_API_KEY="your-openai-api-key"
LANGCHAIN_TRACING_V2="true"
LANGCHAIN_API_KEY="your-langsmith-api-key"
```

*Note: If no keys are configured, the app runs in fallback Mock mode.*

### Step 3: Run the CLI Chatbot
```bash
python3 main.py
```

---

## 📡 CLI Interactive Testing

When running `main.py`, you can test state and tool routing directly:

1. **Test Tool Routing:**
   * *Query:* `Can you check order ORD123?`
   * *Verification:* The log will stream the node execution: `agent` detects the query, triggers a tool call, routes to the `tools` node (running `get_order_status` tool), loops back to `agent`, and outputs the final response.
2. **Test Conversation Memory:**
   * *Query:* `Hi, my name is Yogeshwar`
   * *Follow-up:* `What is my name?`
   * *Verification:* The agent reads the conversation state checkpoint retrieved by `thread_id` and correctly answers `Yogeshwar`.
