package tech.eportfolio.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tech.eportfolio.server.exception.response.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        HttpResponse customResponse = HttpResponse.builder().
                status(HttpStatus.UNAUTHORIZED.value()).
                httpStatus(HttpStatus.UNAUTHORIZED).
                message(HttpStatus.UNAUTHORIZED.getReasonPhrase().toLowerCase()).
                timeStamp(System.currentTimeMillis()).
                errors(Collections.singletonList(SecurityConstant.ACCESS_DENIED_MESSAGE)).build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, customResponse);
        outputStream.flush();
    }
}
