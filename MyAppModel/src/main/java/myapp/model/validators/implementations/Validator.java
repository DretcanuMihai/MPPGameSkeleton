package myapp.model.validators.implementations;


import myapp.model.entities.Configuration;
import myapp.model.entities.Game;
import myapp.model.entities.Round;
import myapp.model.entities.User;
import myapp.model.validators.ValidationException;
import myapp.model.validators.interfaces.IValidator;

public class Validator implements IValidator {

    @Override
    public void validateUser(User user) throws ValidationException {
        String error = "";
        if (user == null)
            error += "User is null;\n";
        else {
            if (user.getUsername() == null)
                error += "username is null;\n";
        }
        if (!error.equals(""))
            throw new ValidationException(error);
    }

    @Override
    public void validateConfiguration(Configuration configuration) throws ValidationException {
        //todo: validate new fields
        String error = "";
        if (configuration == null)
            error += "Round is null;\n";
        if (!error.equals(""))
            throw new ValidationException(error);
    }

    @Override
    public void validateRound(Round round) throws ValidationException {
        //todo: validate new fields
        String error = "";
        if (round == null)
            error += "Round is null;\n";
        if (!error.equals(""))
            throw new ValidationException(error);
    }

    @Override
    public void validateRoundExecution(Game game, Round round) throws ValidationException {
        //todo: validate new fields
        String error = "";
        if (!error.equals(""))
            throw new ValidationException(error);
    }
}
