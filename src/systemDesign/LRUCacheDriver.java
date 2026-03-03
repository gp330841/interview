package systemDesign;

public class LRUCacheDriver {

    static void main() {
        LRUCacheImpl<String, String> cache = new LRUCacheImpl<>(2);
        cache.put("key1", "value1");
//        System.out.println(cache);
        cache.put("key2", "value2");
        cache.get("key2");
        cache.put("key3", "value3");
        cache.get("key2");
    }
}
