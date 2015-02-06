package org.cryse.lkong.logic.restservice.model;

import java.util.List;

public class LKForumThreadList {
    private long nexttime;
    private List<LKForumThreadItem> data;

    public List<LKForumThreadItem> getData() {
        return data;
    }

    public void setData(List<LKForumThreadItem> data) {
        this.data = data;
    }

    public long getNexttime() {
        return nexttime;
    }

    public void setNexttime(long nexttime) {
        this.nexttime = nexttime;
    }
}
