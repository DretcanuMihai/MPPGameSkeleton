package myapp.restapp;

public class RestException extends RuntimeException{

    public RestException(String message) {
        super(message);
    }
}
