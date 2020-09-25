package tech.eportfolio.server.common.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.common.exception.CommentNotFoundException;
import tech.eportfolio.server.common.exception.PortfolioExistException;
import tech.eportfolio.server.common.exception.PortfolioNotFoundException;
import tech.eportfolio.server.common.exception.UserLikeNotExistException;
import tech.eportfolio.server.common.jsend.FailResponse;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PortfolioExceptionHandler {
    private static final String PORTFOLIO = "portfolio";
    private static final String COMMENT = "comment";
    private static final String LIKE = "like";


    @ExceptionHandler(PortfolioNotFoundException.class)
    public ResponseEntity<FailResponse> handleException(PortfolioNotFoundException ex) {
        return new FailResponse(PORTFOLIO, ex.getMessage()).toNotFound();
    }

    @ExceptionHandler(PortfolioExistException.class)
    public ResponseEntity<FailResponse> handleException(PortfolioExistException ex) {
        return new FailResponse(PORTFOLIO, ex.getMessage()).toConflict();
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<FailResponse> handleException(CommentNotFoundException ex) {
        return new FailResponse(COMMENT, ex.getMessage()).toNotFound();
    }

    @ExceptionHandler(UserLikeNotExistException.class)
    public ResponseEntity<FailResponse> handleException(UserLikeNotExistException ex) {
        return new FailResponse(LIKE, ex.getMessage()).toNotFound();
    }
}
