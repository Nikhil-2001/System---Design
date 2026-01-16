package org.example.state;

import org.example.models.Game;
import org.example.models.Player;

public class WinnerState implements GameState{
    @Override
    public void handleMove(Game game, Player player, int row, int col) throws Exception {
        throw new Exception("Game is already over. " + game.getWinner().getName() + " has won.");
    }
}
