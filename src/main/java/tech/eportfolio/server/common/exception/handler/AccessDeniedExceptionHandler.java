package tech.eportfolio.server.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.common.jsend.FailResponse;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
class AccessDeniedExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<FailResponse> exception(Exception ex) {
        return new FailResponse("authentication", ex.getMessage()).toForbidden();
    }
}