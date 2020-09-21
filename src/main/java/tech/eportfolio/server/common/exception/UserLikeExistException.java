package tech.eportfolio.server.common.exception;

public class UserLikeExistException extends RuntimeException {
    public UserLikeExistException(String username, String portfolioId) {
        super("User " + username + " has already liked " + portfolioId);
    }
}
