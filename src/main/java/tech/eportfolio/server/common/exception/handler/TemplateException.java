package tech.eportfolio.server.common.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.common.exception.TemplateNotFoundException;
import tech.eportfolio.server.common.exception.TitleAlreadyExistException;
import tech.eportfolio.server.common.jsend.FailResponse;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TemplateException {
    @ExceptionHandler(TitleAlreadyExistException.class)
    public ResponseEntity<FailResponse> handleException(TitleAlreadyExistException ex) {
        return new FailResponse("title", ex.getMessage()).toConflict();
    }

    @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<FailResponse> handleException(TemplateNotFoundException ex) {
        return new FailResponse("template", ex.getMessage()).toNotFound();
    }

}
