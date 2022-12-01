package myapp.services.interfaces;

import myapp.model.entities.Game;
import myapp.model.entities.Round;
import myapp.model.entities.User;
import myapp.services.ServiceException;

import java.util.List;

public interface ISuperService {

    Game login(User user, IObserver observer) throws ServiceException;

    Game nextRound(User user, Round data) throws ServiceException;

    void logout(User user) throws ServiceException;

    List<Game> getLeaderBoard() throws ServiceException;
}
