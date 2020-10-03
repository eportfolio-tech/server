package tech.eportfolio.server.common.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.common.exception.UserFollowExistException;
import tech.eportfolio.server.common.exception.UserFollowNotExistException;
import tech.eportfolio.server.common.jsend.FailResponse;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserFollowExceptionHandler {
    public static final String FOLLOW = "follow";

    @ExceptionHandler(UserFollowExistException.class)
    public ResponseEntity<FailResponse> handleException(UserFollowExistException ex) {
        return new FailResponse(FOLLOW, ex.getMessage()).toConflict();
    }

    @ExceptionHandler(UserFollowNotExistException.class)
    public ResponseEntity<FailResponse> handleException(UserFollowNotExistException ex) {
        return new FailResponse(FOLLOW, ex.getMessage()).toNotFound();
    }
}
