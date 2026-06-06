package com.interview.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/spring")
public class SpringAiController {

    private final ChatClient springAiChatClient;

    public SpringAiController(ChatClient springAiChatClient) {
        this.springAiChatClient = springAiChatClient;
    }

    /**
     * Endpoint demonstrating Spring AI's fluent ChatClient with message memory advisors.
     * http://localhost:8081/api/ai/spring/chat?message=Hello
     */
    @GetMapping("/chat")
    public String springAiChat(@RequestParam("message") String message) {
        return springAiChatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
