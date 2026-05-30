package springboot.interview.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Spring Boot - Micrometer Metrics and Observability Interview Questions
 * 
 * This class contains common interview Q&As and mock examples for senior engineers
 * to understand how to register custom metrics inside a Spring Boot application.
 */
public class MetricsQA {

    // Q1: What are the main metric types supported by Micrometer?
    // Answer: 
    // 1. Counter: A monotonically increasing metric (e.g. total API hits, errors). Only goes up.
    // 2. Gauge: A fluctuating metric indicating current state (e.g. active threads, CPU usage, queue size).
    // 3. Timer: Measures both short duration execution times and frequency of events (e.g. database latency).
    // 4. DistributionSummary: Tracks distribution of events (e.g. size of payload payloads).

    // Mock Service demonstrating metric creation and usage
    public static class MockMetricsService {
        
        private final Counter orderCounter;
        private final Timer executionTimer;
        private final AtomicInteger activeConnections = new AtomicInteger(0);

        public MockMetricsService(MeterRegistry registry) {
            // Q2: How do you build and register a custom Counter?
            this.orderCounter = Counter.builder("processed_orders_total")
                    .description("Tracks total number of successfully processed orders")
                    .tag("type", "retail")
                    .register(registry);

            // Q3: How do you build and register a custom Gauge to track a dynamic value?
            Gauge.builder("active_client_connections", activeConnections, AtomicInteger::get)
                    .description("Tracks current count of active client connections")
                    .tag("tier", "premium")
                    .register(registry);

            // Q4: How do you register a custom Timer?
            this.executionTimer = Timer.builder("order_processing_latency")
                    .description("Tracks processing latency of order subroutines")
                    .register(registry);
        }

        public void processOrder() {
            // Increment the counter
            orderCounter.increment();

            // Record execution duration using the Timer
            executionTimer.record(() -> {
                // Simulate order processing logic
                activeConnections.incrementAndGet();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    activeConnections.decrementAndGet();
                }
            });
        }
    }
}
