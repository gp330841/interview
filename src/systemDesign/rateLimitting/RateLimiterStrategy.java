package systemDesign.rateLimitting;

public interface RateLimiterStrategy {
    public boolean allowRequest(String key);
}
