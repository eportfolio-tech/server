package tech.eportfolio.server.common.exception;

public class UserLikeNotExistException extends RuntimeException {
    public UserLikeNotExistException(String username, String portfolioId) {
        super("User " + username + " has not liked " + portfolioId);
    }
}
