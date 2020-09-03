package tech.eportfolio.server.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.exception.EmailExistException;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.exception.UsernameExistException;
import tech.eportfolio.server.exception.response.HttpResponse;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> handleException(EmailExistException ex) {
        HttpResponse error = new HttpResponse();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setErrors(Collections.singletonList(ex.getMessage()));
        error.setMessage(ex.getMessage());
        error.setHttpStatus(HttpStatus.CONFLICT);
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> handleException(UsernameExistException ex) {
        HttpResponse error = new HttpResponse();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setErrors(Collections.singletonList(ex.getMessage()));
        error.setMessage(ex.getMessage());
        error.setHttpStatus(HttpStatus.CONFLICT);
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> handleException(UserNotFoundException ex) {
        HttpResponse error = new HttpResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(ex.getMessage());
        error.setHttpStatus(HttpStatus.NOT_FOUND);
        error.setErrors(Collections.singletonList(ex.getMessage()));
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpResponse> handleException(MethodArgumentNotValidException ex) {
        HttpResponse error = new HttpResponse();
        error.setHttpStatus(HttpStatus.BAD_REQUEST);
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrors(ex.getBindingResult().getFieldErrors().stream().map(
                e -> String.format("%s %s", e.getField(), e.getDefaultMessage())).collect(Collectors.toList()));
        error.setMessage("Parameter validation failed");
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> handleException(AccessDeniedException ex) {
        HttpResponse error = new HttpResponse();
        error.setHttpStatus(HttpStatus.UNAUTHORIZED);
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setErrors(Collections.singletonList(ex.getMessage()));
        error.setMessage(ex.getMessage());
        error.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
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
