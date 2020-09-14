package tech.eportfolio.server.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.common.jsend.FailResponse;

@RestControllerAdvice
class AccessDeniedExceptionHandler extends AccessDeniedHandlerImpl {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailResponse> exception(Exception ex) {
        return new FailResponse("authentication", ex.getMessage()).toForbidden();
    }
}