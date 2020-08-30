package tech.eportfolio.server.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String email) {
        super("User registration failed, email " + email + " has been taken.");
    }
}
