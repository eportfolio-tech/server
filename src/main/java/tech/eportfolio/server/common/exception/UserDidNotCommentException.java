package tech.eportfolio.server.common.exception;

public class UserDidNotCommentException extends RuntimeException {
    public UserDidNotCommentException(String username, String commentId) {
        super("User " + username + " did not comment " + commentId);
    }
}
