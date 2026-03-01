package systemDesign;

public class DoublyLinkedList<K, V> {
    private Node<K, V> head;
    private Node<K, V> tail;

    public DoublyLinkedList(Node<K, V> head, Node<K, V> tail) {
        this.head = head;
        this.tail = tail;
        head.setNext(tail);
        tail.setPrev(head);
    }

    public void addFirst(Node<K, V> newNode) {
        Node<K, V> tmp = head.getNext();
        newNode.setNext(tmp);
        head.setNext(newNode);
        newNode.setPrev(head);
        tmp.setPrev(newNode);
    }

    public void remove(Node<K, V> newNode) {
        Node<K, V> prev = newNode.getPrev();
        Node<K, V> next = newNode.getNext();
        prev.setNext(next);
        next.setPrev(prev);
    }

    public void moveToFront(Node<K, V> newNode) {
            remove(newNode);
            addFirst(newNode);
    }

    public Node<K, V> removeLast() {
        Node<K, V> prev = tail.getPrev();
        prev.setNext(tail);
        tail.setPrev(prev);
        return prev;
    }


    public Node<K, V> getHead() {
        return head;
    }

    public void setHead(Node<K, V> head) {
        this.head = head;
    }

    public Node<K, V> getTail() {
        return tail;
    }

    public void setTail(Node<K, V> tail) {
        this.tail = tail;
    }
}
