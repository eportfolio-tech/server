package tech.eportfolio.server.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tech.eportfolio.server.common.jsend.ErrorResponse;
import tech.eportfolio.server.common.jsend.FailResponse;

@RestControllerAdvice
public class CatchAllExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception ex) {
        return new ErrorResponse(ex.getMessage()).toInternalError();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<FailResponse> handleException(HttpMessageNotReadableException ex) {
        return new FailResponse("request", ex.getMessage()).toBadRequest();
    }
}