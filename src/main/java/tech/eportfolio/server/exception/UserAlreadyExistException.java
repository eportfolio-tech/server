package tech.eportfolio.server.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String user) {
        super("Could not find tag " + user);
    }
}
