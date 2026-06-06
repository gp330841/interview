package com.interview.ai.service;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BookingTools {

    private final Map<String, String> bookingStore = new ConcurrentHashMap<>();

    public BookingTools() {
        bookingStore.put("LH123", "DELAYED (expected delay: 2 hours due to weather)");
        bookingStore.put("UA456", "ON TIME (boarding starts at Gate B2)");
        bookingStore.put("EK789", "CANCELLED (re-routing passengers to next flight)");
    }

    @Tool("Retrieves the current flight status given a booking reference code (e.g., LH123, UA456, EK789)")
    public String getBookingStatus(String bookingReference) {
        System.out.println(">>> Tool execution: getBookingStatus triggered for Reference: " + bookingReference);
        if (bookingReference == null || bookingReference.trim().isEmpty()) {
            return "Invalid booking code provided.";
        }
        return bookingStore.getOrDefault(bookingReference.toUpperCase().trim(), "UNKNOWN (Booking not found)");
    }
}
