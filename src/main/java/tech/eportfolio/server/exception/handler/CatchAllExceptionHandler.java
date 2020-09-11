package tech.eportfolio.server.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tech.eportfolio.server.exception.response.HttpResponse;

import java.util.Collections;

@RestControllerAdvice
public class CatchAllExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception ex) {
        HttpResponse error = new HttpResponse();
        error.setHttpStatus(HttpStatus.BAD_REQUEST);
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrors(Collections.singletonList(ex.getMessage()));
        error.setMessage(ex.getMessage());
        error.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}