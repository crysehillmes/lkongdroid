package org.cryse.lkong.logic.restservice.model;

public class LKCheckNoticeCountResult {
    private long time;
    private LKNoticeCount notice;

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
}
