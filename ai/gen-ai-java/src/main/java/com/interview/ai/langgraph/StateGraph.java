package com.interview.ai.langgraph;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StateGraph<T> {
    
    private final Map<String, Node<T>> nodes = new HashMap<>();
    private final Map<String, String> edges = new HashMap<>();
    private final Map<String, ConditionalEdge<T>> conditionalEdges = new HashMap<>();
    private String entryPoint;

    @FunctionalInterface
    public interface Node<S> extends Function<S, S> {}

    @FunctionalInterface
    public interface ConditionalEdge<S> extends Function<S, String> {}

    public StateGraph<T> addNode(String name, Node<T> node) {
        nodes.put(name, node);
        return this;
    }

    public StateGraph<T> setEntryPoint(String name) {
        this.entryPoint = name;
        return this;
    }

    public StateGraph<T> addEdge(String from, String to) {
        edges.put(from, to);
        return this;
    }

    public StateGraph<T> addConditionalEdges(String from, ConditionalEdge<T> condition) {
        conditionalEdges.put(from, condition);
        return this;
    }

    public CompiledGraph<T> compile() {
        if (entryPoint == null) {
            throw new IllegalStateException("Entry point must be set before compiling.");
        }
        return new CompiledGraph<>(nodes, edges, conditionalEdges, entryPoint);
    }
}
