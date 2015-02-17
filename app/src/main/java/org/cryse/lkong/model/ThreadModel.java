package org.cryse.lkong.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

public class ThreadModel implements Serializable, SimpleCollectionItem {
    private long sortKey;
    private Date sortKeyTime;
    private Date dateline;
    private String subject;
    private String userName;
    private boolean digest;
    private int closed;
    private long uid;
    private int replyCount;
    private String id;
    private long fid;
    private String userIcon;

    @Override
    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
    }

    public Date getSortKeyTime() {
        return sortKeyTime;
    }

    public void setSortKeyTime(Date sortKeyTime) {
        this.sortKeyTime = sortKeyTime;
    }

    public Date getDateline() {
        return dateline;
    }

    public void setDateline(Date dateline) {
        this.dateline = dateline;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isDigest() {
        return digest;
    }

    public void setDigest(boolean digest) {
        this.digest = digest;
    }

    public int getClosed() {
        return closed;
    }

    public void setClosed(int closed) {
        this.closed = closed;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public ThreadModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.sortKey);
        dest.writeLong(sortKeyTime != null ? sortKeyTime.getTime() : -1);
        dest.writeLong(dateline != null ? dateline.getTime() : -1);
        dest.writeString(this.subject);
        dest.writeString(this.userName);
        dest.writeByte(digest ? (byte) 1 : (byte) 0);
        dest.writeInt(this.closed);
        dest.writeLong(this.uid);
        dest.writeInt(this.replyCount);
        dest.writeString(this.id);
        dest.writeLong(this.fid);
        dest.writeString(this.userIcon);
    }

    private ThreadModel(Parcel in) {
        this.sortKey = in.readLong();
        long tmpSortKeyTime = in.readLong();
        this.sortKeyTime = tmpSortKeyTime == -1 ? null : new Date(tmpSortKeyTime);
        long tmpDateline = in.readLong();
        this.dateline = tmpDateline == -1 ? null : new Date(tmpDateline);
        this.subject = in.readString();
        this.userName = in.readString();
        this.digest = in.readByte() != 0;
        this.closed = in.readInt();
        this.uid = in.readLong();
        this.replyCount = in.readInt();
        this.id = in.readString();
        this.fid = in.readLong();
        this.userIcon = in.readString();
    }

    public static final Creator<ThreadModel> CREATOR = new Creator<ThreadModel>() {
        public ThreadModel createFromParcel(Parcel source) {
            return new ThreadModel(source);
        }

        public ThreadModel[] newArray(int size) {
            return new ThreadModel[size];
        }
    };
}
