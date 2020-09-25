package tech.eportfolio.server.common.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String commentId) {
        super("Comment not found" + commentId);
    }
}
