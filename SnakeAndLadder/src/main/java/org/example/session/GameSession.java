package org.example.session;

import org.example.model.Game;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Encapsulates a game instance with session metadata
 * Provides lifecycle management for individual game sessions
 */
public class GameSession {
    private final String sessionId;
    private final Game game;
    private final LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private SessionStatus status;
    private final Object lock = new Object();

    public GameSession(Game game) {
        this.sessionId = UUID.randomUUID().toString();
        this.game = game;
        this.createdAt = LocalDateTime.now();
        this.status = SessionStatus.CREATED;
    }

    public GameSession(String sessionId, Game game) {
        this.sessionId = sessionId;
        this.game = game;
        this.createdAt = LocalDateTime.now();
        this.status = SessionStatus.CREATED;
    }

    /**
     * Start the game session in a new thread for concurrent execution
     */
    public void startAsync() {
        synchronized (lock) {
            if (status != SessionStatus.CREATED) {
                throw new IllegalStateException("Game session can only be started from CREATED state. Current state: " + status);
            }
            this.startedAt = LocalDateTime.now();
            this.status = SessionStatus.RUNNING;
        }

        Thread gameThread = new Thread(() -> {
            try {
                game.play();
                synchronized (lock) {
                    this.status = SessionStatus.COMPLETED;
                    this.endedAt = LocalDateTime.now();
                }
            } catch (Exception e) {
                synchronized (lock) {
                    this.status = SessionStatus.FAILED;
                    this.endedAt = LocalDateTime.now();
                }
                System.err.println("Game session " + sessionId + " failed: " + e.getMessage());
            }
        }, "GameSession-" + sessionId);

        gameThread.start();
    }

    /**
     * Start the game session synchronously (blocking)
     */
    public void start() {
        synchronized (lock) {
            if (status != SessionStatus.CREATED) {
                throw new IllegalStateException("Game session can only be started from CREATED state. Current state: " + status);
            }
            this.startedAt = LocalDateTime.now();
            this.status = SessionStatus.RUNNING;
        }

        try {
            game.play();
            synchronized (lock) {
                this.status = SessionStatus.COMPLETED;
                this.endedAt = LocalDateTime.now();
            }
        } catch (Exception e) {
            synchronized (lock) {
                this.status = SessionStatus.FAILED;
                this.endedAt = LocalDateTime.now();
            }
            throw new RuntimeException("Game session " + sessionId + " failed", e);
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public Game getGame() {
        return game;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public SessionStatus getStatus() {
        synchronized (lock) {
            return status;
        }
    }

    public String getSessionInfo() {
        synchronized (lock) {
            return String.format("Session ID: %s | Status: %s | Created: %s | Started: %s | Ended: %s",
                    sessionId, status, createdAt, startedAt, endedAt);
        }
    }

    public enum SessionStatus {
        CREATED,
        RUNNING,
        COMPLETED,
        FAILED
    }
}
