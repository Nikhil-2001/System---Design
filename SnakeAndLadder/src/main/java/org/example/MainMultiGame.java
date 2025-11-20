package org.example;

import org.example.model.*;
import org.example.session.GameFactory;
import org.example.session.GameSession;
import org.example.session.GameSessionManager;

import java.util.List;

public class MainMultiGame {
    public static void main(String[] args) throws InterruptedException {
        GameSessionManager manager = GameSessionManager.getInstance();

        System.out.println("=== Scalable Snake and Ladder Game Demo ===\n");

        System.out.println("--- Creating Multiple Game Sessions ---");

        GameSession session1 = manager.createSession("CLASSIC", List.of("Alice", "Bob"));
        GameSession session2 = manager.createSession("EASY", List.of("Charlie", "David", "Eve"));
        GameSession session3 = manager.createSession("HARD", List.of("Frank", "Grace"));

        System.out.println("\nCreated 3 game sessions with different difficulty levels\n");

        System.out.println("--- Starting Games Concurrently ---");

        manager.startSession(session1.getSessionId(), true); // async
        Thread.sleep(100);
        manager.startSession(session2.getSessionId(), true); // async
        Thread.sleep(100);
        manager.startSession(session3.getSessionId(), true); // async

        System.out.println("\nAll games started concurrently!\n");

        System.out.println("--- Monitoring Active Sessions ---");
        Thread.sleep(2000);

        List<GameSession> activeSessions = manager.getActiveSessions();
        System.out.println("Active sessions: " + activeSessions.size());

        manager.printSessionsSummary();

        System.out.println("--- Waiting for All Games to Complete ---");
        boolean allCompleted = false;
        int maxWait = 60;
        int waited = 0;

        while (!allCompleted && waited < maxWait) {
            Thread.sleep(1000);
            waited++;

            int running = manager.getActiveSessions().size();
            int completed = manager.getCompletedSessions().size();

            System.out.printf("Status: %d running, %d completed\n", running, completed);

            if (running == 0 && completed >= 3) {
                allCompleted = true;
            }
        }

        System.out.println("\n--- Final Statistics ---");
        manager.printSessionsSummary();

        System.out.println("\n--- Creating Custom Game Session ---");

        List<BoardEntity> customEntities = List.of(
                new Snake(25, 5),
                new Snake(47, 19),
                new Snake(83, 45),
                new Ladder(8, 31),
                new Ladder(21, 42),
                new Ladder(51, 67)
        );

        GameSession customSession = manager.createCustomSession(
                100,
                customEntities,
                List.of("Player1", "Player2")
        );

        System.out.println("Starting custom game...");
        manager.startSession(customSession.getSessionId(), false); // synchronous

        System.out.println("\n--- Cleaning Up ---");
        int cleared = manager.clearFinishedSessions();
        System.out.println("Cleared " + cleared + " finished sessions");

        manager.printSessionsSummary();

        System.out.println("\n=== Demo Complete ===");
    }
}
