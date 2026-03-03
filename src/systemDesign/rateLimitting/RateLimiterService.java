package systemDesign.rateLimitting;

public class RateLimiterService {
    private static RateLimiterService instance;
    private RateLimiterStrategy rateLimiterStrategy;

    private RateLimiterService() {};

    public static synchronized RateLimiterService getInstance() {
        if (instance == null) {
            instance = new RateLimiterService();
        }
        return instance;
    }

    public void setRateLimitingStrategy(RateLimiterStrategy rateLimiterStrategy) {
        this.rateLimiterStrategy = rateLimiterStrategy;
    }

    public String handleRequest(String key) {
        if(rateLimiterStrategy.allowRequest(key)) {
            return key+": allowed";
        } else  {
            return key+": rejected";
        }
    }

}
