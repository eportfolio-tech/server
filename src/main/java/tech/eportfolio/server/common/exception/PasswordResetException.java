package tech.eportfolio.server.common.exception;

public class PasswordResetException extends RuntimeException {
    public PasswordResetException(String user) {
        super("Failed to reset password for user" + user);
    }

    public PasswordResetException(String user, String reason) {
        super("Failed to reset password for user" + user + reason);
    }
}
