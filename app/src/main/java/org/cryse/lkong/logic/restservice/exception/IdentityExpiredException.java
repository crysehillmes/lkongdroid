package org.cryse.lkong.logic.restservice.exception;

public class IdentityExpiredException extends RuntimeException {
    public IdentityExpiredException() {
    }

    public IdentityExpiredException(String detailMessage) {
        super(detailMessage);
    }

    public IdentityExpiredException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public IdentityExpiredException(Throwable throwable) {
        super(throwable);
    }
}
