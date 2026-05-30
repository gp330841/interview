package com.interview.javaconcepts.data_structures;

public interface CacheInterface {
     final Integer CAPACITY = 10;
    Node get(String key);
    Node put(String key, Object value);
}
