package tech.eportfolio.server.common.exception;

public class EmailExistException extends RuntimeException {
    public static final String SUBJECT = "email";
    public EmailExistException(String message) {
        super("Email already exist" + message);
    }
}
