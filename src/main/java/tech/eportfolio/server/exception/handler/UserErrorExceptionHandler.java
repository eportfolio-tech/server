package tech.eportfolio.server.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.exception.EmailAlreadyInUseException;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.exception.response.UserErrorResponse;

import java.util.Collections;
import java.util.stream.Collectors;

@RestControllerAdvice
public class UserErrorExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleException(EmailAlreadyInUseException ex) {
        UserErrorResponse error = new UserErrorResponse();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setErrors(Collections.singletonList(ex.getMessage()));
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

    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleException(MethodArgumentNotValidException ex) {
        UserErrorResponse error = new UserErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrors(ex.getBindingResult().getFieldErrors().stream().map(
                e -> e.getField() + e.getDefaultMessage()).collect(Collectors.toList()));
        error.setMessage("Parameter validation failed!");
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<UserErrorResponse> handleException(Exception ex) {
        UserErrorResponse error = new UserErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
