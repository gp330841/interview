package com.interview.javaconcepts.topic11_data_structures_impl;

public interface CacheInterface {
     final Integer CAPACITY = 10;
    Node get(String key);
    Node put(String key, Object value);
}
