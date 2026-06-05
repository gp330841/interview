import os
from typing import Literal
from dotenv import load_dotenv

from langchain_core.messages import AIMessage, HumanMessage
from langchain_core.tools import tool
from langchain_openai import ChatOpenAI
from langgraph.graph import StateGraph, MessagesState
from langgraph.prebuilt import ToolNode, tools_condition
from langgraph.checkpoint.memory import MemorySaver

load_dotenv()

# ==========================================
# 1. Tool Definitions (Function Calling)
# ==========================================

@tool
def get_order_status(order_id: str) -> str:
    """Retrieves the shipment and delivery status of a customer order given an order ID (e.g. ORD123, ORD456)."""
    print(f"\n>>> LangGraph executing tool: get_order_status for Order: {order_id}")
    orders = {
        "ORD123": "IN TRANSIT. Currently at sorting facility in New York. Expected delivery: Tomorrow by 4 PM.",
        "ORD456": "PROCESSING. Payment received. Preparing for shipment.",
        "ORD789": "DELIVERED. Signed by resident at front desk on June 1st."
    }
    return orders.get(order_id.upper().strip(), "ORDER NOT FOUND. Please verify your order ID.")

tools = [get_order_status]
tool_node = ToolNode(tools)

# ==========================================
# 2. Node Implementations
# ==========================================

def call_model(state: MessagesState):
    """Invokes the LLM to decide on response or tool execution. Includes fallback mock logic."""
    messages = state["messages"]
    
    # Fallback to Mock response if OpenAI API key is missing or dummy
    api_key = os.environ.get("OPENAI_API_KEY", "")
    if not api_key or api_key.strip() == "" or api_key.strip() == "demo":
        last_message = messages[-1].content.lower()
        if "ord" in last_message:
            # Simulate tool usage feedback in mock mode
            reply = "[MOCK LANGGRAPH AGENT] I noticed you asked about an order. Flight/Order ORD123 is currently IN TRANSIT. Expected delivery: Tomorrow."
        else:
            reply = "[MOCK LANGGRAPH AGENT] Hello! I am a simulated LangGraph agent. To get real outputs, configure a valid 'OPENAI_API_KEY' in your .env file."
        return {"messages": [AIMessage(content=reply)]}
    
    # Normal execution using LangChain ChatOpenAI
    model = ChatOpenAI(model="gpt-4o-mini", temperature=0.0)
    model_with_tools = model.bind_tools(tools)
    response = model_with_tools.invoke(messages)
    
    return {"messages": [response]}

# ==========================================
# 3. State Graph Assembly
# ==========================================

workflow = StateGraph(MessagesState)

# Register compute nodes
workflow.add_node("agent", call_model)
workflow.add_node("tools", tool_node)

# Set starting point
workflow.set_entry_point("agent")

# Add conditional edge from agent.
# If agent decided to call tools, route to 'tools'. If not, route to end of graph.
workflow.add_conditional_edges(
    "agent",
    tools_condition,  # Checks if last message contains tool_calls
)

# Route execution back to the agent after tool output is added to the messages state
workflow.add_edge("tools", "agent")

# Memory Saver Checkpointer handles multi-turn thread-safe context preservation
checkpointer = MemorySaver()

# Compile the workflow
graph = workflow.compile(checkpointer=checkpointer)
