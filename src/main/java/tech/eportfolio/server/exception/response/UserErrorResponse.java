package tech.eportfolio.server.exception.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserErrorResponse {
    private int status;
    private String message;
    private long timeStamp;
    private List<String> errors;
}
