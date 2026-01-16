package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
            LRUCache<String, Integer> cache = new LRUCache<>(3);
            cache.put("a", 1);
            cache.put("b", 2);
            cache.put("c", 3);
            System.out.println(cache.get("a"));
            cache.put("d", 4);
            System.out.println(cache.get("b"));
    }
}