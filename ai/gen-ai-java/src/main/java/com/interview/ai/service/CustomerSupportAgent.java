package com.interview.ai.service;

import dev.langchain4j.service.SystemMessage;

public interface CustomerSupportAgent {

    @SystemMessage({
        "You are a friendly customer support agent for a travel company.",
        "You have access to tools to check flight details and booking statuses.",
        "Always query the appropriate tool when asked about specific bookings or flights.",
        "Be polite, professional, and concise in your responses."
    })
    String answer(String userMessage);
}
