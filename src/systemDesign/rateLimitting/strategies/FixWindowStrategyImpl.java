package systemDesign.rateLimitting.strategies;

import systemDesign.rateLimitting.RateLimiterStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixWindowStrategyImpl implements RateLimiterStrategy {
   private final Map<String, UserRequestInfo> userRequestMap ;
   private final Long windowSizeMills;
   private final Integer maxRequestAllowed;

   public FixWindowStrategyImpl(Long windowSizeMills, Integer maxRequestAllowed) {
       this.userRequestMap = new ConcurrentHashMap<>();
       this.windowSizeMills = windowSizeMills;
       this.maxRequestAllowed = maxRequestAllowed;
   }

    @Override
    public boolean allowRequest(String key) {
       Long currentTime = System.currentTimeMillis();
       userRequestMap.putIfAbsent(key, new UserRequestInfo(currentTime));
        UserRequestInfo userRequestInfo = userRequestMap.get(key);
        synchronized (userRequestInfo) {
            if (currentTime - userRequestInfo.startTime >= windowSizeMills) {
                userRequestInfo.reset(currentTime);
            }
            if (userRequestInfo.requestCount.get() < maxRequestAllowed) {
                userRequestInfo.requestCount.incrementAndGet();
                return true;
            } else {
                return false;
            }
        }
    }

    private static class UserRequestInfo {
        Long startTime;
        AtomicInteger requestCount;
       public UserRequestInfo(Long startTime) {
           this.startTime = startTime;
           this.requestCount = new AtomicInteger(0);
       }

       public void reset(Long startTime) {
           this.startTime = startTime;
           this.requestCount = new AtomicInteger(0);
       }
    }
}
