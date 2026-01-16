package org.example;

public class DoubleLinkedList<K, V> {
    Node<K, V> head, tail;

    public DoubleLinkedList() {
        head = new Node<>(null, null);
        tail = new Node<>(null, null);
        head.next = tail;
        tail.previous = head;
    }

    public void addFirst(Node<K, V> node) {
        node.next = head.next;
        node.previous = head;
        head.next.previous = node;
        head.next = node;
    }

    public void remove(Node<K, V> node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
    }

    public void moveToFront(Node<K, V> node) {
        remove(node);
        addFirst(node);
    }

    public Node<K,V> removeLast() {
        Node<K,V> last = tail.previous;
        remove(last);
        return last;
    }
}
