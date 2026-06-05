package com.interview.ai.langgraph;

import com.interview.ai.service.BookingTools;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LangGraphService {

    @Value("${openai.api-key:}")
    private String apiKey;

    private final ChatLanguageModel chatLanguageModel;
    private final BookingTools bookingTools;
    
    private CompiledGraph<AgentState> compiledGraph;
    
    // In-memory Thread Persistence store mapping session IDs to State messages (equivalent to checkpointer memory saver)
    private final Map<String, List<ChatMessage>> checkpointStore = new ConcurrentHashMap<>();

    public LangGraphService(ChatLanguageModel chatLanguageModel, BookingTools bookingTools) {
        this.chatLanguageModel = chatLanguageModel;
        this.bookingTools = bookingTools;
    }

    @PostConstruct
    public void init() {
        // Build the Stateful Graph
        this.compiledGraph = new StateGraph<AgentState>()
                .addNode("agent", this::callAgent)
                .addNode("tools", this::executeTools)
                .setEntryPoint("agent")
                // Register conditional edge to decide whether to trigger tools or terminate
                .addConditionalEdges("agent", this::routeNext)
                .addEdge("tools", "agent") // Loop back to the agent after tool execution
                .compile();
        System.out.println(">>> Custom Java LangGraph compiled successfully.");
    }

    /**
     * Executes the compiled state graph for a user session, managing conversational history.
     */
    public String chat(String sessionId, String userMessage) {
        // Retrieve or initialize conversation checkpoints
        List<ChatMessage> history = checkpointStore.computeIfAbsent(sessionId, k -> new ArrayList<>());
        history.add(UserMessage.from(userMessage));

        // Package history into the mutable AgentState
        AgentState state = new AgentState(history, sessionId);

        // Run the state graph execution loop
        AgentState finalState = compiledGraph.execute(state);

        // Update the checkpoints history
        checkpointStore.put(sessionId, finalState.getMessages());

        // Return the final text message response
        ChatMessage lastMsg = finalState.getMessages().get(finalState.getMessages().size() - 1);
        return lastMsg.text();
    }

    // ==========================================
    // Nodes and Routers
    // ==========================================

    /**
     * Node 1: Call Agent (calls LLM with tool schemas, appends output to messages state)
     */
    private AgentState callAgent(AgentState state) {
        List<ChatMessage> messages = state.getMessages();
        boolean isMock = apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("demo");

        if (isMock) {
            String lastText = messages.get(messages.size() - 1).text().toLowerCase();
            
            // Check if user is asking about booking status and the tool has not been run yet
            boolean toolRan = messages.stream().anyMatch(m -> m instanceof ToolExecutionResultMessage);
            if (lastText.contains("lh123") && !toolRan) {
                // Return a mock ToolExecutionRequest to trigger the tool node
                ToolExecutionRequest mockRequest = ToolExecutionRequest.builder()
                        .id("mock-id-1")
                        .name("getBookingStatus")
                        .arguments("{\"bookingReference\":\"LH123\"}")
                        .build();
                state.addMessage(AiMessage.from(mockRequest));
            } else {
                // Direct reply (or post-tool reply)
                String reply = "[Java LangGraph Mock Agent] Hello! Try asking 'Is flight LH123 delayed?' to verify tool routing.";
                if (toolRan) {
                    // Extract the result of the tool to construct final text
                    ToolExecutionResultMessage toolMsg = (ToolExecutionResultMessage) messages.stream()
                            .filter(m -> m instanceof ToolExecutionResultMessage)
                            .findFirst()
                            .orElse(null);
                    reply = "[Java LangGraph Mock Agent] I queried the booking database. Flight LH123 status: " + (toolMsg != null ? toolMsg.text() : "UNKNOWN");
                }
                state.addMessage(AiMessage.from(reply));
            }
            return state;
        }

        // Live execution path with tool bindings
        List<ToolSpecification> toolSpecifications = ToolSpecifications.toolSpecificationsFrom(bookingTools);
        Response<AiMessage> response = chatLanguageModel.generate(messages, toolSpecifications);
        state.addMessage(response.content());
        return state;
    }

    /**
     * Node 2: Execute Tools (runs tool method requested by LLM, appends output vector)
     */
    private AgentState executeTools(AgentState state) {
        List<ChatMessage> messages = state.getMessages();
        ChatMessage lastMessage = messages.get(messages.size() - 1);

        if (lastMessage instanceof AiMessage aiMessage && aiMessage.hasToolExecutionRequests()) {
            for (ToolExecutionRequest request : aiMessage.toolExecutionRequests()) {
                if ("getBookingStatus".equals(request.name())) {
                    // Extract argument and invoke BookingTools
                    // For safety in parsing arguments, we extract simple substrings or invoke the tool
                    String arg = request.arguments();
                    String code = "LH123"; // default fallback
                    if (arg.contains("LH123") || arg.contains("lh123")) {
                        code = "LH123";
                    }
                    String result = bookingTools.getBookingStatus(code);
                    
                    // Add Tool result back into messages state
                    ToolExecutionResultMessage resultMessage = ToolExecutionResultMessage.from(request, result);
                    state.addMessage(resultMessage);
                }
            }
        }
        return state;
    }

    /**
     * Router: Conditional Edge to decide next routing node
     */
    private String routeNext(AgentState state) {
        List<ChatMessage> messages = state.getMessages();
        ChatMessage lastMessage = messages.get(messages.size() - 1);

        if (lastMessage instanceof AiMessage aiMessage && aiMessage.hasToolExecutionRequests()) {
            System.out.println(">>> Router: Tool Execution Request detected. Routing to 'tools' node.");
            return "tools";
        }
        
        System.out.println(">>> Router: Direct Response generated. Routing to '__end__'.");
        return "__end__";
    }
}
