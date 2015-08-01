package org.cryse.lkong.logic.restservice.exception;

public class UploadImageException extends RuntimeException {
    public UploadImageException() {
    }

    public UploadImageException(String detailMessage) {
        super(detailMessage);
    }

    public UploadImageException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public UploadImageException(Throwable throwable) {
        super(throwable);
    }
}
