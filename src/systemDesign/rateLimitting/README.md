# Rate Limiter System

A Rate Limiter strictly prevents cascading failures by restricting the total number of physical requests a user/client architecture can make in a given timeframe limit. This explicitly blocks localized DDoS attacks and stabilizes infrastructure fair usage. This package implementation heavily leverages the **Strategy Pattern** to support swapping out different rate-limiting algorithms dynamically.

## Class Breakdown
* **`RateLimiterStrategy.java`**: The core implementation Interface declaring the main validation boundary contract: `boolean allowRequest(String key)`.
* **`RateLimiterService.java`**: The context/service layer that delegates processing back to the provided `RateLimiterStrategy` object to dictate granting or blocking of incoming requests universally.
* **`RateLimitterDemo.java`**: Driver wrapper demo launching concurrently-simulated requests testing the logging behavior marking rejections and API resolutions.

### Rate Limiter Strategies
* **`strategies/FixWindowStrategyImpl.java`**:
  * Formally constructs the **Fixed Window Counter** algorithm bounds.
  * Consolidates memory limits managing a `ConcurrentHashMap` assigning identifiers (like User ID / API Keys) directly linked to a structured `UserRequestInfo` tracking blueprint.
  * **Thread Safety Paradigms**: It heavily implements `AtomicInteger` ensuring atomic counting structures. It enforces precision utilizing `synchronized` monitors wrapped on specific end-user segments averting threading race conditions specifically observed when validating time allocations via timestamp shifts (`currentTime - startTime > windowSizemills`). 
  * *Critical Interview Design Check*: While incredibly scalable & lightweight, Fixed Window logic strictly suffers from standard overlap vulnerabilities identified as "burst" edge-cases mapping overlapping window boundaries allowing users to fire double the allotted threshold over intersection spans. Implementing a secondary *Sliding Window Log* strategy typically fully stabilizes this vulnerability.
