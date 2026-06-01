package codingInInterview.companyWise.greyOrange;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation to flatten a nested JSON object represented as nested Maps in Java.
 * Uses recursion to traverse the nested structures and build a single-level map with dot-notation keys.
 */
public class FlattenNestedJson {

    /**
     * Flattens a nested Map representing a JSON structure.
     *
     * @param input The nested map input.
     * @return A flat map with keys represented in dot-notation (e.g. "a.b.c").
     */
    public static Map<String, Object> flatten(Map<String, Object> input) {
        Map<String, Object> result = new HashMap<>();
        if (input == null) {
            return result;
        }
        flattenHelper(input, "", result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static void flattenHelper(Map<String, Object> currentMap, String parentKey, Map<String, Object> result) {
        for (Map.Entry<String, Object> entry : currentMap.entrySet()) {
            String currentKey = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                // Recursive call for nested maps
                flattenHelper((Map<String, Object>) value, currentKey, result);
            } else {
                // Base case: put leaf values directly into result
                result.put(currentKey, value);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Testing FlattenNestedJson ---");

        // Construct a nested Map representation of JSON:
        // {
        //   "name": "GreyOrange",
        //   "info": {
        //     "established": 2011,
        //     "location": {
        //       "city": "Gurugram",
        //       "country": "India"
        //     }
        //   },
        //   "active": true
        // }
        Map<String, Object> location = new HashMap<>();
        location.put("city", "Gurugram");
        location.put("country", "India");

        Map<String, Object> info = new HashMap<>();
        info.put("established", 2011);
        info.put("location", location);

        Map<String, Object> input = new HashMap<>();
        input.put("name", "GreyOrange");
        input.put("info", info);
        input.put("active", true);

        System.out.println("Original Nested Map: " + input);

        Map<String, Object> flattened = flatten(input);
        System.out.println("\nFlattened Map:");
        flattened.forEach((k, v) -> System.out.println("  " + k + " : " + v));
    }
}
