package org.example.models;

import org.example.enums.GameStatus;
import org.example.observer.GameSubject;
import org.example.state.GameState;
import org.example.state.InProgressState;
import org.example.strategy.ColoumnWinningStrategy;
import org.example.strategy.DiagonalWinningStrategy;
import org.example.strategy.RowWinningStrategy;
import org.example.strategy.Strategy;

import java.util.List;

public class Game extends GameSubject {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;
    private Player winner;
    private GameStatus status;
    private GameState state;
    private final List<Strategy> winningStrategies;

    public Game(Player player1, Player player2) {
        this.board = new Board(3);
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1; // Player 1 starts
        this.status = GameStatus.IN_PROGRESS;
        this.state = new InProgressState();
        this.winningStrategies = List.of(
                new RowWinningStrategy(),
                new ColoumnWinningStrategy(),
                new DiagonalWinningStrategy()
        );
    }

    public void makeMove(Player player, int row, int col) throws Exception {
        state.handleMove(this, player, row, col);
    }

    public boolean checkWinner(Player player) {
        for (Strategy strategy : winningStrategies) {
            if (strategy.check(board, player)) {
                return true;
            }
        }
        return false;
    }

    public void switchPlayer() {
        this.currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    public Board getBoard() { return board; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public Player getWinner() { return winner; }
    public void setWinner(Player winner) { this.winner = winner; }
    public GameStatus getStatus() { return status; }
    public void setState(GameState state) { this.state = state; }
    public void setStatus(GameStatus status) {
        this.status = status;
        if (status != GameStatus.IN_PROGRESS) {
            notifyObservers();
        }
    }
}