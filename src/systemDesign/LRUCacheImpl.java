package systemDesign;

import java.util.Map;
import java.util.stream.Collectors;

public class LRUCacheImpl extends LRUCacheBase<String, String> {
    private final DoublyLinkedList<String, String> dll;

    public LRUCacheImpl(Integer cacheCapacity) {
        super(cacheCapacity);
        this.dll = new DoublyLinkedList<>(new Node<>(null, null), new Node<>(null, null));
    }


    @Override
    Node<String, String> get(String key) {
        Node<String, String> node = map.get(key);
        dll.moveToFront(node);
        printMap();
        return node;
    }

    @Override
    Node<String, String> put(String key, String value) {
        Node<String, String> resultNode;
        if(map.containsKey(key)) {
            Node<String, String> node = map.get(key);
            node.setValue(value);
            dll.moveToFront(node);
            resultNode = node;
        } else {
            if(map.size()>=cacheCapacity) {
                Node<String, String> removeNode = dll.removeLast();
                map.remove(removeNode.getKey());
            }

            Node<String,String> newNode = new Node<>(key, value);
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
