package javaBasics;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Question: What is the difference between HashMap and ConcurrentHashMap?
 * 
 * Critical Points:
 * - HashMap: Not thread-safe. Allows one null key and multiple null values. Fast performance.
 * - ConcurrentHashMap: Thread-safe, used in highly concurrent applications. It divides the map 
 *   into segments and locks only the segment being updated (lock stripping), allowing multiple 
 *   reader and writer threads concurrently.
 * - ConcurrentHashMap does NOT allow null keys or null values. If attempted, it throws NullPointerException.
 */
public class CollectionsBasics {

    public static void main(String[] args) {
        System.out.println("--- HashMap Demo ---");
        Map<String, String> map = new HashMap<>();
        map.put("Key1", "Value1");
        map.put(null, "Value2"); // Valid in HashMap
        
        System.out.println("HashMap Output: " + map);

        System.out.println("\n--- ConcurrentHashMap Demo ---");
        Map<String, String> concurrentMap = new ConcurrentHashMap<>();
        concurrentMap.put("Key1", "Value1");
        
        try {
            // Un-commenting the following line would throw a NullPointerException!
            // concurrentMap.put(null, "Value2"); 
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.toString());
        }
        
        System.out.println("ConcurrentHashMap Output: " + concurrentMap);
    }
}
