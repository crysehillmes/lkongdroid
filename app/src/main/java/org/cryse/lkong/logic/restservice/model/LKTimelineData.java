package org.cryse.lkong.logic.restservice.model;

import java.util.List;

public class LKTimelineData {
    long nexttime;
    long curtime;
    List<LKTimelineItem> data;

    public long getNexttime() {
        return nexttime;
    }

    public void setNexttime(long nexttime) {
        this.nexttime = nexttime;
    }

    public long getCurtime() {
        return curtime;
    }

    public void setCurtime(long curtime) {
        this.curtime = curtime;
    }

    public List<LKTimelineItem> getData() {
        return data;
    }

    public void setData(List<LKTimelineItem> data) {
        this.data = data;
    }
}
