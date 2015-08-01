package org.cryse.lkong.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.cryse.lkong.data.provider.followedforum.FollowedForumModel;
import org.cryse.lkong.model.SimpleCollectionItem;

public class FollowedForum implements FollowedForumModel, SimpleCollectionItem {
    private long userId;
    private long forumId;
    private String forumName;
    private String forumIcon;
    private long forumSortValue;

    public FollowedForum(long userId, long forumId, String forumName, String forumIcon, long forumSortValue) {
        this.userId = userId;
        this.forumId = forumId;
        this.forumName = forumName;
        this.forumIcon = forumIcon;
        this.forumSortValue = forumSortValue;
    }

    public FollowedForum(FollowedForumModel model) {
        this.userId = model.getUserId();
        this.forumId = model.getForumId();
        this.forumName = model.getForumName();
        this.forumIcon = model.getForumIcon();
        this.forumSortValue = model.getForumSortValue();
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public long getForumId() {
        return forumId;
    }

    @NonNull
    @Override
    public String getForumName() {
        return forumName;
    }

    @NonNull
    @Override
    public String getForumIcon() {
        return forumIcon;
    }

    @Override
    public long getForumSortValue() {
        return forumSortValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeLong(this.forumId);
        dest.writeString(this.forumName);
        dest.writeString(this.forumIcon);
        dest.writeLong(this.forumSortValue);
    }

    protected FollowedForum(Parcel in) {
        this.userId = in.readLong();
        this.forumId = in.readLong();
        this.forumName = in.readString();
        this.forumIcon = in.readString();
        this.forumSortValue = in.readLong();
    }

    public static final Parcelable.Creator<FollowedForum> CREATOR = new Parcelable.Creator<FollowedForum>() {
        public FollowedForum createFromParcel(Parcel source) {
            return new FollowedForum(source);
        }

        public FollowedForum[] newArray(int size) {
            return new FollowedForum[size];
        }
    };

    @Override
    public long getSortKey() {
        return  forumSortValue;
    }
}
