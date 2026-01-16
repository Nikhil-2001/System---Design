package org.example;

import org.example.models.Game;
import org.example.models.Player;
import org.example.observer.ScoreBoard;

public class TicTacToeManager {
    private static volatile TicTacToeManager instance;
    private Game game;
    private final ScoreBoard scoreboard;

    private TicTacToeManager() {
        this.scoreboard = new ScoreBoard();
    }

    public static synchronized TicTacToeManager getInstance() {
        if (instance == null) {
            instance = new TicTacToeManager();
        }
        return instance;
    }

    public void createGame(Player player1, Player player2) {
        this.game = new Game(player1, player2);
        this.game.addObserver(this.scoreboard);
        System.out.printf("Game started between %s (X) and %s (O).%n", player1.getName(), player2.getName());
    }

    public void makeMove(Player player, int row, int col) {
        if (game == null) {
            System.out.println("No game in progress. Please create a game first.");
            return;
        }
        try {
            System.out.printf("%s plays at (%d, %d)%n", player.getName(), row, col);
            game.makeMove(player, row, col);
            printBoard();
            System.out.println("Game Status: " + game.getStatus());
            if (game.getWinner() != null) {
                System.out.println("Winner: " + game.getWinner().getName());
            }
        } catch ( Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void printBoard() {
        game.getBoard().printBoard();
    }

    public void printScoreBoard() {
        scoreboard.printScores();
    }
}
