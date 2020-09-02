package tech.eportfolio.server.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.exception.EmailAlreadyInUseException;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.exception.response.HttpResponse;

import java.util.Collections;
import java.util.stream.Collectors;

@RestControllerAdvice
public class UserErrorExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<HttpResponse> handleException(EmailAlreadyInUseException ex) {
        HttpResponse error = new HttpResponse();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setErrors(Collections.singletonList(ex.getMessage()));
        error.setMessage(ex.getMessage());
        error.setHttpStatus(HttpStatus.CONFLICT);
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<HttpResponse> handleException(UserNotFoundException ex) {
        HttpResponse error = new HttpResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(ex.getMessage());
        error.setHttpStatus(HttpStatus.NOT_FOUND);
        error.setErrors(Collections.singletonList(ex.getMessage()));
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<HttpResponse> handleException(MethodArgumentNotValidException ex) {
        HttpResponse error = new HttpResponse();
        error.setHttpStatus(HttpStatus.BAD_REQUEST);
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrors(ex.getBindingResult().getFieldErrors().stream().map(
                e -> e.getField() + e.getDefaultMessage()).collect(Collectors.toList()));
        error.setMessage("Parameter validation failed!");
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<HttpResponse> handleException(Exception ex) {
        HttpResponse error = new HttpResponse();
        error.setHttpStatus(HttpStatus.BAD_REQUEST);
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrors(Collections.singletonList(ex.getMessage()));
        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
