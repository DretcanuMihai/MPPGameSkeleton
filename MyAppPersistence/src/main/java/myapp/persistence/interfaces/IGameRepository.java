package myapp.persistence.interfaces;


import myapp.model.entities.Game;
import myapp.model.entities.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface IGameRepository extends IRepository<Integer, Game> {
    List<Game> getLeaderBoard();

    List<Game> getUserGames(User user);
}
