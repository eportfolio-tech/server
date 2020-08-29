package tech.eportfolio.server.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Could not find user id" + id);
    }

    public UserNotFoundException(String username) {
        super("Could not find user username" + username);
    }
}
