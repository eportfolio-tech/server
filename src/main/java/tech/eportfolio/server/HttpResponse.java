package tech.eportfolio.server;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class HttpResponse {
    private long timeStamp;
    private int status;
    private HttpStatus httpStatus;
    private String message;
    private String error;
}
