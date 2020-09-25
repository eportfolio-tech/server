package tech.eportfolio.server.common.exception.handler;

import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.common.jsend.FailResponse;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    public static final String AUTHENTICATION = "authentication";

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<FailResponse> handleException(AuthenticationException ex) {
        return new FailResponse(AUTHENTICATION, ex.getMessage()).toUnauthorised();
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<FailResponse> handleException(TokenExpiredException ex) {
        return new FailResponse(AUTHENTICATION, ex.getMessage()).toUnauthorised();
    }

}
