package com.interview.ai.controller;

import com.interview.ai.rag.SimpleRagService;
import com.interview.ai.service.CustomerSupportAgent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class LangChain4jController {

    private final CustomerSupportAgent customerSupportAgent;
    private final SimpleRagService ragService;

    public LangChain4jController(CustomerSupportAgent customerSupportAgent, SimpleRagService ragService) {
        this.customerSupportAgent = customerSupportAgent;
        this.ragService = ragService;
    }

    /**
     * Endpoint demonstrating LangChain4j Agent with dynamic tool/function calling.
     * http://localhost:8082/api/ai/langchain/agent?message=Is booking LH123 delayed?
     */
    @GetMapping("/langchain/agent")
    public String langChain4jAgent(@RequestParam("message") String message) {
        return customerSupportAgent.answer(message);
    }

    /**
     * Endpoint demonstrating local vector-store search and grounded RAG query generation.
     * http://localhost:8082/api/ai/rag/query?question=What is the return policy?
     */
    @GetMapping("/rag/query")
    public String ragQuery(@RequestParam("question") String question) {
        return ragService.query(question);
    }
}
