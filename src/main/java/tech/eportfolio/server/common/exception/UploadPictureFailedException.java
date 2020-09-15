package tech.eportfolio.server.common.exception;

public class UploadPictureFailedException extends RuntimeException {
    public UploadPictureFailedException(String message) {
        super(message);
    }

    public UploadPictureFailedException() {
    }
}
