package springboot.interview.microservices;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;

/**
 * Spring Boot - Microservices Interview Questions
 * Contains practical code examples for OpenFeign, Circuit Breakers, and Fallbacks.
 */
public class MicroservicesQA {

    // Dummy DTO
    public static class Order {
        private String id;
        public Order(String id) { this.id = id; }
        public String getId() { return id; }
    }

    // Q14: OpenFeign Client [Medium]
    // Declarative REST client that automatically integrates with Eureka for load balancing
    @FeignClient(name = "inventory-service", fallback = InventoryServiceFallback.class)
    public interface InventoryClient {

        @GetMapping("/api/inventory/{productId}")
        Integer getStockLevel(@PathVariable("productId") String productId);
    }

    // Fallback logic if inventory-service is completely down
    public static class InventoryServiceFallback implements InventoryClient {
        @Override
        public Integer getStockLevel(String productId) {
            System.err.println("Inventory Service is down! Returning default stock.");
            return 0; // Safe default
        }
    }

    @Service
    public static class CheckoutService {

        // Q8 & Q9: Resilience4j Circuit Breaker and Retry [Easy/Medium]
        public void q8_q9() {
            /*
             * Answer: CircuitBreaker prevents cascading failures by stopping calls 
             * to a broken service. Retry automatically attempts the call a few times before failing.
             */
        }
        
        // If the payment service fails 50% of the time, the circuit opens.
        // It will automatically route to 'paymentFallback' instead of making the HTTP call.
        @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
        @Retry(name = "paymentService") // Will retry the call 3 times (configured in properties) before tripping
        public String processPayment(String orderId) {
            System.out.println("Attempting to call external Payment Gateway for order: " + orderId);
            
            // Simulate a failure in the external service
            boolean externalServiceIsDown = true;
            if (externalServiceIsDown) {
                throw new RuntimeException("Payment Gateway Timeout!");
            }
            
            return "Payment Successful";
        }

        // The fallback method signature MUST match the original method (plus an exception parameter)
        public String paymentFallback(String orderId, Throwable ex) {
            System.err.println("Circuit Breaker Tripped! Exception: " + ex.getMessage());
            return "Payment is currently unavailable. We will process your order later.";
        }
    }

    // Q20: API Composition (BFF) Pattern [Medium]
    @Service
    public static class DashboardComposerService {

        private final InventoryClient inventoryClient;
        // Assume we have other clients like UserClient, RecommendationClient

        public DashboardComposerService(InventoryClient inventoryClient) {
            this.inventoryClient = inventoryClient;
        }

        public DashboardData getDashboard(String userId, String productId) {
            // Makes multiple internal microservice calls and aggregates them into one DTO for the UI
            
            // Call 1
            Integer stock = inventoryClient.getStockLevel(productId);
            // Call 2
            // UserInfo user = userClient.getUser(userId);
            
            return new DashboardData("John Doe", stock);
        }
    }

    // Aggregated DTO
    public static class DashboardData {
        public String userName;
        public Integer stockLevel;
        public DashboardData(String userName, Integer stockLevel) {
            this.userName = userName;
            this.stockLevel = stockLevel;
        }
    }
}
