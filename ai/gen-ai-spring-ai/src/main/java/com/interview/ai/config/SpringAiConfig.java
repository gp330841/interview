package com.interview.ai.config;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.List;

@Configuration
public class SpringAiConfig {

    @Bean
    public ChatClient springAiChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("You are a helpful Spring AI assistant.")
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

    public static class MockModelCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String key = context.getEnvironment().getProperty("spring.ai.openai.api-key");
            return key == null || key.trim().isEmpty() || "demo".equals(key.trim());
        }
    }

    @Bean
    @Conditional(MockModelCondition.class)
    @Primary
    public ChatModel mockChatModel() {
        System.out.println("WARNING: spring.ai.openai.api-key is not configured. Loading mock ChatModel for Spring AI.");
        return new ChatModel() {
            @Override
            public ChatResponse call(Prompt prompt) {
                String userQuery = prompt.getInstructions().get(prompt.getInstructions().size() - 1).getText();
                String responseText = "[MOCK SPRING AI RESPONSE] Hello! I am a simulated Spring AI ChatClient. " +
                        "Your query was: \"" + userQuery + "\". " +
                        "Please configure 'spring.ai.openai.api-key' for live OpenAI queries.";
                AssistantMessage assistantMessage = new AssistantMessage(responseText);
                Generation generation = new Generation(assistantMessage);
                return new ChatResponse(List.of(generation));
            }
        };
    }
}
