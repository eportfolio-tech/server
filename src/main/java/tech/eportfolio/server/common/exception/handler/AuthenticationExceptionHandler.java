package tech.eportfolio.server.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.eportfolio.server.common.exception.EmailExistException;
import tech.eportfolio.server.common.exception.PortfolioNotFoundException;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.exception.UsernameExistException;
import tech.eportfolio.server.common.jsend.ErrorResponse;
import tech.eportfolio.server.common.jsend.FailResponse;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    public static final String AUTHENTICATION = "authentication";

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<FailResponse> handleException(AuthenticationException ex) {
        return new FailResponse(AUTHENTICATION, ex.getMessage()).toUnauthorised();
    }
}
