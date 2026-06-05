package com.interview.ai.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class SimpleRagService {

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("classpath:data/faq.txt")
    private Resource faqResource;

    private final ChatLanguageModel chatLanguageModel;
    
    private EmbeddingModel embeddingModel;
    private EmbeddingStore<TextSegment> embeddingStore;

    public SimpleRagService(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    @PostConstruct
    public void init() throws IOException {
        // 1. Initialize In-Memory Vector Store and Embedding Model
        boolean isMock = apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("demo");
        
        if (isMock) {
            System.out.println("WARNING: Loading Mock EmbeddingModel for RAG initialization.");
            this.embeddingModel = new EmbeddingModel() {
                @Override
                public Response<Embedding> embed(String text) {
                    return Response.from(Embedding.from(new float[]{0.1f, 0.2f, 0.3f}));
                }

                @Override
                public Response<Embedding> embed(TextSegment textSegment) {
                    return embed(textSegment.text());
                }

                @Override
                public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
                    List<Embedding> embeddings = textSegments.stream()
                            .map(ts -> embed(ts).content())
                            .toList();
                    return Response.from(embeddings);
                }
            };
        } else {
            this.embeddingModel = OpenAiEmbeddingModel.builder()
                    .apiKey(apiKey)
                    .modelName("text-embedding-3-small")
                    .build();
        }
        
        this.embeddingStore = new InMemoryEmbeddingStore<>();

        // 2. Load the Knowledge Base Document
        String text = new String(faqResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Document document = Document.from(text);

        // 3. Chunk the Document into smaller segments (150 chars overlap of 30)
        DocumentSplitter splitter = DocumentSplitters.recursive(150, 30);
        List<TextSegment> segments = splitter.split(document);

        // 4. Generate Embeddings and Store Chunks in the Vector Database
        for (TextSegment segment : segments) {
            Embedding embedding = embeddingModel.embed(segment).content();
            embeddingStore.add(embedding, segment);
        }
        System.out.println(">>> RAG Pipeline Ingestion Complete. Ingested " + segments.size() + " document chunks.");
    }

    /**
     * Executes RAG: retrieves matching context vectors, builds custom prompt context, and queries the LLM.
     */
    public String query(String question) {
        boolean isMock = apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("demo");
        
        String contextText;

        if (isMock) {
            // Mock matching based on simple text lookup to guarantee functional response out of the box
            contextText = retrieveMockContext(question);
        } else {
            // A. Embed the incoming user question
            Embedding questionEmbedding = embeddingModel.embed(question).content();

            // B. Find Top-2 nearest neighbor vector matches in the database
            List<EmbeddingMatch<TextSegment>> relevantMatches = embeddingStore.findRelevant(questionEmbedding, 2);

            // C. Combine matches into a single context string
            StringBuilder contextBuilder = new StringBuilder();
            for (EmbeddingMatch<TextSegment> match : relevantMatches) {
                contextBuilder.append(match.embedded().text()).append("\n\n");
            }
            contextText = contextBuilder.toString();
        }

        // D. Build the final context-grounded Prompt
        String prompt = "You are a customer support representative. Answer the user's question based strictly on the provided context. " +
                "If the answer is not found in the context, politely say that you do not know.\n\n" +
                "=== CONTEXT ===\n" +
                contextText +
                "===============\n\n" +
                "QUESTION: " + question + "\n" +
                "ANSWER:";

        // E. Generate response from the LLM model
        return chatLanguageModel.generate(prompt);
    }

    private String retrieveMockContext(String question) {
        String q = question.toLowerCase();
        if (q.contains("return") || q.contains("policy")) {
            return "Our standard return policy allows customers to return any unused, unopened products within 30 days of the purchase date. To complete a return, you must present the original receipt.";
        } else if (q.contains("refund") || q.contains("time") || q.contains("process")) {
            return "Once a return is received and inspected at our warehouse, it takes between 5 to 7 business days to process your refund. Banks may take an additional 2 to 3 days to post.";
        } else if (q.contains("shipping") || q.contains("fee") || q.contains("cost")) {
            return "Standard shipping is free for all orders above $50. For orders under $50, a flat shipping fee of $5.99 is applied. Express shipping is $14.99.";
        } else {
            return "Contact support: reach our customer support team by emailing support@travelcompany.com or by calling 1-800-555-0199 between 9 AM and 5 PM EST, Monday through Friday.";
        }
    }
}
