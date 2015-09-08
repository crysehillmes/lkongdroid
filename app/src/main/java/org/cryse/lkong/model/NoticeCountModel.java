package org.cryse.lkong.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class NoticeCountModel implements Parcelable {
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

    public int getAllNoticeCount() {
        return notice + mentionNotice + rateNotice + privateMessageNotice /* + fansNotice*/;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeLong(updateTime != null ? updateTime.getTime() : -1);
        dest.writeInt(this.notice);
        dest.writeInt(this.mentionNotice);
        dest.writeInt(this.rateNotice);
        dest.writeInt(this.fansNotice);
        dest.writeInt(this.privateMessageNotice);
        dest.writeByte(success ? (byte) 1 : (byte) 0);
        dest.writeString(this.errorMessage);
    }

    public NoticeCountModel() {
    }

    protected NoticeCountModel(Parcel in) {
        this.userId = in.readLong();
        long tmpUpdateTime = in.readLong();
        this.updateTime = tmpUpdateTime == -1 ? null : new Date(tmpUpdateTime);
        this.notice = in.readInt();
        this.mentionNotice = in.readInt();
        this.rateNotice = in.readInt();
        this.fansNotice = in.readInt();
        this.privateMessageNotice = in.readInt();
        this.success = in.readByte() != 0;
        this.errorMessage = in.readString();
    }

    public static final Parcelable.Creator<NoticeCountModel> CREATOR = new Parcelable.Creator<NoticeCountModel>() {
        public NoticeCountModel createFromParcel(Parcel source) {
            return new NoticeCountModel(source);
        }

        public NoticeCountModel[] newArray(int size) {
            return new NoticeCountModel[size];
        }
    };
}
