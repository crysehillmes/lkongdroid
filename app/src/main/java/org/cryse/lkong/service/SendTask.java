package org.cryse.lkong.service;

import org.cryse.lkong.utils.LKAuthObject;

public abstract class SendTask {
    protected LKAuthObject authObject;

    public LKAuthObject getAuthObject() {
        return authObject;
    }

    public void setAuthObject(LKAuthObject authObject) {
        this.authObject = authObject;
    }
}
