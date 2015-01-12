package org.cryse.lkong.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ThreadInfoModel implements Parcelable {
    private long fid;
    private long tid;
    private String subject;
    private int views;
    private int replies;
    private String forumName;
    private boolean digest;
    private Date timeStamp;
    private Long uid;
    private String userName;
    private long authorId;
    private String authorName;
    private Date dateline;
    private String id;

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
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

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }

    public boolean isDigest() {
        return digest;
    }

    public void setDigest(boolean digest) {
        this.digest = digest;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getDateline() {
        return dateline;
    }

    public void setDateline(Date dateline) {
        this.dateline = dateline;
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
        dest.writeLong(this.fid);
        dest.writeLong(this.tid);
        dest.writeString(this.subject);
        dest.writeInt(this.views);
        dest.writeInt(this.replies);
        dest.writeString(this.forumName);
        dest.writeByte(digest ? (byte) 1 : (byte) 0);
        dest.writeLong(timeStamp != null ? timeStamp.getTime() : -1);
        dest.writeValue(this.uid);
        dest.writeString(this.userName);
        dest.writeLong(this.authorId);
        dest.writeString(this.authorName);
        dest.writeLong(dateline != null ? dateline.getTime() : -1);
        dest.writeString(this.id);
    }

    public ThreadInfoModel() {
    }

    private ThreadInfoModel(Parcel in) {
        this.fid = in.readLong();
        this.tid = in.readLong();
        this.subject = in.readString();
        this.views = in.readInt();
        this.replies = in.readInt();
        this.forumName = in.readString();
        this.digest = in.readByte() != 0;
        long tmpTimeStamp = in.readLong();
        this.timeStamp = tmpTimeStamp == -1 ? null : new Date(tmpTimeStamp);
        this.uid = (Long) in.readValue(Long.class.getClassLoader());
        this.userName = in.readString();
        this.authorId = in.readLong();
        this.authorName = in.readString();
        long tmpDateline = in.readLong();
        this.dateline = tmpDateline == -1 ? null : new Date(tmpDateline);
        this.id = in.readString();
    }

    public static final Parcelable.Creator<ThreadInfoModel> CREATOR = new Parcelable.Creator<ThreadInfoModel>() {
        public ThreadInfoModel createFromParcel(Parcel source) {
            return new ThreadInfoModel(source);
        }

        public ThreadInfoModel[] newArray(int size) {
            return new ThreadInfoModel[size];
        }
    };
}
