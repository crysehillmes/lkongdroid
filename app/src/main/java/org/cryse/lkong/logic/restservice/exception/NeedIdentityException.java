package org.cryse.lkong.logic.restservice.exception;

public class NeedIdentityException extends RuntimeException {
    public NeedIdentityException() {
    }

    public NeedIdentityException(String detailMessage) {
        super(detailMessage);
    }

    public NeedIdentityException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NeedIdentityException(Throwable throwable) {
        super(throwable);
    }
}
