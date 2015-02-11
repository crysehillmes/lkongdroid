package org.cryse.lkong.logic.restservice.model;

import java.util.List;

public class LKNoticeResult {
    private long curtime;
    private long nexttime;
    private List<LKNoticeItem> data;

    public long getCurtime() {
        return curtime;
    }

    public void setCurtime(long curtime) {
        this.curtime = curtime;
    }

    public long getNexttime() {
        return nexttime;
    }

    public void setNexttime(long nexttime) {
        this.nexttime = nexttime;
    }

    public List<LKNoticeItem> getData() {
        return data;
    }

    public void setData(List<LKNoticeItem> data) {
        this.data = data;
    }
}
