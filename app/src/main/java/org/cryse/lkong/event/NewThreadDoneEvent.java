package org.cryse.lkong.event;

import org.cryse.lkong.model.NewThreadResult;

public class NewThreadDoneEvent implements AbstractEvent {
    private NewThreadResult newThreadResult;

    public NewThreadDoneEvent(NewThreadResult newThreadResult) {
        this.newThreadResult = newThreadResult;
    }

    public NewThreadResult getNewThreadResult() {
        return newThreadResult;
    }

    public void setNewThreadResult(NewThreadResult newThreadResult) {
        this.newThreadResult = newThreadResult;
    }
}
