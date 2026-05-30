package springboot.interview.metrics.components;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/metrics")
public class MetricsDemoController {

    private final Counter customVisitCounter;
    private final Timer customRequestTimer;

    public MetricsDemoController(MeterRegistry meterRegistry) {
        // Register a custom Micrometer Counter to track total API visits
        this.customVisitCounter = Counter.builder("custom_api_visits_total")
                .description("Tracks total visits to the custom metrics demo endpoint")
                .tag("tier", "demo")
                .register(meterRegistry);

        // Register a custom Micrometer Timer to track processing latency
        this.customRequestTimer = Timer.builder("custom_api_latency_seconds")
                .description("Tracks execution latency of the custom metrics demo endpoint")
                .tag("tier", "demo")
                .register(meterRegistry);
    }

    @GetMapping("/demo")
    public String triggerMetrics() {
        // Record latency using Micrometer Timer
        return customRequestTimer.record(() -> {
            // Increment the Counter
            customVisitCounter.increment();

            // Simulate brief mock latency (e.g. 50ms) to populate timer data
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return "Metrics recorded! Custom Visit Counter incremented, Custom Timer latency tracked.";
        });
    }
}
