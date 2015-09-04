package org.cryse.lkong.service.task;

import org.cryse.lkong.account.LKAuthObject;

public class EditPostTask extends SendTask {
    LKAuthObject authObject;
    long tid;
    long pid;
    String action;
    String title;
    String content;

    public LKAuthObject getAuthObject() {
        return authObject;
    }

    public void setAuthObject(LKAuthObject authObject) {
        this.authObject = authObject;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
