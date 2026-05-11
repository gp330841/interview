package springboot.interview.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Spring Boot - Caching (Redis) Interview Questions
 * Contains practical code examples for @Cacheable, SpEL Conditions, and Custom TTL Configuration.
 */
public class CacheQA {

    // Dummy DTO
    public static class Product {
        private Long id;
        private String name;
        private double price;

        public Product(Long id, String name, double price) {
            this.id = id; this.name = name; this.price = price;
        }
        public Long getId() { return id; }
        public String getName() { return name; }
    }

    @Service
    public static class ProductService {

        // Q4, Q11, Q12: @Cacheable, SpEL keys, and conditions [Easy/Medium]
        public void q4_q11_q12() {
            /*
             * Answer: @Cacheable checks the cache before executing.
             * The 'key' uses SpEL to target specific arguments.
             * 'unless = "#result == null"' prevents caching if the DB returns nothing (prevents poisoning).
             * 'sync = true' (Q15) prevents Cache Stampede.
             */
        }
        @Cacheable(value = "products", key = "#productId", unless = "#result == null", sync = true)
        public Product getProductById(Long productId) {
            System.out.println("Cache MISS! Hitting the database for product " + productId);
            // Simulate slow DB call
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            return new Product(productId, "Laptop", 1200.00);
        }

        // Q6: @CachePut for updating [Easy]
        public void q6() {
            /*
             * Answer: @CachePut ALWAYS executes the method and overwrites the existing cache entry.
             */
        }
        @CachePut(value = "products", key = "#product.id")
        public Product updateProduct(Product product) {
            System.out.println("Updating product in DB and overwriting cache...");
            return product; // This returned object replaces the cached value
        }

        // Q5 & Q13: @CacheEvict and allEntries [Easy/Medium]
        public void q5_q13() {
            /*
             * Answer: Removes a specific entry. allEntries=true clears the entire "products" region.
             */
        }
        @CacheEvict(value = "products", key = "#productId")
        public void deleteProduct(Long productId) {
            System.out.println("Deleting product from DB and removing from cache...");
        }

        @CacheEvict(value = "products", allEntries = true)
        public void clearEntireProductCache() {
            System.out.println("Wiped the entire products cache.");
        }

        // Q17: @Caching for multiple operations [Medium]
        public void q17() {
            /*
             * Answer: Groups multiple cache annotations together.
             */
        }
        @Caching(evict = {
            @CacheEvict(value = "products", key = "#productId"),
            @CacheEvict(value = "topSellersCache", allEntries = true)
        })
        public void completelyRemoveProduct(Long productId) {
            // Logic here
        }
    }

    // Q16 & Q18: Configuring TTL and JSON Serialization in Redis [Medium]
    @Configuration
    public static class RedisCacheConfig {

        @Bean
        public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
            
            // Setting up Jackson to serialize objects to JSON (instead of unreadable Java Bytecode)
            GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

            RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                    // Set default TTL to 10 minutes
                    .entryTtl(Duration.ofMinutes(10)) 
                    // Prevent caching null values globally
                    .disableCachingNullValues()       
                    // Apply JSON serialization
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

            return RedisCacheManager.builder(connectionFactory)
                    .cacheDefaults(defaultConfig)
                    .build();
        }
    }
}
