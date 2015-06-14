package org.cryse.lkong.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.cryse.lkong.model.SimpleCollectionItem;

import java.io.Serializable;

public class PinnedForumEntity implements Serializable, SimpleCollectionItem {
    private long forumId;
    private long userId;
    private String forumName;
    private String forumIcon;

    public PinnedForumEntity(long forumId, long userId, String forumName, String forumIcon, long sortValue) {
        this.forumId = forumId;
        this.userId = userId;
        this.forumName = forumName;
        this.forumIcon = forumIcon;
        this.sortValue = sortValue;
    }

    private long sortValue;

    public long getForumId() {
        return forumId;
    }

    public void setForumId(long forumId) {
        this.forumId = forumId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }

    public String getForumIcon() {
        return forumIcon;
    }

    public void setForumIcon(String forumIcon) {
        this.forumIcon = forumIcon;
    }

    public long getSortValue() {
        return sortValue;
    }

    @Override
    public long getSortKey() {
        return sortValue;
    }

    public void setSortValue(long sortValue) {
        this.sortValue = sortValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.forumId);
        dest.writeLong(this.userId);
        dest.writeString(this.forumName);
        dest.writeString(this.forumIcon);
        dest.writeLong(this.sortValue);
    }

    protected PinnedForumEntity(Parcel in) {
        this.forumId = in.readLong();
        this.userId = in.readLong();
        this.forumName = in.readString();
        this.forumIcon = in.readString();
        this.sortValue = in.readLong();
    }

    public static final Parcelable.Creator<PinnedForumEntity> CREATOR = new Parcelable.Creator<PinnedForumEntity>() {
        public PinnedForumEntity createFromParcel(Parcel source) {
            return new PinnedForumEntity(source);
        }

        public PinnedForumEntity[] newArray(int size) {
            return new PinnedForumEntity[size];
        }
    };
}
