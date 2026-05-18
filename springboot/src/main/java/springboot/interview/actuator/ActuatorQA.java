package springboot.interview.actuator;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Spring Boot - Actuator & Monitoring Interview Questions
 * Contains practical code examples for Custom Health Indicators, Custom Actuator Endpoints,
 * and Micrometer Metrics.
 */
public class ActuatorQA {

    // Q13: Creating a Custom Health Indicator [Medium]
    // This will automatically be included in the /actuator/health endpoint response
    @Component
    public static class PaymentGatewayHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            boolean isGatewayReachable = checkGatewayStatus();
            
            if (isGatewayReachable) {
                // Includes details only visible if management.endpoint.health.show-details=always
                return Health.up().withDetail("payment-api", "Available").build();
            }
            
            // Returns a 503 Service Unavailable if DOWN
            return Health.down()
                    .withDetail("payment-api", "Unreachable")
                    .withDetail("error", "Timeout connecting to Stripe")
                    .build();
        }
        
        private boolean checkGatewayStatus() {
            // Logic to ping the external API
            return true;
        }
    }

    // Q20: Creating a Custom Actuator Endpoint [Medium]
    // Will be exposed at /actuator/release-notes
    @Component
    @Endpoint(id = "release-notes")
    public static class ReleaseNotesEndpoint {

        @ReadOperation // Maps to an HTTP GET request
        public Map<String, String> getReleaseNotes() {
            Map<String, String> notes = new HashMap<>();
            notes.put("v1.0.0", "Initial Release");
            notes.put("v1.1.0", "Added Custom Actuator Endpoints");
            return notes;
        }
    }

    // Q22 & Q23: Micrometer Metrics (Counters and @Timed) [Hard]
    @Service
    public static class OrderProcessingService {

        private final MeterRegistry meterRegistry;

        @Autowired
        public OrderProcessingService(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
            
            // Example of a Gauge: A value that can go up and down
            meterRegistry.gauge("orders.queue.size", this, OrderProcessingService::getQueueSize);
        }

        // The @Timed annotation creates a Timer metric that tracks execution time, count, and percentiles.
        // It requires the TimedAspect Bean to be registered in your configuration.
        @Timed(value = "order.processing.time", description = "Time taken to process an order")
        public void processOrder(String orderType) {
            
            // Q22: Incrementing a custom Counter metric manually
            // Adding a "tag" (type) allows you to filter metrics in Grafana (e.g., only show 'digital' orders)
            meterRegistry.counter("orders.total", "type", orderType).increment();
            
            // Simulate work
            try { Thread.sleep(new Random().nextInt(500)); } catch (InterruptedException e) {}
        }
        
        private int getQueueSize() {
            // Logic to return current backlog size
            return new Random().nextInt(100);
        }
    }
}
