package tech.eportfolio.server.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import tech.eportfolio.server.security.JwtAccessDeniedHandler;
import tech.eportfolio.server.security.SecurityConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * This class defines an interceptor which validate if username in path variable matches with  username in JWT
 *
 * @author Haswell
 */
@Component
public class PathUsernameInterceptor extends HandlerInterceptorAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // Extract path variable `username` from request if it exists
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables == null || pathVariables.isEmpty()) {
            return true;
        }
        String usernameInPath = pathVariables.get("username");
        // Compare username extracted from path with username in security context
        if (StringUtils.isNotEmpty(usernameInPath) && !StringUtils.equals(SecurityContextHolder.getContext().
                getAuthentication().getName(), usernameInPath)) {
            // create a JwtAccessDeniedHandler to response properly. This is a workaround of not being able to use
            // exceptionHandler defined in @ControllerAdvice
            // See https://stackoverflow.com/questions/22062311/how-to-use-exceptionhandler-in-spring-interceptor
            JwtAccessDeniedHandler jwtAccessDeniedHandler = new JwtAccessDeniedHandler();
            LOGGER.info("PathUsernameInterceptor failed: {} in JWT mismatch with {} in path", SecurityContextHolder.getContext().
                    getAuthentication().getName(), usernameInPath);
            jwtAccessDeniedHandler.handle(request, response, new AccessDeniedException(SecurityConstant.ACCESS_DENIED_MESSAGE));
            return false;
        }
        return true;
    }
}
