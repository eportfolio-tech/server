package tech.eportfolio.server.common.exception;

public class EmailVerificationFailException extends RuntimeException {
    public EmailVerificationFailException() {
        super("Email Verification failed ");
    }
}
