package org.example.strategy;

import org.example.models.Board;
import org.example.models.Player;

public interface Strategy {
    public boolean check(Board board, Player player);
}
