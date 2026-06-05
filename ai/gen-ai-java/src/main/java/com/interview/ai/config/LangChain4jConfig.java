package com.interview.ai.config;

import com.interview.ai.service.BookingTools;
import com.interview.ai.service.CustomerSupportAgent;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LangChain4jConfig {

    @Value("${openai.api-key:}")
    private String apiKey;

    @Bean
    public ChatLanguageModel langChain4jChatModel() {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("demo")) {
            System.out.println("WARNING: OPENAI_API_KEY is not configured. Loading mock ChatLanguageModel for LangChain4j.");
            return new ChatLanguageModel() {
                @Override
                public Response<AiMessage> generate(List<ChatMessage> messages) {
                    ChatMessage lastMessage = messages.get(messages.size() - 1);
                    String text = lastMessage.text();
                    String reply;
                    if (text.toLowerCase().contains("lh123")) {
                        reply = "[MOCK RESPONSE] Flight LH123 is currently DELAYED. Departure is expected in 2 hours.";
                    } else {
                        reply = "[MOCK RESPONSE] Hello! I am a simulated Customer Support Agent. Please set a valid 'OPENAI_API_KEY' in application.yml or environment variables to connect to OpenAI.";
                    }
                    return Response.from(AiMessage.from(reply));
                }

                @Override
                public Response<AiMessage> generate(List<ChatMessage> messages, List<dev.langchain4j.agent.tool.ToolSpecification> toolSpecifications) {
                    // Fallback to simple generate if tool calling is triggered in mock mode
                    return generate(messages);
                }
            };
        }

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .temperature(0.0)
                .build();
    }

    @Bean
    public ChatMemory chatMemory() {
        // Keeps track of conversation history in memory, limiting it to a window of tokens
        return TokenWindowChatMemory.withMaxTokens(1000, new OpenAiTokenizer("gpt-4o-mini"));
    }

    @Bean
    public CustomerSupportAgent customerSupportAgent(ChatLanguageModel chatLanguageModel, ChatMemory chatMemory, BookingTools bookingTools) {
        // High-level declarative service bridging the LLM model, the memory window, and tools
        return AiServices.builder(CustomerSupportAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemory(chatMemory)
                .tools(bookingTools)
                .build();
    }
}
