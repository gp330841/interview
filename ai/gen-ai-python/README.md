# Python Generative AI Module (LangGraph & LangSmith)

This module demonstrates how to build stateful multi-agent applications using **LangGraph** (LangChain's graph orchestration library) and track execution flows via **LangSmith**.

---

## 🛠️ Key Technologies Demonstrated

### 1. LangGraph
* **Framework Philosophy:** Models agentic workflows as circular, stateful graphs. Perfect for flows that need loops (e.g., trying a tool, seeing the output, correcting, and querying again).
* **Key Components:**
  * **State (`MessagesState`):** A shared graph schema that holds the state context (conversation history). Nodes receive this state, modify it, and return the modified state.
  * **Nodes (`agent`, `tools`):** Compute steps. `agent` calls the LLM, and `tools` executes helper functions.
  * **Conditional Edges:** Logic that routes state dynamically. Using `tools_condition` routes to the tool node if the LLM calls a tool, or exits if a direct response is generated.
  * **Checkpointer (`MemorySaver`):** A thread-safe, local memory saver that stores state snapshots at every node transition, allowing thread retrieval for multi-turn chats.

### 2. LangSmith Tracing & Observability
Captures input/output execution flows, tool execution latencies, and token allocations out of the box.

---

## 📂 Code Map

* [chatbot.py](chatbot.py): Assembles the `StateGraph` model, conditional edges, and memory checkpointers.
* [main.py](main.py): Initiates the compiled graph, displays the flow diagram, and starts an interactive streaming CLI loop.
* [.env.example](.env.example): Environment variable template for OpenAI and LangSmith keys.

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

Try asking questions like:
* *"Hi, my name is Yogeshwar!"*
* *"What is my name?"* (Verifies memory saver checkpointer)
* *"Can you check order ORD123?"* (Verifies agent routes to tool node, executes python function, and responds with the return data)
