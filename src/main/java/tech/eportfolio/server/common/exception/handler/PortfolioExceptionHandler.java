package tech.eportfolio.server.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.common.exception.PortfolioExistException;
import tech.eportfolio.server.common.exception.PortfolioNotFoundException;
import tech.eportfolio.server.common.jsend.FailResponse;

@RestControllerAdvice
public class PortfolioExceptionHandler {
    private static final String PORTFOLIO = "portfolio";

    @ExceptionHandler(PortfolioNotFoundException.class)
    public ResponseEntity<FailResponse> handleException(PortfolioNotFoundException ex) {
        return new FailResponse(PORTFOLIO, ex.getMessage()).toNotFound();
    }

    @ExceptionHandler(PortfolioExistException.class)
    public ResponseEntity<FailResponse> handleException(PortfolioExistException ex) {
        return new FailResponse(PORTFOLIO, ex.getMessage()).toConflict();
    }
}
