package tech.eportfolio.server.common.exception;

public class ContentNotExistException extends RuntimeException {
    public ContentNotExistException(String message) {
        super("User " + message + "does not have a portfolio");
    }
}
