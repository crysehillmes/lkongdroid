package org.cryse.lkong.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class TimelineModel implements Parcelable {
    private boolean isQuote;
    private long userId;
    private String userName;
    private Date dateline;
    private String message;
    private boolean isThread;
    private long tid;
    private String subject;
    private String threadAuthor;
    private long threadAuthorId;
    private int threadReplyCount;
    private String id;
    private long sortKey;
    private Date sortKeyDate;

    public boolean isQuote() {
        return isQuote;
    }

    public void setQuote(boolean isQuote) {
        this.isQuote = isQuote;
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

    public Date getDateline() {
        return dateline;
    }

    public void setDateline(Date dateline) {
        this.dateline = dateline;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isThread() {
        return isThread;
    }

    public void setThread(boolean isThread) {
        this.isThread = isThread;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getThreadAuthor() {
        return threadAuthor;
    }

    public void setThreadAuthor(String threadAuthor) {
        this.threadAuthor = threadAuthor;
    }

    public long getThreadAuthorId() {
        return threadAuthorId;
    }

    public void setThreadAuthorId(long threadAuthorId) {
        this.threadAuthorId = threadAuthorId;
    }

    public int getThreadReplyCount() {
        return threadReplyCount;
    }

    public void setThreadReplyCount(int threadReplyCount) {
        this.threadReplyCount = threadReplyCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
    }

    public Date getSortKeyDate() {
        return sortKeyDate;
    }

    public void setSortKeyDate(Date sortKeyDate) {
        this.sortKeyDate = sortKeyDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(isQuote ? (byte) 1 : (byte) 0);
        dest.writeLong(this.userId);
        dest.writeString(this.userName);
        dest.writeLong(dateline != null ? dateline.getTime() : -1);
        dest.writeString(this.message);
        dest.writeByte(isThread ? (byte) 1 : (byte) 0);
        dest.writeLong(this.tid);
        dest.writeString(this.subject);
        dest.writeString(this.threadAuthor);
        dest.writeLong(this.threadAuthorId);
        dest.writeInt(this.threadReplyCount);
        dest.writeString(this.id);
        dest.writeLong(this.sortKey);
        dest.writeLong(sortKeyDate != null ? sortKeyDate.getTime() : -1);
    }

    public TimelineModel() {
    }

    private TimelineModel(Parcel in) {
        this.isQuote = in.readByte() != 0;
        this.userId = in.readLong();
        this.userName = in.readString();
        long tmpDateline = in.readLong();
        this.dateline = tmpDateline == -1 ? null : new Date(tmpDateline);
        this.message = in.readString();
        this.isThread = in.readByte() != 0;
        this.tid = in.readLong();
        this.subject = in.readString();
        this.threadAuthor = in.readString();
        this.threadAuthorId = in.readLong();
        this.threadReplyCount = in.readInt();
        this.id = in.readString();
        this.sortKey = in.readLong();
        long tmpSortKeyDate = in.readLong();
        this.sortKeyDate = tmpSortKeyDate == -1 ? null : new Date(tmpSortKeyDate);
    }

    public static final Parcelable.Creator<TimelineModel> CREATOR = new Parcelable.Creator<TimelineModel>() {
        public TimelineModel createFromParcel(Parcel source) {
            return new TimelineModel(source);
        }

        public TimelineModel[] newArray(int size) {
            return new TimelineModel[size];
        }
    };
}
