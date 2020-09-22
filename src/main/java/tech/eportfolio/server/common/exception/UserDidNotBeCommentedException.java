package tech.eportfolio.server.common.exception;

public class UserDidNotBeCommentedException extends RuntimeException {
    public UserDidNotBeCommentedException(String username, String commentId) {
        super("User " + username + " did not be commented on " + commentId);
    }
}
