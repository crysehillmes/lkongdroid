package org.cryse.lkong.logic.restservice.model;

public class LKCheckNoticeCountResult {
    private long time;
    private LKNoticeCount notice;
    private boolean ok;
    private String error;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public LKNoticeCount getNotice() {
        return notice;
    }

    public void setNotice(LKNoticeCount notice) {
        this.notice = notice;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
