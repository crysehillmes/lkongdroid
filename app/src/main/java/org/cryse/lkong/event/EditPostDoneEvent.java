package org.cryse.lkong.event;

import org.cryse.lkong.model.EditPostResult;

public class EditPostDoneEvent extends AbstractEvent {
    private EditPostResult editResult;

    public EditPostDoneEvent(EditPostResult editResult) {
        this.editResult = editResult;
    }

    public EditPostResult getPostResult() {
        return editResult;
    }

    public void setPostResult(EditPostResult editResult) {
        this.editResult = editResult;
    }
}
