package org.example.state;

import org.example.models.Game;
import org.example.models.Player;

public interface GameState {
    void handleMove(Game game, Player player, int row, int col) throws Exception;
}
