# Snake and Ladder - Scalable Multi-Game Architecture

## Overview
This document describes the scalable architecture for managing multiple concurrent Snake and Ladder game sessions. The design follows industry-standard patterns to ensure thread safety, maintainability, and extensibility.

## Architecture Components

### 1. GameSession
**Purpose**: Wraps a Game instance with metadata and lifecycle management

**Key Features**:
- Unique session ID (UUID)
- Timestamps (created, started, ended)
- Session status tracking (CREATED, RUNNING, COMPLETED, FAILED)
- Thread-safe state management
- Support for both synchronous and asynchronous execution

**Usage**:
```java
Game game = new Game.Builder()
    .setBoard(100, entities)
    .setPlayers(playerNames)
    .setDice(new Dice())
    .build();

GameSession session = new GameSession(game);
session.startAsync(); // Non-blocking execution
// or
session.start(); // Blocking execution
```

### 2. GameFactory
**Purpose**: Simplifies game creation with predefined templates

**Available Templates**:
- **CLASSIC**: Balanced snakes and ladders (original configuration)
- **EASY**: More ladders, fewer snakes (10 ladders, 2 snakes)
- **HARD**: More snakes, fewer ladders (15 snakes, 3 ladders)
- **CUSTOM**: User-defined configuration

**Usage**:
```java
// Create with template
Game game = GameFactory.createGame(GameTemplate.CLASSIC, playerNames);

// Create custom game
Game customGame = GameFactory.createCustomGame(boardSize, entities, playerNames);

// Create session directly
GameSession session = GameFactory.createSession(GameTemplate.EASY, playerNames);
```

### 3. GameSessionManager
**Purpose**: Central registry for managing multiple concurrent game sessions

**Key Features**:
- Singleton pattern (single source of truth)
- Thread-safe operations (ConcurrentHashMap)
- Session lifecycle management
- Query and filtering capabilities
- Statistics and monitoring

**Core Operations**:

#### Creating Sessions
```java
GameSessionManager manager = GameSessionManager.getInstance();

// Create from template
GameSession session1 = manager.createSession("CLASSIC", playerNames);

// Create custom session
GameSession session2 = manager.createCustomSession(100, entities, playerNames);

// Create from Game object
GameSession session3 = manager.createSession(game);
```

#### Managing Sessions
```java
// Start session (async or sync)
manager.startSession(sessionId, true);  // async
manager.startSession(sessionId, false); // sync

// Retrieve sessions
GameSession session = manager.getSession(sessionId);
List<GameSession> all = manager.getAllSessions();
List<GameSession> active = manager.getActiveSessions();
List<GameSession> completed = manager.getCompletedSessions();

// Clean up
manager.removeSession(sessionId);
int cleared = manager.clearFinishedSessions();
```

#### Monitoring
```java
// Get statistics
GameSessionManager.SessionStats stats = manager.getStats();
System.out.println("Total: " + stats.getTotal());
System.out.println("Running: " + stats.getRunning());

// Print summary
manager.printSessionsSummary();
```

## Design Patterns Used

### 1. **Singleton Pattern**
- `GameSessionManager` ensures single instance
- Provides global access point for session management

### 2. **Factory Pattern**
- `GameFactory` encapsulates object creation logic
- Provides template-based game creation
- Simplifies complex object construction

### 3. **Builder Pattern**
- `Game.Builder` (existing) for flexible game construction
- Maintains immutability of Game objects

### 4. **Facade Pattern**
- `GameSessionManager` provides simplified interface
- Hides complex session management logic

## Scalability Features

### 1. Concurrent Execution
```java
// Multiple games running simultaneously
manager.startSession(session1.getSessionId(), true);
manager.startSession(session2.getSessionId(), true);
manager.startSession(session3.getSessionId(), true);
```

### 2. Thread Safety
- Uses `ConcurrentHashMap` for session storage
- Synchronized blocks for critical sections
- Thread-safe status updates

### 3. Session Isolation
- Each game session runs independently
- No shared mutable state between sessions
- Separate threads for async execution

### 4. Resource Management
- Session cleanup capabilities
- Automatic session tracking
- Memory-efficient storage

## Usage Examples

