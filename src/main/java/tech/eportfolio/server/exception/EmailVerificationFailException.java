package tech.eportfolio.server.exception;

public class EmailVerificationFailException extends RuntimeException {
    public EmailVerificationFailException() {
        super("Email Verification failed ");
    }
}
