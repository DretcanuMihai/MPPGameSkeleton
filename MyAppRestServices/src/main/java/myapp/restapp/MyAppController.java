package myapp.restapp;

import myapp.model.entities.Configuration;
import myapp.model.entities.Game;
import myapp.model.entities.User;
import myapp.model.validators.interfaces.IValidator;
import myapp.persistence.interfaces.IConfigurationRepository;
import myapp.persistence.interfaces.IGameRepository;
import myapp.persistence.interfaces.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/myapp")
public class MyAppController {

    private final IConfigurationRepository configurationRepository;

    private final IGameRepository gameRepository;

    private final IUserRepository userRepository;

    private final IValidator validator;

    @Autowired
    public MyAppController(IConfigurationRepository configurationRepository, IGameRepository gameRepository, IUserRepository userRepository, IValidator validator) {
        this.configurationRepository = configurationRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @RequestMapping(value = "/configurations",method = RequestMethod.POST)
    public Configuration createConfiguration(@RequestBody Configuration configuration) {
        try {
            validator.validateConfiguration(configuration);
        } catch (Exception e) {
            throw new RestException(e.getMessage());
        }
        Configuration result = configurationRepository.create(configuration);
        if (result == null) {
            throw new RestException("Couldn't create Configuration;\n");
        }
        return result;
    }

    @RequestMapping(value = "/users/{id}/games", method = RequestMethod.GET)
    public ResponseEntity<?> readUserGames(@PathVariable String id) {
        User user = new User(id);
        try {
            validator.validateUser(user);
        } catch (Exception e) {
            throw new RestException(e.getMessage());
        }
        User result = userRepository.read(id);
        if (result == null) {
            return new ResponseEntity<>("No user with given credentials;\n", HttpStatus.NOT_FOUND);
        }
        List<Game> games=gameRepository.getUserGames(user);
        if(games==null){
            return new ResponseEntity<>("Failed to get user games\n", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    @ExceptionHandler(RestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String error(RestException e) {
        e.printStackTrace();
        return e.getMessage();
    }
}
