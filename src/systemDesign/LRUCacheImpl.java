package systemDesign;

public class LRUCacheImpl<K, V> extends LRUCacheBase<K, V> {
    private final DoublyLinkedList<K, V> dll;

    public LRUCacheImpl(Integer cacheCapacity) {
        super(cacheCapacity);
        this.dll = new DoublyLinkedList<>(new Node<>(null, null), new Node<>(null, null));
    }


    @Override
    Node<K, V> get(K key) {
        if(!map.containsKey(key)) {
            return null;
        }
        Node<K, V> node = map.get(key);
        dll.moveToFront(node);
        printMap();
        return node;
    }

    @Override
    Node<K, V> put(K key, V value) {
        Node<K, V> resultNode;
        if(map.containsKey(key)) {
            Node<K, V> node = map.get(key);
            node.setValue(value);
            dll.moveToFront(node);
            resultNode = node;
        } else {
            if(map.size()>=cacheCapacity) {
                Node<K, V> removeNode = dll.removeLast();
                map.remove(removeNode.getKey());
            }

            Node<K, V> newNode = new Node<>(key, value);
            map.put(key, newNode);
            dll.addFirst(newNode);
            resultNode = newNode;
        }

        printMap();
        return resultNode;
    }

    void printMap() {
        map.keySet().forEach(k->System.out.print(map.get(k)));
        System.out.println();
    }
}
