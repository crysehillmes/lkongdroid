package org.cryse.lkong.model;

import java.util.Date;

public class NoticeCountModel {
    private Date updateTime;
    private int notice;
    private int mentionNotice;
    private int rateNotice;
    private int fansNotice;
    private int privateMessageNotice;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getNotice() {
        return notice;
    }

    public void setNotice(int notice) {
        this.notice = notice;
    }

    public int getMentionNotice() {
        return mentionNotice;
    }

    public void setMentionNotice(int mentionNotice) {
        this.mentionNotice = mentionNotice;
    }

    public int getRateNotice() {
        return rateNotice;
    }

    public void setRateNotice(int rateNotice) {
        this.rateNotice = rateNotice;
    }

    public int getFansNotice() {
        return fansNotice;
    }

    public void setFansNotice(int fansNotice) {
        this.fansNotice = fansNotice;
    }

    public int getPrivateMessageNotice() {
        return privateMessageNotice;
    }

    public void setPrivateMessageNotice(int privateMessageNotice) {
        this.privateMessageNotice = privateMessageNotice;
    }
}
