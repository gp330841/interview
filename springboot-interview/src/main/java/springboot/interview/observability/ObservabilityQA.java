package springboot.interview.observability;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

/**
 * Spring Boot - Observability (Logging, Tracing & Metrics) Interview Questions
 * Contains practical code examples for SLF4J, MDC, and Custom Micrometer Metrics.
 */
public class ObservabilityQA {

    private static final Logger log = LoggerFactory.getLogger(ObservabilityQA.class);

    @Service
    public static class OrderProcessingService {

        private final MeterRegistry meterRegistry;

        @Autowired
        public OrderProcessingService(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
            
            // Q20: Gauge Metric [Medium]
            // Monitors the size of a queue dynamically without needing to manually increment/decrement
            meterRegistry.gauge("orders.pending.queue.size", this, OrderProcessingService::getQueueSize);
        }

        // Q18: @Timed Annotation [Medium]
        // Automatically creates a Timer metric capturing count, max, and total duration.
        // Needs TimedAspect bean in configuration.
        @Timed(value = "order.processing.duration", description = "Time spent processing orders")
        public void processOrder(String orderId, String userId) {
            
            // Q11: Using MDC (Mapped Diagnostic Context) [Medium]
            // We put the userId and a generated transaction ID into the MDC.
            // EVERY log statement executed by this thread will now automatically include these IDs!
            MDC.put("userId", userId);
            MDC.put("transactionId", UUID.randomUUID().toString());

            try {
                // Q3: Using SLF4J Parameterized Logging [Easy]
                // {} is much faster than string concatenation because it only evaluates if INFO level is enabled
                log.info("Starting processing for order ID: {}", orderId);

                // Q14 & Q28: Custom Metrics with Bounded Tags [Medium/Hard]
                // We use 'type' (digital/physical) which is bounded. 
                // We DO NOT use 'orderId' as a tag to avoid High Cardinality memory crashes!
                meterRegistry.counter("orders.processed.total", "type", "digital").increment();

                simulateHeavyWork();

                log.info("Successfully finished processing order: {}", orderId);

            } catch (Exception e) {
                log.error("Failed to process order: {}", orderId, e);
                meterRegistry.counter("orders.failed.total", "reason", "timeout").increment();
            } finally {
                // VERY IMPORTANT: Always clear the MDC in a finally block!
                // Thread pools reuse threads. If you don't clear it, the next request 
                // handled by this thread will incorrectly log the previous user's ID.
                MDC.clear();
            }
        }

        private void simulateHeavyWork() throws Exception {
            Thread.sleep(new Random().nextInt(300));
            if (new Random().nextBoolean()) {
                throw new RuntimeException("Database Timeout");
            }
        }

        private int getQueueSize() {
            // Logic to check active backlog
            return new Random().nextInt(50);
        }
    }

    // Example showing Q22: MDC Context Loss in Async Programming [Hard]
    public static class AsyncMdcExample {
        
        public void processAsync() {
            MDC.put("traceId", "12345");
            log.info("Executing in Main Thread. TraceId is present.");

            // Standard Thread / Async execution LOSES the ThreadLocal MDC variables!
            new Thread(() -> {
                // If you log here, traceId will NOT be printed.
                log.info("Executing in Child Thread. TraceId is LOST!");
            }).start();
            
            MDC.clear();
        }
    }
}
