package tech.eportfolio.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;
import tech.eportfolio.server.exception.response.HttpResponse;
import tech.eportfolio.server.security.SecurityConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author haswell
 */
@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2) throws IOException {
        HttpResponse customResponse = HttpResponse.builder().
                status(HttpStatus.FORBIDDEN.value()).
                httpStatus(HttpStatus.FORBIDDEN).
                message(HttpStatus.FORBIDDEN.getReasonPhrase().toLowerCase()).
                error(SecurityConstant.FORBIDDEN_MESSAGE).
                timeStamp(System.currentTimeMillis()).build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, customResponse);
        outputStream.flush();
    }
}
