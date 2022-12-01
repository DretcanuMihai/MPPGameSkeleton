package myapp.server;

import myapp.model.entities.Game;
import myapp.model.entities.Configuration;
import myapp.model.entities.Round;
import myapp.model.entities.User;
import myapp.model.validators.ValidationException;
import myapp.model.validators.interfaces.IValidator;
import myapp.persistence.interfaces.IConfigurationRepository;
import myapp.persistence.interfaces.IGameRepository;
import myapp.persistence.interfaces.IUserRepository;
import myapp.services.ServiceException;
import myapp.services.interfaces.ISuperService;
import myapp.services.interfaces.IObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuperService implements ISuperService {

    private final IValidator validator;

    private final IUserRepository userRepository;

    private final IGameRepository gameRepository;

    private final IConfigurationRepository configurationRepository;

    private final Map<User, IObserver> loggedClients;

    private final Map<User, Game> ongoingGames;

    public SuperService(IValidator validator, IUserRepository userRepository,
                        IGameRepository gameRepository, IConfigurationRepository configurationRepository) {
        this.validator = validator;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.configurationRepository = configurationRepository;
        loggedClients = new ConcurrentHashMap<>();
        ongoingGames = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized Game login(User user, IObserver observer) throws ServiceException {
        try {
            validator.validateUser(user);
        } catch (ValidationException e) {
            throw new ServiceException(e.getMessage());
        }

        User result = userRepository.read(user.getIdentifier());
        if (result == null) {
            throw new ServiceException("No user with given credentials!");
        }

//        if (loggedClients.get(user) != null) {
//            throw new ServiceException("User already logged in!");
//        }

        Game game = startGame(result);
        loggedClients.put(result, observer);
        ongoingGames.put(result, game);
        return game;
    }

    private Game startGame(User user) throws ServiceException {
        Game game = new Game();
        Configuration configuration = configurationRepository.getRandomConfig();
        if (configuration == null) {
            throw new ServiceException("Couldn't get configuration!");
        }
        game.setConfiguration(configuration);
        game.setUser(user);
        game.setRounds(new ArrayList<>());
        game.setStartDate(LocalDateTime.now());
        Game result = gameRepository.create(game);
        if (result == null) {
            throw new ServiceException("Couldn't create game!");
        }
        return result;
    }

    @Override
    public synchronized Game nextRound(User user, Round round) throws ServiceException {
        try {
            validator.validateUser(user);
        } catch (ValidationException e) {
            throw new ServiceException(e.getMessage());
        }
        if (loggedClients.get(user) == null) {
            throw new ServiceException("User not logged in!;\n");
        }

        Game game = ongoingGames.get(user);
        if (game.isFinished()) {
            throw new ServiceException("Game already ended!;\n");
        }

        try {
            validator.validateRound(round);
        } catch (ValidationException e) {
            throw new ServiceException(e.getMessage());
        }

        executeRound(game, round);

        Game result = gameRepository.update(game);
        if (result == null) {
            throw new ServiceException("Error updating the game!;\n");
        }
        result = gameRepository.read(game.getId());
        if (result == null) {
            throw new ServiceException("Error reading the game!;\n");
        }
        game=result;

        ongoingGames.put(user,game);
        if (game.isFinished()) {
            notifyUsers(game);
        }

        return game;
    }

    private void executeRound(Game game, Round round) throws ServiceException {
        try {
            validator.validateRoundExecution(game, round);
        } catch (ValidationException e) {
            throw new ServiceException(e.getMessage());
        }
        game.getRounds().add(round);
    }

    @Override
    public synchronized void logout(User user) throws ServiceException {
        IObserver observer = loggedClients.remove(user);
        ongoingGames.remove(user);
        if (observer == null) {
            throw new ServiceException("User not logged in!\n");
        }
    }

    @Override
    public synchronized List<Game> getLeaderBoard() throws ServiceException {
        List<Game> games = gameRepository.getLeaderBoard();
        if (games == null) {
            throw new ServiceException("Couldn't read leaderboard!\n");
        }
        return games;
    }

    private final int defaultThreadsNo = 5;

    private void notifyUsers(Game game) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for (IObserver observer : loggedClients.values()) {
            executor.execute(() -> observer.gameEnded(game));
        }
    }
}
