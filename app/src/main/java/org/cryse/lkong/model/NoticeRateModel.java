package org.cryse.lkong.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class NoticeRateModel implements SimpleCollectionItem {
    private long sortKey;
    private long userId;
    private String userName;
    private String message;
    private String extCredits;
    private int score;
    private String reason;
    private Date dateline;
    private long pid;
    private String id;

    @Override
    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtCredits() {
        return extCredits;
    }

    public void setExtCredits(String extCredits) {
        this.extCredits = extCredits;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDateline() {
        return dateline;
    }

    public void setDateline(Date dateline) {
        this.dateline = dateline;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.sortKey);
        dest.writeLong(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.message);
        dest.writeString(this.extCredits);
        dest.writeInt(this.score);
        dest.writeString(this.reason);
        dest.writeLong(dateline != null ? dateline.getTime() : -1);
        dest.writeLong(this.pid);
        dest.writeString(this.id);
    }

    public NoticeRateModel() {
    }

    private NoticeRateModel(Parcel in) {
        this.sortKey = in.readLong();
        this.userId = in.readLong();
        this.userName = in.readString();
        this.message = in.readString();
        this.extCredits = in.readString();
        this.score = in.readInt();
        this.reason = in.readString();
        long tmpDateline = in.readLong();
        this.dateline = tmpDateline == -1 ? null : new Date(tmpDateline);
        this.pid = in.readLong();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<NoticeRateModel> CREATOR = new Parcelable.Creator<NoticeRateModel>() {
        public NoticeRateModel createFromParcel(Parcel source) {
            return new NoticeRateModel(source);
        }

        public NoticeRateModel[] newArray(int size) {
            return new NoticeRateModel[size];
        }
    };
}
