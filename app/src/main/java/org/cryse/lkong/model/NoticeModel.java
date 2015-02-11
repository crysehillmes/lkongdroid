package org.cryse.lkong.model;

import java.util.Date;

public class NoticeModel {
    long userId;
    String userName;
    String noticeNote;
    long noticeId;
    long sortkey;
    Date dateline;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNoticeNote() {
        return noticeNote;
    }

    public void setNoticeNote(String noticeNote) {
        this.noticeNote = noticeNote;
    }

    public long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(long noticeId) {
        this.noticeId = noticeId;
    }

    public long getSortkey() {
        return sortkey;
    }

    public void setSortkey(long sortkey) {
        this.sortkey = sortkey;
    }

    public Date getDateline() {
        return dateline;
    }

    public void setDateline(Date dateline) {
        this.dateline = dateline;
    }
}
