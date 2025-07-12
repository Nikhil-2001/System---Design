package org.learning;

import java.util.*;
import java.util.concurrent.*;

public class GossipFailureDetector {

    enum Status { ALIVE, SUSPECT, DEAD }

    static class NodeState {
        int heartbeat;
        Status status;
        long lastSeen;

        NodeState(int heartbeat, Status status) {
            this.heartbeat = heartbeat;
            this.status = status;
            this.lastSeen = System.currentTimeMillis();
        }
    }

    static class Node implements Runnable {
        String id;
        Map<String, NodeState> peerStates = new ConcurrentHashMap<>(); // Each nodes maintains a copy of other nodes status
        List<Node> cluster;
        int heartbeat = 0;
        Random random = new Random();

        static final int SUSPECT_TIMEOUT = 3000;
        static final int DEAD_TIMEOUT = 6000;

        Node(String id) {
            this.id = id;
        }

        void gossip() {
            if (cluster.isEmpty()) return;
            Node peer = cluster.get(random.nextInt(cluster.size()));
            if (peer == this) return;
            peer.receiveGossip(id, new HashMap<>(peerStates)); // it's node status map is passed to the random selected peer
        }

        void receiveGossip(String fromId, Map<String, NodeState> incomingStates) {
            incomingStates.forEach((nodeId, incoming) -> {    // now the receiver of gossiper updates it with latest info from gossiper
                peerStates.merge(nodeId, incoming, (current, inc) -> {
                    if (inc.heartbeat > current.heartbeat) {
                        return new NodeState(inc.heartbeat, inc.status);
                    }
                    return current;
                });
            });
        }

        void detectFailures() {
            long now = System.currentTimeMillis();
            for (var entry : peerStates.entrySet()) {
                NodeState state = entry.getValue();
                if (state.status == Status.ALIVE && now - state.lastSeen > SUSPECT_TIMEOUT) {
                    state.status = Status.SUSPECT;
                }
                if (state.status == Status.SUSPECT && now - state.lastSeen > DEAD_TIMEOUT) {
                    state.status = Status.DEAD;
                }
            }
        }

        void heartbeatTick() {
            heartbeat++;
            peerStates.put(id, new NodeState(heartbeat, Status.ALIVE));
        }

        void printStates() {
            System.out.println("[" + id + "] states:");
            peerStates.forEach((k, v) -> System.out.println("  " + k + ": " + v.status));
        }

        @Override
        public void run() {
            while (true) {
                heartbeatTick();
                gossip();
                detectFailures();
                try {
                    Thread.sleep(1000); // simulate 1-second gossip interval
                } catch (InterruptedException e) {
                    break;
                }
                printStates();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Node> cluster = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cluster.add(new Node("Node-" + i));
        }
        for (Node node : cluster) {
            node.cluster = cluster;
        }

        List<Thread> threads = new ArrayList<>();
        for (Node node : cluster) {
            Thread t = new Thread(node);
            threads.add(t);
            t.start();
        }

        // Simulate Node-2 failing after 5 seconds
        Thread.sleep(5000);
        System.out.println("Simulating failure of Node-2");
        threads.get(2).interrupt();

        // Let it run for a bit more
        Thread.sleep(10000);
        System.exit(0);
    }
}
