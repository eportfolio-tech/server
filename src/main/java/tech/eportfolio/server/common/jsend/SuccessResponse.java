package tech.eportfolio.server.common.jsend;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

@Data

public class SuccessResponse<T> {
    private String status = "success";
    private Map<String, T> data;

    public SuccessResponse() {
    }

    public SuccessResponse(Map<String, T> data) {
        this.data = data;
    }


    public SuccessResponse(String key, T data) {
        this.data = Collections.singletonMap(key, data);
    }

    public ResponseEntity<SuccessResponse<T>> toOk(HttpHeaders headers) {
        return new ResponseEntity<>(this, headers, HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<T>> toAccepted(HttpHeaders headers) {
        return new ResponseEntity<>(this, headers, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<SuccessResponse<T>> toCreated(HttpHeaders headers) {
        return new ResponseEntity<>(this, headers, HttpStatus.CREATED);
    }

    public ResponseEntity<SuccessResponse<T>> toOk() {
        return new ResponseEntity<>(this, null, HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse<T>> toAccepted() {
        return new ResponseEntity<>(this, null, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<SuccessResponse<T>> toCreated() {
        return new ResponseEntity<>(this, null, HttpStatus.CREATED);
    }

    public ResponseEntity<SuccessResponse<T>> toNoContent() {
        return new ResponseEntity<>(this, null, HttpStatus.NO_CONTENT);
    }

}
