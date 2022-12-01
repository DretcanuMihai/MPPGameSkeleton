package myapp.services.interfaces;

import myapp.model.entities.Game;

public interface IObserver {
    void gameEnded(Game game);
}
