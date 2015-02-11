package org.cryse.lkong.logic.restservice.model;

import java.util.List;

public class LKNoticeRateResult {
    private long curtime;
    private long nexttime;
    private List<LKNoticeRateItem> data;

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

    public List<LKNoticeRateItem> getData() {
        return data;
    }

    public void setData(List<LKNoticeRateItem> data) {
        this.data = data;
    }
}
