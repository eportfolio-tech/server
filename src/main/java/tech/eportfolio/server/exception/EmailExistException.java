package tech.eportfolio.server.exception;

public class EmailExistException extends RuntimeException {
    public EmailExistException(String message) {
        super("Email already exist" + message);
    }
}