### Example 1: Single Game Session
```java
GameSessionManager manager = GameSessionManager.getInstance();

GameSession session = manager.createSession("CLASSIC", 
    List.of("Alice", "Bob", "Charlie"));

manager.startSession(session.getSessionId(), false); // Synchronous
```

### Example 2: Multiple Concurrent Games
```java
GameSessionManager manager = GameSessionManager.getInstance();

// Create multiple sessions
GameSession session1 = manager.createSession("CLASSIC", List.of("Alice", "Bob"));
GameSession session2 = manager.createSession("EASY", List.of("Charlie", "David"));
GameSession session3 = manager.createSession("HARD", List.of("Eve", "Frank"));

// Start all concurrently
manager.startSession(session1.getSessionId(), true);
manager.startSession(session2.getSessionId(), true);
manager.startSession(session3.getSessionId(), true);

// Monitor progress
while (manager.getActiveSessions().size() > 0) {
    Thread.sleep(1000);
    System.out.println("Active games: " + manager.getActiveSessions().size());
}

// View results
manager.printSessionsSummary();
```

### Example 3: Custom Game Configuration
```java
GameSessionManager manager = GameSessionManager.getInstance();

List<BoardEntity> customEntities = List.of(
    new Snake(99, 10),
    new Snake(85, 24),
    new Ladder(4, 56),
    new Ladder(35, 78)
);

GameSession session = manager.createCustomSession(
    100, 
    customEntities, 
    List.of("Player1", "Player2")
);

manager.startSession(session.getSessionId(), false);
```

## Running the Demo

### Run Single Game (Original)
```bash
./gradlew run --args="org.example.Main"
```

### Run Multi-Game Demo
```bash
./gradlew run --args="org.example.MainMultiGame"
```

Or in IntelliJ IDEA:
1. Open `MainMultiGame.java`
2. Right-click and select "Run MainMultiGame.main()"

## API Reference

### GameSession API
```java
String getSessionId()
Game getGame()
LocalDateTime getCreatedAt()
LocalDateTime getStartedAt()
LocalDateTime getEndedAt()
SessionStatus getStatus()
String getSessionInfo()
void start()
void startAsync()
```

### GameFactory API
```java
static Game createGame(GameTemplate template, List<String> playerNames)
static Game createCustomGame(int boardSize, List<BoardEntity> entities, List<String> playerNames)
static GameSession createSession(GameTemplate template, List<String> playerNames)
static GameSession createQuickSession(List<String> playerNames)
```

### GameSessionManager API
```java
static GameSessionManager getInstance()
GameSession createSession(String template, List<String> playerNames)
GameSession createCustomSession(int boardSize, List<BoardEntity> entities, List<String> playerNames)
void startSession(String sessionId, boolean async)
GameSession getSession(String sessionId)
List<GameSession> getAllSessions()
List<GameSession> getActiveSessions()
List<GameSession> getCompletedSessions()
boolean removeSession(String sessionId)
int clearFinishedSessions()
SessionStats getStats()
void printSessionsSummary()
```

## Future Enhancements

### Potential Additions
1. **Persistence Layer**
   - Save/load sessions from database
   - Session history tracking

2. **REST API**
   - HTTP endpoints for session management
   - WebSocket for real-time updates

3. **Player Management**
   - Player profiles and statistics
   - Leaderboard system

4. **Advanced Features**
   - Pause/resume functionality
   - Spectator mode
   - Tournament support

5. **Monitoring & Metrics**
   - Performance metrics
   - Session analytics
   - Health checks

## Best Practices

1. **Always use GameSessionManager** for session creation and management
2. **Use templates** when possible for consistency
3. **Clean up finished sessions** regularly to free memory
4. **Use async execution** for concurrent games
5. **Monitor session status** for production systems

## Thread Safety Considerations

- All public methods in `GameSessionManager` are thread-safe
- `GameSession` uses synchronized blocks for state updates
- `ConcurrentHashMap` provides thread-safe session storage
- Each game runs in its own thread when started asynchronously

## Backward Compatibility

The new architecture is **fully backward compatible**:
- Original `Game`, `Board`, `Player`, `Dice` classes unchanged
- Original `Main.java` continues to work
- Builder pattern preserved
- No breaking changes to existing code

## Conclusion

This scalable architecture provides a robust foundation for managing multiple Snake and Ladder game sessions. The design follows industry best practices, ensures thread safety, and is easily extensible for future requirements.
