package org.cryse.lkong.model;

import android.os.Parcel;

import java.util.Date;

public class NoticeModel implements SimpleCollectionItem {
    long userId;
    String userName;
    String noticeNote;
    long noticeId;
    long sortKey;
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

    @Override
    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
    }

    public Date getDateline() {
        return dateline;
    }

    public void setDateline(Date dateline) {
        this.dateline = dateline;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.noticeNote);
        dest.writeLong(this.noticeId);
        dest.writeLong(this.sortKey);
        dest.writeLong(dateline != null ? dateline.getTime() : -1);
    }

    public NoticeModel() {
    }

    private NoticeModel(Parcel in) {
        this.userId = in.readLong();
        this.userName = in.readString();
        this.noticeNote = in.readString();
        this.noticeId = in.readLong();
        this.sortKey = in.readLong();
        long tmpDateline = in.readLong();
        this.dateline = tmpDateline == -1 ? null : new Date(tmpDateline);
    }

    public static final Creator<NoticeModel> CREATOR = new Creator<NoticeModel>() {
        public NoticeModel createFromParcel(Parcel source) {
            return new NoticeModel(source);
        }

        public NoticeModel[] newArray(int size) {
            return new NoticeModel[size];
        }
    };
}
