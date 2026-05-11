# Spring Boot - Caching (Redis)

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **API Rate Limiting with Redis**: An API Gateway needs to restrict users to 100 requests per minute. Using Redis (which operates in-memory and is extremely fast), the gateway increments a counter key tied to the user's IP or API token. Redis's TTL (Time To Live) feature automatically resets the counter every 60 seconds. This is much faster and more scalable than querying a relational database on every request.
2. **Accelerating Heavy Database Queries**: An e-commerce site has a "Top 10 Bestselling Products of the Week" widget on its homepage. Calculating this requires complex joins across millions of order records, taking ~3 seconds. By annotating the service method with `@Cacheable`, the result of the query is saved into Redis. Subsequent visitors get the data instantly (in milliseconds) from Redis without hitting the database, until the cache is evicted via `@CacheEvict` when a new order is placed.

---

## Beginner Level Questions

### Q1. What is Caching? [Easy]
**Answer:** Caching is the process of storing copies of frequently accessed data in a temporary, high-speed storage layer (usually RAM). When the data is requested again, it is served from the fast cache rather than re-computing it or fetching it from a slower underlying database, drastically improving performance.

### Q2. What is Redis? [Easy]
**Answer:** Redis (Remote Dictionary Server) is an open-source, in-memory data structure store. It is predominantly used as a distributed, highly-available cache and message broker. It supports various data structures like Strings, Hashes, Lists, Sets, and Sorted Sets.

### Q3. How do you enable caching in a Spring Boot application? [Easy]
**Answer:** You must add the `@EnableCaching` annotation to one of your `@Configuration` classes (or the main application class). Without this, all caching annotations (like `@Cacheable`) are completely ignored.

### Q4. What does the `@Cacheable` annotation do? [Easy]
**Answer:** It is applied to a method. Before the method executes, Spring checks the cache to see if the result already exists for the given parameters. If it does, the cached result is returned immediately, and the method is *not* executed. If it doesn't exist, the method executes, and its return value is saved into the cache.

### Q5. What does the `@CacheEvict` annotation do? [Easy]
**Answer:** It is used to remove stale data from the cache. When the annotated method is executed, Spring deletes the specified entry (or the entire cache if configured) from the cache store. This is usually placed on `update` or `delete` methods.

### Q6. What does the `@CachePut` annotation do? [Easy]
**Answer:** It is used to *update* the cache without interfering with the method execution. Unlike `@Cacheable`, a method annotated with `@CachePut` will *always* execute. Its return value is then placed into the cache, overwriting any existing value.

### Q7. What is a Cache "Key"? [Easy]
**Answer:** A cache operates like a Key-Value store (like a `HashMap`). The "Key" is the unique identifier used to store and retrieve the data. By default, Spring generates the key based on the parameters passed to the method.

### Q8. What happens if a `@Cacheable` method has no parameters? [Easy]
**Answer:** Spring uses a `SimpleKeyGenerator` which generates a default, constant key (essentially `SimpleKey.EMPTY`). This means the method will only ever cache one value, regardless of when it's called.

### Q9. What is a Cache Provider? [Easy]
**Answer:** Spring Caching is just an abstraction layer. A Cache Provider is the actual underlying implementation that stores the data. Examples include `ConcurrentHashMap` (the default local cache), Ehcache, Hazelcast, and Redis.

### Q10. What dependency do you need to use Redis caching in Spring Boot? [Easy]
**Answer:** You need `spring-boot-starter-data-redis` and `spring-boot-starter-cache`.

---

## Intermediate Level Questions

### Q11. How do you customize the Cache Key generation? [Medium]
**Answer:** You use the `key` attribute in the caching annotations, which accepts SpEL (Spring Expression Language).
**Code Example:**
```java
@Cacheable(value = "users", key = "#userId")
public User getUser(Long userId, boolean fetchDetails) { ... }
```

### Q12. How do you cache a method result conditionally? [Medium]
**Answer:** You use the `condition` or `unless` attributes, which also accept SpEL.
* `condition = "#id > 10"`: The result is cached *only if* the parameter `id` is greater than 10. (Evaluated *before* method execution).
* `unless = "#result == null"`: The result is cached *unless* the returned object is null. (Evaluated *after* method execution).

### Q13. What is the difference between `@CacheEvict(allEntries = true)` and normal eviction? [Medium]
**Answer:** By default, `@CacheEvict` only removes the specific entry matching the generated key. If you set `allEntries = true`, Spring will clear the *entire* cache region specified in the `value` attribute, regardless of keys. This is useful when a massive bulk update occurs.

### Q14. What is the "Cache Stampede" (or Thundering Herd) problem? [Medium]
**Answer:** It occurs when a highly requested cache key expires (or is evicted). Suddenly, thousands of concurrent requests miss the cache and hit the database simultaneously to recompute the same value, causing the database to crash.

### Q15. How does Spring Boot's `@Cacheable(sync = true)` help with Cache Stampedes? [Medium]
**Answer:** If multiple threads try to load the exact same missing cache key simultaneously, setting `sync = true` instructs the underlying cache provider (if it supports it) to lock the cache key. Only one thread is allowed to execute the actual method and hit the database. The other threads are blocked and wait until the first thread populates the cache, at which point they read the newly cached value.

### Q16. How do you configure a Time-To-Live (TTL) for Redis caches in Spring Boot? [Medium]
**Answer:** Spring's basic caching abstraction does not support TTL out of the box because not all providers support it. To set a TTL for Redis, you must configure a `RedisCacheManager` bean and set a default `RedisCacheConfiguration` with `entryTtl(Duration.ofMinutes(10))`.

