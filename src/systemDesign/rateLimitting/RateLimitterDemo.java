package systemDesign.rateLimitting;

import systemDesign.rateLimitting.strategies.FixWindowStrategyImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RateLimitterDemo {
    static void main() {
        String userId = "user123";
        runFixedWindow(userId);
    }

    public static void runFixedWindow(String userId) {
        RateLimiterStrategy strategy = new FixWindowStrategyImpl(1000L, 100);
        RateLimiterService service = RateLimiterService.getInstance();
        service.setRateLimitingStrategy(strategy);

        try (ExecutorService executor = Executors.newFixedThreadPool(5)) {
            for(int i = 0; i < 200; i++) {
                executor.submit(() -> {
                    String result = service.handleRequest(userId);
                    System.out.println(result);
                });
            }
        }



    }
}
