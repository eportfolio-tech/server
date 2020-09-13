package tech.eportfolio.server.common.exception;

public class UserNotFoundException extends RuntimeException {
    public static final String PROMPT = "User doesn't exist: ";
    public UserNotFoundException(Long id) {
        super(PROMPT + id);
    }

    public UserNotFoundException(String username) {
        super(PROMPT + username);
    }
}
