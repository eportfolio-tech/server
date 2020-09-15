package tech.eportfolio.server.common.exception;

public class UploadBlobFailedException extends RuntimeException {
    public UploadBlobFailedException(String message) {
        super(message);
    }

    public UploadBlobFailedException() {
    }
}
