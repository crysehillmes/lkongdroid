package org.cryse.lkong.model;

import java.util.Date;

public class NoticeCountModel {
    private long userId;
    private Date updateTime;
    private int notice;
    private int mentionNotice;
    private int rateNotice;
    private int fansNotice;
    private int privateMessageNotice;

    private boolean success;
    private String errorMessage;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean hasNotification() {
        return notice > 0 || mentionNotice > 0 || rateNotice > 0 || privateMessageNotice > 0/* || fansNotice > 0*/;
    }
}
