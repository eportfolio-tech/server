package tech.eportfolio.server.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpResponse {
    private HttpStatus httpStatus;
    private String message;
    private int status;
    private long timestamp;
    private List<String> errors;
}
