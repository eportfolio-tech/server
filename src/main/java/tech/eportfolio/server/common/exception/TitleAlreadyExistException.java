package tech.eportfolio.server.common.exception;

public class TitleAlreadyExistException extends RuntimeException {
    public TitleAlreadyExistException(String title) {
        super("title already exist " + title);
    }
}
