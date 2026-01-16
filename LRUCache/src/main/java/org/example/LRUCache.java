package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LRUCache<K,V> {
    int capacity;
    DoubleLinkedList<K,V> list;
    Map<K, Node<K,V>> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.list = new DoubleLinkedList<>();
        cache = new ConcurrentHashMap<>();
    }

    public synchronized void put(K key, V value) {
        if (cache.containsKey(key)) {
            Node<K, V> node = cache.get(key);
            node.value = value;
            list.moveToFront(node);
        } else {
            if (cache.size() == capacity) {
                Node<K, V> lru = list.removeLast();
                if (lru != null) cache.remove(lru.key);
            }
            Node<K, V> newNode = new Node<>(key, value);
            list.addFirst(newNode);
            cache.put(key, newNode);
        }
    }

    public synchronized V get(K key) {
        if( cache.containsKey(key)) {
            list.moveToFront(cache.get(key));
            return cache.get(key).value;
        }
        return null;
    }

    public synchronized void remove(K key) {
        if( cache.containsKey(key)) {
            Node<K, V> node = cache.get(key);
            list.remove(node);
            cache.remove(key);
        }
    }
}
