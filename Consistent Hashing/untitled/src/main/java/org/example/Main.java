package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        ConsistentHashing ch = new ConsistentHashing(100);

        // Add nodes to the system
        ch.addNode("Server-1");
        ch.addNode("Server-2");
        ch.addNode("Server-3");
        ch.addNode("Server-4");
        ch.addNode("Server-5");

        ch.printNodes();

        // Simulating 1 Million Requests
        int requestCount = 1_000_000;

        for (int i = 0; i < requestCount; i++) {
            String key = "request-" + i;
            String assignedNode = ch.addKey(key);
            if (assignedNode == null) {
                logger.warning("Failed to assign key: " + key);
            }
        }
        ch.printDistribution();

        ch.removeNode("Server-1");
        ch.removeNode("Server-2");
        System.out.println("Request Distribution:");
        ch.printDistribution();
    }
}