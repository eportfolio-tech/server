package tech.eportfolio.server.exception;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(Long id) {
        super("Could not find tag " + id);
    }
}
