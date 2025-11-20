package org.example.session;

import org.example.model.BoardEntity;
import org.example.model.Game;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Central manager for handling multiple concurrent game sessions
 * Provides thread-safe operations for creating, tracking, and managing game sessions
 * Singleton pattern ensures single source of truth for all sessions
 */
public class GameSessionManager {
    private static GameSessionManager instance;
    private final Map<String, GameSession> sessions;
    private final Object lock = new Object();

    private GameSessionManager() {
        this.sessions = new ConcurrentHashMap<>();
    }

    public static GameSessionManager getInstance() {
        if (instance == null) {
            synchronized (GameSessionManager.class) {
                if (instance == null) {
                    instance = new GameSessionManager();
                }
            }
        }
        return instance;
    }

    public GameSession createSession(String template, List<String> playerNames) {
        GameFactory.GameTemplate gameTemplate;
        try {
            gameTemplate = GameFactory.GameTemplate.valueOf(template.toUpperCase());
        } catch (IllegalArgumentException e) {
            gameTemplate = GameFactory.GameTemplate.CLASSIC;
        }

        GameSession session = GameFactory.createSession(gameTemplate, playerNames);
        sessions.put(session.getSessionId(), session);
        System.out.println("Created game session: " + session.getSessionId());
        return session;
    }

    public GameSession createCustomSession(int boardSize, List<BoardEntity> entities, List<String> playerNames) {
        GameSession session = GameFactory.createCustomSession(boardSize, entities, playerNames);
        sessions.put(session.getSessionId(), session);
        System.out.println("Created custom game session: " + session.getSessionId());
        return session;
    }

    public GameSession createSession(Game game) {
        GameSession session = new GameSession(game);
        sessions.put(session.getSessionId(), session);
        System.out.println("Created game session: " + session.getSessionId());
        return session;
    }

    public void startSession(String sessionId, boolean async) {
        GameSession session = sessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }

        System.out.println("Starting game session: " + sessionId + (async ? " (async)" : " (sync)"));
        if (async) {
            session.startAsync();
        } else {
            session.start();
        }
    }

    public GameSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public List<GameSession> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    public List<GameSession> getActiveSessions() {
        return sessions.values().stream()
                .filter(session -> session.getStatus() == GameSession.SessionStatus.RUNNING)
                .collect(Collectors.toList());
    }

    public List<GameSession> getCompletedSessions() {
        return sessions.values().stream()
                .filter(session -> session.getStatus() == GameSession.SessionStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    public List<GameSession> getSessionsByStatus(GameSession.SessionStatus status) {
        return sessions.values().stream()
                .filter(session -> session.getStatus() == status)
                .collect(Collectors.toList());
    }

    public boolean removeSession(String sessionId) {
        GameSession removed = sessions.remove(sessionId);
        if (removed != null) {
            System.out.println("Removed session: " + sessionId);
            return true;
        }
        return false;
    }

    public int clearFinishedSessions() {
        int count = 0;
        List<String> toRemove = new ArrayList<>();

        for (Map.Entry<String, GameSession> entry : sessions.entrySet()) {
            GameSession.SessionStatus status = entry.getValue().getStatus();
            if (status == GameSession.SessionStatus.COMPLETED || status == GameSession.SessionStatus.FAILED) {
                toRemove.add(entry.getKey());
            }
        }

        for (String sessionId : toRemove) {
            sessions.remove(sessionId);
            count++;
        }

        System.out.println("Cleared " + count + " finished sessions");
        return count;
    }

    public int getTotalSessionCount() {
        return sessions.size();
    }

    public SessionStats getStats() {
        int total = sessions.size();
        int created = 0;
        int running = 0;
        int completed = 0;
        int failed = 0;

        for (GameSession session : sessions.values()) {
            switch (session.getStatus()) {
                case CREATED:
                    created++;
                    break;
                case RUNNING:
                    running++;
                    break;
                case COMPLETED:
                    completed++;
                    break;
                case FAILED:
                    failed++;
                    break;
            }
        }

        return new SessionStats(total, created, running, completed, failed);
    }

    public void printSessionsSummary() {
        System.out.println("\n=== Game Sessions Summary ===");
        SessionStats stats = getStats();
        System.out.println(stats);
        System.out.println("\nSession Details:");
        for (GameSession session : sessions.values()) {
            System.out.println("  " + session.getSessionInfo());
        }
        System.out.println("=============================\n");
    }

    public static class SessionStats {
        private final int total;
        private final int created;
        private final int running;
        private final int completed;
        private final int failed;

        public SessionStats(int total, int created, int running, int completed, int failed) {
            this.total = total;
            this.created = created;
            this.running = running;
            this.completed = completed;
            this.failed = failed;
        }

        public int getTotal() { return total; }
        public int getCreated() { return created; }
        public int getRunning() { return running; }
        public int getCompleted() { return completed; }
        public int getFailed() { return failed; }

        @Override
        public String toString() {
            return String.format("Total: %d | Created: %d | Running: %d | Completed: %d | Failed: %d",
                    total, created, running, completed, failed);
        }
    }
}
