package myapp.model.validators.interfaces;


import myapp.model.entities.Configuration;
import myapp.model.entities.Game;
import myapp.model.entities.Round;
import myapp.model.entities.User;
import myapp.model.validators.ValidationException;

public interface IValidator {
    void validateUser(User entity) throws ValidationException;

    void validateConfiguration(Configuration configuration) throws ValidationException;

    void validateRound(Round round) throws ValidationException;

    void validateRoundExecution(Game game, Round round) throws ValidationException;
}
