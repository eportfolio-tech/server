package tech.eportfolio.server.common.jsend;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

@Data
@NoArgsConstructor

public class FailResponse {
    private String status = "fail";
    private Map<String, String> data;


    public FailResponse(String key, String message) {
        this.data = Collections.singletonMap(key, message);
    }

    public ResponseEntity<FailResponse> toNotFound() {
        return new ResponseEntity<>(this, null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<FailResponse> toBadRequest() {
        return new ResponseEntity<>(this, null, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<FailResponse> toConflict() {
        return new ResponseEntity<>(this, null, HttpStatus.CONFLICT);
    }

    public ResponseEntity<FailResponse> toUnauthorised() {
        return new ResponseEntity<>(this, null, HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<FailResponse> toForbidden() {
        return new ResponseEntity<>(this, null, HttpStatus.FORBIDDEN);
    }

}


