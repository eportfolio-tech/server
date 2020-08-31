package tech.eportfolio.server.exception;

public class EmailNotValidException extends RuntimeException {
    public EmailNotValidException(String email) {
        super("User registration failed, email " + email + " is not valid");
    }
}
