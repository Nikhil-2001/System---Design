package org.example.session;

import org.example.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating Game instances with predefined configurations
 * Simplifies game creation and provides consistent setup
 */
public class GameFactory {

    /**
     * Game template types with predefined board configurations
     */
    public enum GameTemplate {
        CLASSIC,
        EASY,
        HARD,
        CUSTOM
    }

    /**
     * Create a game with a specific template
     */
    public static Game createGame(GameTemplate template, List<String> playerNames) {
        return createGame(template, playerNames, null);
    }

    /**
     * Create a game with custom board entities
     */
    public static Game createGame(GameTemplate template, List<String> playerNames, List<BoardEntity> customEntities) {
        int boardSize;
        List<BoardEntity> entities;

        switch (template) {
            case CLASSIC:
                boardSize = 100;
                entities = getClassicBoardEntities();
                break;
            case EASY:
                boardSize = 100;
                entities = getEasyBoardEntities();
                break;
            case HARD:
                boardSize = 100;
                entities = getHardBoardEntities();
                break;
            case CUSTOM:
                boardSize = 100;
                entities = customEntities != null ? customEntities : new ArrayList<>();
                break;
            default:
                boardSize = 100;
                entities = getClassicBoardEntities();
        }

        return new Game.Builder()
                .setBoard(boardSize, entities)
                .setPlayers(playerNames)
                .setDice(new Dice())
                .build();
    }

    /**
     * Create a game with custom board size and entities
     */
    public static Game createCustomGame(int boardSize, List<BoardEntity> entities, List<String> playerNames) {
        return new Game.Builder()
                .setBoard(boardSize, entities)
                .setPlayers(playerNames)
                .setDice(new Dice())
                .build();
    }

    /**
     * Classic board configuration - balanced snakes and ladders
     */
    private static List<BoardEntity> getClassicBoardEntities() {
        return List.of(
                new Snake(17, 7),
                new Snake(54, 34),
                new Snake(62, 19),
                new Snake(98, 79),
                new Ladder(3, 38),
                new Ladder(24, 33),
                new Ladder(42, 93),
                new Ladder(72, 84)
        );
    }

    /**
     * Easy board configuration - more ladders, fewer snakes
     */
    private static List<BoardEntity> getEasyBoardEntities() {
        return List.of(
                new Snake(95, 75),
                new Snake(87, 36),
                new Ladder(4, 14),
                new Ladder(9, 31),
                new Ladder(20, 38),
                new Ladder(28, 84),
                new Ladder(40, 59),
                new Ladder(51, 67),
                new Ladder(63, 81),
                new Ladder(71, 91)
        );
    }

    /**
     * Hard board configuration - more snakes, fewer ladders
     */
    private static List<BoardEntity> getHardBoardEntities() {
        return List.of(
                new Snake(99, 54),
                new Snake(95, 72),
                new Snake(92, 53),
                new Snake(83, 19),
                new Snake(73, 28),
                new Snake(69, 33),
                new Snake(64, 36),
                new Snake(59, 17),
                new Snake(55, 7),
                new Snake(52, 29),
                new Snake(48, 16),
                new Snake(46, 5),
                new Ladder(2, 23),
                new Ladder(8, 26),
                new Ladder(21, 42)
        );
    }

    /**
     * Create a quick game session with default settings
     */
    public static GameSession createQuickSession(List<String> playerNames) {
        Game game = createGame(GameTemplate.CLASSIC, playerNames);
        return new GameSession(game);
    }

    /**
     * Create a game session with specific template
     */
    public static GameSession createSession(GameTemplate template, List<String> playerNames) {
        Game game = createGame(template, playerNames);
        return new GameSession(game);
    }

    /**
     * Create a custom game session
     */
    public static GameSession createCustomSession(int boardSize, List<BoardEntity> entities, List<String> playerNames) {
        Game game = createCustomGame(boardSize, entities, playerNames);
        return new GameSession(game);
    }
}
