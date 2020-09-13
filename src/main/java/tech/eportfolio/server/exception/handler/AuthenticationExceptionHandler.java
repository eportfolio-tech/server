package tech.eportfolio.server.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.common.jsend.ErrorResponse;
import tech.eportfolio.server.common.jsend.FailResponse;
import tech.eportfolio.server.exception.EmailExistException;
import tech.eportfolio.server.exception.UserNotFoundException;
import tech.eportfolio.server.exception.UsernameExistException;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

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
        return new FailResponse("authorization", ex.getMessage()).toUnauthorised();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FailResponse> handleException(MethodArgumentNotValidException ex) {
        FailResponse failResponse = new FailResponse();
        failResponse.setData(ex.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,
                FieldError::getDefaultMessage)));
        return failResponse.toBadRequest();
    }


}
