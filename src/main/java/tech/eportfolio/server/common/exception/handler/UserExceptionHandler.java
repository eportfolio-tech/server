package tech.eportfolio.server.common.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.common.exception.EmailExistException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.exception.UsernameExistException;
import tech.eportfolio.server.common.jsend.FailResponse;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {
    public static final String USER = "user";
    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<FailResponse> handleException(EmailExistException ex) {
        return new FailResponse(USER, ex.getMessage()).toConflict();
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<FailResponse> handleException(UsernameExistException ex) {
        return new FailResponse(USER, ex.getMessage()).toConflict();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<FailResponse> handleException(UserNotFoundException ex) {
        return new FailResponse(USER, ex.getMessage()).toNotFound();
    }
}
