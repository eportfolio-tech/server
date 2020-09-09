package tech.eportfolio.server.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String message) {
        super("Email not found " + message);
    }
}
