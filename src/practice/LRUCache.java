//package practice;
//
//import systemDesign.lru.DoublyLinkedList;
//
//import java.security.DrbgParameters;
//import java.util.Map;
//import java.util.Objects;
//
//public class LRUCache implements CacheInterface {
//    Map<String, Node> map;
//
//
//    @Override
//    public Node get(String key) {
//        if(map.containsKey(key)) {
//            Node node = map.get(key);
//            //adding at front of dll
//            dll.addAtFirst(node);
//            return node;
//        }
//        return null;
//    }
//
//    @Override
//    public Node put(String key, Object value) {
//        //DLL is empty
//        if(dll.isEmpty()) {
//            //create or initialize dll
//        }
//        if(map.contains(key)) {
//            Node node = map.get(key);
//            if(!Objects.equals(node.value, value)) {
//                node.value = value;
//            }
//            //add at front
//            dll.adAtFirst(node);
//        } else {
//            //if dll capacity is reached we need to remove one from back
//            if(map.size()== CAPACITY) {
//                //remove from last
//                dll.remove();
//                dll.adAtFirst(node);
//            }
//        }
//
//
//        return null;
//    }
//}
