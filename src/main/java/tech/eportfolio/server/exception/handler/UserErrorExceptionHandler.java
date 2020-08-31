package tech.eportfolio.server.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tech.eportfolio.server.exception.EmailAlreadyInUseException;
import tech.eportfolio.server.exception.EmailNotValidException;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.exception.response.UserErrorResponse;

@ControllerAdvice
public class UserErrorExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleException(EmailAlreadyInUseException ex) {
        UserErrorResponse error = new UserErrorResponse();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleException(EmailNotValidException ex) {
        UserErrorResponse error = new UserErrorResponse();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleException(UserNotFoundException ex) {
        UserErrorResponse error = new UserErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
