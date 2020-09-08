package tech.eportfolio.server.exception;

public class PasswordNotMatchException extends RuntimeException {
    public PasswordNotMatchException() {
        super("New password not match with the old one");
    }
}
