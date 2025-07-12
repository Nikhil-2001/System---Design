package org.example;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

public class ConsistentHashing {
    private static final Logger logger = Logger.getLogger(ConsistentHashing.class.getName());
    private final TreeMap<Long, String> ring = new TreeMap<>();
    private final int virtualNodes;
    private final MessageDigest md5;
    private final MessageDigest sha256;
    private final Map<String, Set<String>> nodeToKeysMap = new TreeMap<>();

    public ConsistentHashing(int virtualNodes) {
        this.virtualNodes = virtualNodes;
        try {
            this.md5 = MessageDigest.getInstance("MD5");
            this.sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            logger.severe("Hash Algorithm not found: " + e.getMessage());
            throw new RuntimeException("Hash Algorithm not found", e);
        }
    }

    private long hashMD5(String key) {
        md5.update(key.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md5.digest();
        return ((long) (digest[3] & 0xFF) << 24) | ((long) (digest[2] & 0xFF) << 16) |
                ((long) (digest[1] & 0xFF) << 8) | (digest[0] & 0xFF);
    }

    private long hashSHA256(String key) {
        sha256.update(key.getBytes(StandardCharsets.UTF_8));
        byte[] digest = sha256.digest();
        return ((long) (digest[3] & 0xFF) << 24) | ((long) (digest[2] & 0xFF) << 16) |
                ((long) (digest[1] & 0xFF) << 8) | (digest[0] & 0xFF);
    }

    public void addNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hashSHA256(node + "#" + i);
            ring.put(hash, node);
        }
        logger.info("Node added: " + node);
    }

    public void removeNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hashSHA256(node + "#" + i);
            ring.remove(hash);
        }
        rebalanceKeys(node);
        logger.info("Node removed: " + node);
    }

    private void rebalanceKeys(String removedNode) {
        Set<String> keys = nodeToKeysMap.getOrDefault(removedNode, new HashSet<>());
        nodeToKeysMap.remove(removedNode);

        for (String key : keys) {
            String newNode = getNode(key);
            nodeToKeysMap.computeIfAbsent(newNode, k -> new HashSet<>()).add(key);
            //logger.info("Key: " + key + " reassigned from " + removedNode + " to " + newNode);
        }
    }

    public String getNode(String key) {
        if (ring.isEmpty()) {
            logger.warning("Ring is empty, cannot assign key: " + key);
            return null;
        }
        long hash = hashSHA256(key);
        Map.Entry<Long, String> entry = ring.ceilingEntry(hash);
        if (entry == null) return ring.firstEntry().getValue();
        return entry.getValue();
    }

    public String addKey(String key) {
        String assignedNode = getNode(key);
        if (assignedNode != null) {
            nodeToKeysMap.computeIfAbsent(assignedNode, k -> new HashSet<>()).add(key);
        }
        return  assignedNode;
    }

    public void printDistribution() {
        for(Map.Entry<String, Set<String>> entry : nodeToKeysMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().size());
        }
    }

    public void printNodes() {
        System.out.println("Current Nodes: " + new HashSet<>(ring.values()));
    }
}