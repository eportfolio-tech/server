package tech.eportfolio.server.common.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String message) {
        super("Email not found " + message);
    }
}
