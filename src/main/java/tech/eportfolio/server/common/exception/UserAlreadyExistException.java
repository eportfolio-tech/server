package tech.eportfolio.server.common.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String user) {
        super("Could not find tag " + user);
    }
}
