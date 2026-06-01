package codingInInterview.companyWise.greyOrange;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a Least Recently Used (LRU) Cache.
 * Provides O(1) time complexity for both get and put operations.
 */
public class LRUCache<K, V> {

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "{" + key + "=" + value + "}";
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final Node<K, V> head;
    private final Node<K, V> tail;

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
        this.map = new HashMap<>();
        
        // Dummy head and tail nodes to simplify boundary operations
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    /**
     * Retrieves the value associated with the key from the cache.
     * Moves the accessed node to the head of the doubly linked list.
     */
    public V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            return null;
        }
        moveToHead(node);
        return node.value;
    }

    /**
     * Inserts or updates the value associated with the key.
     * Moves the node to the head. If capacity is exceeded, removes the LRU node.
     */
    public void put(K key, V value) {
        Node<K, V> node = map.get(key);
        if (node != null) {
            node.value = value;
            moveToHead(node);
        } else {
            if (map.size() >= capacity) {
                Node<K, V> lruNode = removeTail();
                if (lruNode != null) {
                    map.remove(lruNode.key);
                }
            }
            Node<K, V> newNode = new Node<>(key, value);
            map.put(key, newNode);
            addNode(newNode);
        }
    }

    /**
     * Removes the node from the tail (the least recently used element).
     */
    private Node<K, V> removeTail() {
        Node<K, V> res = tail.prev;
        if (res == head) {
            return null; // Empty cache
        }
        removeNode(res);
        return res;
    }

    /**
     * Adds a new node right after the dummy head.
     */
    private void addNode(Node<K, V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    /**
     * Unlinks an existing node from the list.
     */
    private void removeNode(Node<K, V> node) {
        Node<K, V> prevNode = node.prev;
        Node<K, V> nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    /**
     * Moves an existing node to the head of the list.
     */
    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addNode(node);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<K, V> curr = head.next;
        while (curr != tail) {
            sb.append(curr);
            if (curr.next != tail) {
                sb.append(", ");
            }
            curr = curr.next;
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("--- Testing LRUCache ---");
        LRUCache<Integer, String> cache = new LRUCache<>(3);

        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");
        System.out.println("Cache after inserting 1, 2, 3: " + cache);

        System.out.println("Getting key 2 (value: " + cache.get(2) + ")");
        System.out.println("Cache after accessing 2: " + cache);

        cache.put(4, "Four");
        System.out.println("Cache after putting 4 (1 should be evicted): " + cache);

        System.out.println("Getting key 1 (should be null): " + cache.get(1));
    }
}
