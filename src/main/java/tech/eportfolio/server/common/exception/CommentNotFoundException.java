package tech.eportfolio.server.common.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String username, String portfolioId) {
        super("User " + username + " has not commented " + portfolioId);
    }
}
