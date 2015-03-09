package org.cryse.lkong.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ForumModel implements Serializable, SimpleCollectionItem {
    private long fid;
    private String name;
    private String icon;
    private String description;
    private String status;
    private int sortByDateline;
    private int threads;
    private int todayPosts;
    private int fansNum;
    private String blackboard;
    private String[] moderators;

    public ForumModel() {
    }

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSortByDateline() {
        return sortByDateline;
    }

    public void setSortByDateline(int sortByDateline) {
        this.sortByDateline = sortByDateline;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getTodayPosts() {
        return todayPosts;
    }

    public void setTodayPosts(int todayPosts) {
        this.todayPosts = todayPosts;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public String getBlackboard() {
        return blackboard;
    }

    public void setBlackboard(String blackboard) {
        this.blackboard = blackboard;
    }

    public String[] getModerators() {
        return moderators;
    }

    public void setModerators(String[] moderators) {
        this.moderators = moderators;
    }

    @Override
    public long getSortKey() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.fid);
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeString(this.description);
        dest.writeString(this.status);
        dest.writeInt(this.sortByDateline);
        dest.writeInt(this.threads);
        dest.writeInt(this.todayPosts);
        dest.writeInt(this.fansNum);
        dest.writeString(this.blackboard);
        dest.writeStringArray(this.moderators);
    }

    private ForumModel(Parcel in) {
        this.fid = in.readLong();
        this.name = in.readString();
        this.icon = in.readString();
        this.description = in.readString();
        this.status = in.readString();
        this.sortByDateline = in.readInt();
        this.threads = in.readInt();
        this.todayPosts = in.readInt();
        this.fansNum = in.readInt();
        this.blackboard = in.readString();
        this.moderators = in.createStringArray();
    }

    public static final Parcelable.Creator<ForumModel> CREATOR = new Parcelable.Creator<ForumModel>() {
        public ForumModel createFromParcel(Parcel source) {
            return new ForumModel(source);
        }

        public ForumModel[] newArray(int size) {
            return new ForumModel[size];
        }
    };
}
