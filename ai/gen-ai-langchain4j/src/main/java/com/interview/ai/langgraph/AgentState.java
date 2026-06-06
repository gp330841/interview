package com.interview.ai.langgraph;

import dev.langchain4j.data.message.ChatMessage;
import java.util.ArrayList;
import java.util.List;

public class AgentState {
    private final List<ChatMessage> messages;
    private String threadId;

    public AgentState(List<ChatMessage> messages, String threadId) {
        this.messages = new ArrayList<>(messages);
        this.threadId = threadId;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
}
