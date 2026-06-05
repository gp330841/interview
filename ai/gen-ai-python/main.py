import os
import sys
from dotenv import load_dotenv
from chatbot import graph
from langchain_core.messages import HumanMessage

load_dotenv()

def print_graph_ascii():
    """Prints the state graph representation to understand how nodes connect."""
    print("\n==============================================")
    print("      LANGGRAPH AGENT FLOW CHART (RE-ACT)")
    print("==============================================")
    print("  [Start] --> Node: agent")
    print("               |")
    print("               +---> (Conditional Check: Should Call Tool?)")
    print("                           |")
    print("                           +--> Yes --> Node: tools --> Node: agent")
    print("                           |")
    print("                           +--> No  --> [End/Answer User]")
    print("==============================================")

def main():
    print_graph_ascii()
    
    # Configure LangSmith Tracing check
    tracing = os.environ.get("LANGCHAIN_TRACING_V2", "false")
    project = os.environ.get("LANGCHAIN_PROJECT", "default")
    api_key = os.environ.get("OPENAI_API_KEY", "")
    
    print("\n--- System Configuration ---")
    print(f"OPENAI_API_KEY: {'CONFIGURED' if (api_key and api_key != 'demo') else 'NOT CONFIGURED (Running Mock Mode)'}")
    print(f"LANGSMITH TRACING: {'ENABLED' if tracing.lower() == 'true' else 'DISABLED'}")
    if tracing.lower() == "true":
        print(f"LANGSMITH PROJECT: {project}")
    print("----------------------------\n")
    
    print("LangGraph Agent Ready! Type 'exit' or 'quit' to end the chat.")
    print("Try asking: 'What is the status of my order ORD123?'")
    
    # Config object containing the thread ID to keep track of conversation state
    config = {"configurable": {"thread_id": "learning-thread-1"}}
    
    while True:
        try:
            user_input = input("\nUser > ")
            if user_input.strip().lower() in ["exit", "quit", "q"]:
                print("Exiting. Happy learning!")
                break
                
            if not user_input.strip():
                continue
            
            # Start streaming the graph execution
            # graph.stream returns a generator yielding dicts representing active nodes and updates
            events = graph.stream(
                {"messages": [HumanMessage(content=user_input)]},
                config,
                stream_mode="values"
            )
            
            print("\n--- LangGraph Execution Stream ---")
            last_message = None
            for event in events:
                if "messages" in event:
                    last_message = event["messages"][-1]
                    # Print the class name of the message and a snippet of content
                    sender = "User" if last_message.__class__.__name__ == "HumanMessage" else "Agent"
                    print(f"[{sender}] {last_message.content}")
            print("----------------------------------")
            
        except KeyboardInterrupt:
            print("\nExiting.")
            sys.exit(0)
        except Exception as e:
            print(f"\nAn error occurred: {e}")

if __name__ == "__main__":
    main()
