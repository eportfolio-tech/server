package tech.eportfolio.server.common.exception;

public class UserVerificationException extends RuntimeException {
    public static final String PROMPT = "User already verified: ";

    public UserVerificationException(Long id) {
        super(PROMPT + id);
    }

    public UserVerificationException(String username) {
        super(PROMPT + username);
    }
}
