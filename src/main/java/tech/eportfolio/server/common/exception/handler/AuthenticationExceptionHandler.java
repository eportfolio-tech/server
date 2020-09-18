package tech.eportfolio.server.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.common.exception.EmailExistException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.exception.UsernameExistException;
import tech.eportfolio.server.common.jsend.ErrorResponse;
import tech.eportfolio.server.common.jsend.FailResponse;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<FailResponse> handleException(EmailExistException ex) {
        return new FailResponse(EmailExistException.SUBJECT, ex.getMessage()).toConflict();
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<FailResponse> handleException(UsernameExistException ex) {
        return new FailResponse(UsernameExistException.SUBJECT, ex.getMessage()).toConflict();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException ex) {
        return new ErrorResponse(ex.getMessage()).toNotFound();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<FailResponse> handleException(AccessDeniedException ex) {
        return new FailResponse("authentication", ex.getMessage()).toUnauthorised();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<FailResponse> handleException(AuthenticationException ex) {
        return new FailResponse("authentication", ex.getMessage()).toUnauthorised();
    }




}
