package org.cryse.lkong.logic.restservice.exception;

public class NeedSignInException extends RuntimeException {
    public NeedSignInException() {
    }

    public NeedSignInException(String detailMessage) {
        super(detailMessage);
    }

    public NeedSignInException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NeedSignInException(Throwable throwable) {
        super(throwable);
    }
}
