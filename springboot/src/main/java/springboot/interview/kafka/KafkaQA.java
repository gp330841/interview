package springboot.interview.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Spring Boot - Apache Kafka Integration Interview Questions
 * Contains practical code examples for Producers, Consumers, Manual Acks, and DLTs.
 */
public class KafkaQA {

    // Dummy DTO
    public static class OrderEvent {
        private String orderId;
        private Double amount;
        
        public OrderEvent(String orderId, Double amount) { this.orderId = orderId; this.amount = amount; }
        public String getOrderId() { return orderId; }
        public Double getAmount() { return amount; }
    }

    @Service
    public static class KafkaProducerService {

        // Q6 & Q7: Using KafkaTemplate to send messages [Easy]
        @Autowired
        private KafkaTemplate<String, Object> kafkaTemplate;

        public void q6_q7() {
            /*
             * Answer: Use KafkaTemplate.send(topic, key, payload)
             */
        }

        public void publishOrder(OrderEvent event) {
            System.out.println("Producing Order Event: " + event.getOrderId());
            
            // Sending with a KEY (event.getOrderId()) ensures all events for this 
            // specific order go to the exact same partition (Q12 & Q13).
            CompletableFuture<?> future = kafkaTemplate.send("orders-topic", event.getOrderId(), event);
            
            // Handling the async callback (Optional but recommended)
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Message sent successfully");
                } else {
                    System.out.println("Failed to send message: " + ex.getMessage());
                }
            });
        }
    }

    @Service
    public static class KafkaConsumerService {

        // Q8 & Q9: Basic @KafkaListener [Easy]
        public void q8_q9() {
            /*
             * Answer: Method annotated with @KafkaListener continuously polls the topic.
             */
        }
        
        // Q18: Configuring a Dead Letter Topic (DLT) using @RetryableTopic [Medium]
        // If an exception is thrown, it retries 3 times (with backoff). 
        // If it still fails, it automatically routes the message to 'orders-topic-dlt'.
        @RetryableTopic(
                attempts = "3", 
                dltStrategy = DltStrategy.FAIL_ON_ERROR,
                autoCreateTopics = "false" // Assume topics are created by ops
        )
        @KafkaListener(topics = "orders-topic", groupId = "inventory-group")
        public void consumeOrder(
                @Payload OrderEvent event, 
                @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
            
            System.out.println("Received Order: " + event.getOrderId() + " from partition " + partition);
            
            if (event.getAmount() < 0) {
                // This exception triggers the @RetryableTopic logic
                throw new IllegalArgumentException("Amount cannot be negative!"); 
            }
            
            // Process inventory...
        }

        // Target method for the DLT. You can log it, save to DB, or trigger an alert.
        @KafkaListener(topics = "orders-topic-dlt", groupId = "inventory-group")
        public void handleDltMessage(OrderEvent failedEvent) {
            System.err.println("CRITICAL: Message permanently failed! Order ID: " + failedEvent.getOrderId());
        }

        // Q19: Consuming messages in Batches [Medium]
        // Requires spring.kafka.listener.type=batch in application.properties
        @KafkaListener(topics = "metrics-topic", groupId = "metrics-group")
        public void consumeBatch(List<String> metricsBatch) {
            System.out.println("Received a batch of " + metricsBatch.size() + " messages.");
            // E.g., perform a bulk insert into Elasticsearch
        }

        // Q20: Manual Acknowledgment (MANUAL_IMMEDIATE ackMode) [Medium]
        // Requires spring.kafka.listener.ack-mode=manual_immediate
        @KafkaListener(topics = "payment-topic", groupId = "payment-group")
        public void consumeWithManualAck(String message, Acknowledgment ack) {
            try {
                System.out.println("Processing payment: " + message);
                // Perform database update...
                
                // ONLY commit the offset if processing succeeds
                ack.acknowledge(); 
            } catch (Exception e) {
                System.err.println("Payment failed. Offset NOT committed. Message will be re-polled.");
            }
        }
    }
}
