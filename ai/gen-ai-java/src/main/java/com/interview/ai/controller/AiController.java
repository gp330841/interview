package com.interview.ai.controller;

import com.interview.ai.rag.SimpleRagService;
import com.interview.ai.service.CustomerSupportAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final ChatClient springAiChatClient;
    private final CustomerSupportAgent customerSupportAgent;
    private final SimpleRagService ragService;

    public AiController(ChatClient springAiChatClient, 
                        CustomerSupportAgent customerSupportAgent, 
                        SimpleRagService ragService) {
        this.springAiChatClient = springAiChatClient;
        this.customerSupportAgent = customerSupportAgent;
        this.ragService = ragService;
    }

    /**
     * Endpoint demonstrating Spring AI's fluent ChatClient with message memory advisors.
     * http://localhost:8080/api/ai/spring/chat?message=Hello
     */
    @GetMapping("/spring/chat")
    public String springAiChat(@RequestParam("message") String message) {
        return springAiChatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * Endpoint demonstrating LangChain4j Agent with dynamic tool/function calling.
     * http://localhost:8080/api/ai/langchain/agent?message=Is LH123 delayed?
     */
    @GetMapping("/langchain/agent")
    public String langChain4jAgent(@RequestParam("message") String message) {
        // Under the hood, LangChain4j invokes the model, detects if it requests a tool,
        // runs getBookingStatus in BookingTools, and feeds the output back into the conversation.
        return customerSupportAgent.answer(message);
    }

    /**
     * Endpoint demonstrating local vector-store search and grounded RAG query generation.
     * http://localhost:8080/api/ai/rag/query?question=What is the return policy?
     */
    @GetMapping("/rag/query")
    public String ragQuery(@RequestParam("question") String question) {
        return ragService.query(question);
    }
}
