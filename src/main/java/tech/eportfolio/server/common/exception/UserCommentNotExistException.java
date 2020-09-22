package tech.eportfolio.server.common.exception;

public class UserCommentNotExistException extends RuntimeException {
    public UserCommentNotExistException(String username, String portfolioId) {
        super("User " + username + " did not comment " + portfolioId);
    }
}
