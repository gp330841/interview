package com.interview.ai.langgraph;

import java.util.Map;

public class CompiledGraph<T> {
    
    private final Map<String, StateGraph.Node<T>> nodes;
    private final Map<String, String> edges;
    private final Map<String, StateGraph.ConditionalEdge<T>> conditionalEdges;
    private final String entryPoint;

    public CompiledGraph(Map<String, StateGraph.Node<T>> nodes,
                         Map<String, String> edges,
                         Map<String, StateGraph.ConditionalEdge<T>> conditionalEdges,
                         String entryPoint) {
        this.nodes = nodes;
        this.edges = edges;
        this.conditionalEdges = conditionalEdges;
        this.entryPoint = entryPoint;
    }

    /**
     * Loops through nodes in the state graph, modifying the state at each step,
     * until the termination state ("__end__") is hit.
     */
    public T execute(T initialState) {
        String currentNode = entryPoint;
        T state = initialState;

        while (currentNode != null && !currentNode.equals("__end__")) {
            System.out.println(">>> [Java StateGraph] Executing Node: [" + currentNode + "]");
            
            StateGraph.Node<T> node = nodes.get(currentNode);
            if (node == null) {
                throw new IllegalStateException("Node not registered: " + currentNode);
            }
            
            // Execute compute step on current node
            state = node.apply(state);

            // Determine next node: check conditional edges first, then fall back to standard edges
            if (conditionalEdges.containsKey(currentNode)) {
                currentNode = conditionalEdges.get(currentNode).apply(state);
            } else {
                currentNode = edges.get(currentNode);
            }
        }
        
        System.out.println(">>> [Java StateGraph] Execution terminated. Output state ready.");
        return state;
    }
}
