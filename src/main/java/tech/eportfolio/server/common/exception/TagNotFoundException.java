package tech.eportfolio.server.common.exception;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(String id) {
        super("Could not find tag " + id);
    }
}
