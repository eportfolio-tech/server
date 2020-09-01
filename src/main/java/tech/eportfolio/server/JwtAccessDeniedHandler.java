package tech.eportfolio.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tech.eportfolio.server.security.SecurityConstant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpResponse customResponse = HttpResponse.builder().
                status(HttpStatus.UNAUTHORIZED.value()).
                httpStatus(HttpStatus.UNAUTHORIZED).
                message(HttpStatus.UNAUTHORIZED.getReasonPhrase().toLowerCase()).
                timeStamp(System.currentTimeMillis()).
                error(SecurityConstant.ACCESS_DENIED_MESSAGE).build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, customResponse);
        outputStream.flush();
    }
}
