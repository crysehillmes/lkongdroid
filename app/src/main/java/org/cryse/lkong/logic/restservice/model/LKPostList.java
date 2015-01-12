package org.cryse.lkong.logic.restservice.model;

import java.util.List;

public class LKPostList {
    private String model;
    private int replies;
    private int page;
    private List<LKPostItem> data;
    private long curtime;
    private long nexttime;
    private int isend;
    private long loadtime;
    private String tmp;


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<LKPostItem> getData() {
        return data;
    }

    public void setData(List<LKPostItem> data) {
        this.data = data;
    }

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

    public int getIsend() {
        return isend;
    }

    public void setIsend(int isend) {
        this.isend = isend;
    }

    public long getLoadtime() {
        return loadtime;
    }

    public void setLoadtime(long loadtime) {
        this.loadtime = loadtime;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }
}
