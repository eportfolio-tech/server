package tech.eportfolio.server.common.jsend;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Data

public class ErrorResponse {
    private String status = "error";
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ResponseEntity<ErrorResponse> toNotFound() {
        return new ResponseEntity<>(this, null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<ErrorResponse> toInternalError() {
        return new ResponseEntity<>(this, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
