package org.example.observer;

import org.example.models.Game;

public interface GameObserver {
    void update(Game game);
}
