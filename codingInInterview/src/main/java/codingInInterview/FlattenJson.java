package codingInInterview;

import java.util.*;

public class FlattenJson {

    public static void flatten(Map<String, Object> input,
                               String parent,
                               Map<String, Object> result) {

        for (Map.Entry<String, Object> entry : input.entrySet()) {

            String key = parent.isEmpty()
                    ? entry.getKey()
                    : parent + "." + entry.getKey();

            Object value = entry.getValue();

            if (value instanceof Map) {
                flatten((Map<String, Object>) value, key, result);
            } else {
                result.put(key, value);
            }
        }
    }

    public static Map<String, Object> flattenJson(Map<String, Object> input) {

        Map<String, Object> result = new HashMap<>();
        flatten(input, "", result);
        return result;
    }

    public static void main(String[] args) {

        Map<String, Object> input = new HashMap<>();

        Map<String, Object> d = new HashMap<>();
        d.put("e", 3);

        Map<String, Object> b = new HashMap<>();
        b.put("c", 2);
        b.put("d", d);

        input.put("a", 1);
        input.put("b", b);

        Map<String, Object> output = flattenJson(input);

        System.out.println(output);
    }
}