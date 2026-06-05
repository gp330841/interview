package com.interview.ai.langgraph;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/langgraph")
public class LangGraphController {

    private final LangGraphService langGraphService;

    public LangGraphController(LangGraphService langGraphService) {
        this.langGraphService = langGraphService;
    }

    /**
     * Endpoint to chat with the custom state-graph agent, passing a sessionId for memory/history.
     * http://localhost:8080/api/ai/langgraph/chat?sessionId=session-123&message=Is flight LH123 delayed?
     */
    @GetMapping("/chat")
    public String chat(@RequestParam("sessionId") String sessionId,
                       @RequestParam("message") String message) {
        return langGraphService.chat(sessionId, message);
    }
}
