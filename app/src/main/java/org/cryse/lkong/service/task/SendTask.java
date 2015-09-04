package org.cryse.lkong.service.task;

import org.cryse.lkong.account.LKAuthObject;

public abstract class SendTask {
    protected LKAuthObject authObject;

    public LKAuthObject getAuthObject() {
        return authObject;
    }

    public void setAuthObject(LKAuthObject authObject) {
        this.authObject = authObject;
    }
}