### Q17. What is `@Caching` used for? [Medium]
**Answer:** Java does not allow multiple annotations of the exact same type on a single method. If you need to evict entries from two different caches simultaneously, you group them inside the `@Caching` annotation.
**Code Example:**
```java
@Caching(evict = { 
    @CacheEvict(value = "userCache", key = "#user.id"), 
    @CacheEvict(value = "usersListCache", allEntries = true) 
})
public void deleteUser(User user) { ... }
```

### Q18. How does Spring serialize objects to store them in Redis? [Medium]
**Answer:** Redis only understands bytes. Spring uses a `RedisSerializer` to convert Java objects to byte arrays. By default, it uses `JdkSerializationRedisSerializer` (which requires your objects to implement `Serializable` and produces unreadable binary data). A better practice is to configure a `GenericJackson2JsonRedisSerializer` to store data as readable JSON strings.

### Q19. What is `RedisTemplate`? [Medium]
**Answer:** It is a Spring helper class that simplifies Redis data access code. It handles connection management and serialization automatically, providing rich operations for Redis data types (e.g., `opsForValue()` for Strings, `opsForHash()` for Hashes, `opsForList()` for Lists).

### Q20. What is the difference between `RedisTemplate` and `StringRedisTemplate`? [Medium]
**Answer:** `StringRedisTemplate` is an extension of `RedisTemplate` configured specifically for handling Strings for both keys and values. It uses `StringRedisSerializer` by default. It is the most common template used because most Redis data is stored as strings (like JSON strings).

---

## Advanced Level Questions

### Q21. Explain Redis Eviction Policies. [Hard]
**Answer:** When Redis runs out of memory (reaches `maxmemory`), it must decide which keys to delete to make room for new data based on an eviction policy configured in `redis.conf`:
* `noeviction`: Returns errors when the memory limit is reached.
* `allkeys-lru`: Evicts the Least Recently Used keys out of all keys. (Most popular for pure caches).
* `volatile-lru`: Evicts the Least Recently Used keys, but only those that have an expire set (TTL).
* `allkeys-lfu`: Evicts the Least Frequently Used keys.

### Q22. How do you implement a distributed lock using Redis? [Hard]
**Answer:** You use Redis's `SETNX` (Set if Not eXists) command. 
1. Thread A tries `SETNX lock_key "node1_uuid"`. It succeeds, gets the lock.
2. Thread B tries `SETNX lock_key "node2_uuid"`. It fails, waits.
3. Thread A finishes and deletes `lock_key`.
*Important*: You must set an expiration (TTL) on the lock key so that if Thread A crashes, the lock is eventually released. In Java, it's safer to use the **Redisson** library which handles lock leasing and heartbeats automatically.

### Q23. What is the Cache Penetration problem, and how do you solve it? [Hard]
**Answer:** It occurs when a user repeatedly queries a key that does *not* exist in the cache AND does *not* exist in the database (e.g., querying `user/99999999`). Since it's not in the database, it's never cached, so every malicious request bypasses the cache and hits the database.
*Solution*: Cache the "null" result with a short TTL (`@Cacheable(unless="#result==null")` actually *causes* this problem if not careful, you should cache the nulls), or use a **Bloom Filter** to reject invalid keys before they even reach the cache layer.

### Q24. What is the Cache Avalanche problem? [Hard]
**Answer:** It occurs when thousands of cached items have the exact same TTL and expire at the exact same millisecond. The next immediate wave of requests will cause massive cache misses, crashing the database.
*Solution*: Add a random "jitter" to the TTL (e.g., base TTL of 1 hour + a random duration between 0-5 minutes) so the expirations are smeared over time.

### Q25. How do you configure different TTLs for different caches in Spring Boot? [Hard]
**Answer:** You must configure the `RedisCacheManager` bean using a builder. You define a default configuration, and then use `withInitialCacheConfigurations(Map<String, RedisCacheConfiguration>)` to specify distinct configurations (with different TTLs) for specific cache names.

### Q26. Explain Redis Persistence mechanisms (RDB vs AOF). [Hard]
**Answer:** Even though Redis is in-memory, it needs to survive restarts.
* **RDB (Redis Database Backup)**: Takes point-in-time snapshots of the dataset at specified intervals. Very fast to load, but you might lose the last few minutes of data if it crashes.
* **AOF (Append Only File)**: Logs every single write operation received by the server. Slower and requires more disk space, but provides much better durability (minimal data loss).

### Q27. What is Redis Sentinel? [Hard]
**Answer:** Redis Sentinel provides High Availability (HA) for Redis. It monitors the Master and Replica nodes. If the Master node goes down, Sentinel detects the failure, automatically promotes a Replica to be the new Master, and updates the application clients with the new connection details.

### Q28. What is Redis Cluster? [Hard]
**Answer:** Redis Cluster provides automatic data sharding (partitioning) across multiple Redis nodes, allowing you to scale out memory beyond a single machine's limits. It splits data across 16,384 "hash slots." It inherently includes high availability (Master-Replica setups per shard) without needing Sentinel.

### Q29. How do you perform a transaction in Redis? [Hard]
**Answer:** Redis transactions use the `MULTI`, `EXEC`, `DISCARD`, and `WATCH` commands. It guarantees that commands inside `MULTI/EXEC` are executed sequentially without interference from other clients. However, unlike relational databases, if one command fails during execution (e.g., trying to increment a string), Redis *does not roll back* the other successful commands in the block.

### Q30. Why is Redis single-threaded, and how is it still so fast? [Hard]
**Answer:** Redis executes commands using a single event loop thread. This eliminates the need for complex, CPU-expensive thread synchronization and locks. Because all data is in memory, operations are bound by CPU speed and network I/O, not disk I/O. By using efficient asynchronous I/O multiplexing (like `epoll`), a single thread can handle tens of thousands of connections and operations per second.
