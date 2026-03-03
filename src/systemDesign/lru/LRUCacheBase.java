package systemDesign.lru;

import java.util.HashMap;
import java.util.Map;

abstract class LRUCacheBase<K, V> {
    Map<K,Node<K, V>> map ;
    Integer cacheCapacity = 5;
    abstract Node<K, V> get(K key);
    abstract Node<K, V> put(K key, V value);

    public LRUCacheBase(Integer cacheCapacity) {
        this.map = new HashMap<>();
        this.cacheCapacity = cacheCapacity;
    }
}
