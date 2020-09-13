package tech.eportfolio.server.exception;

public class UsernameExistException extends RuntimeException {
    public static final String SUBJECT = "username";
    public static final String PROMPT = "Username already exists: ";

    public UsernameExistException(String message) {
        super(PROMPT + message);
    }

    public UsernameExistException(String message, Throwable cause) {
        super(PROMPT + message, cause);
    }
}
