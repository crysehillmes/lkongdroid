package org.cryse.lkong.model;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.lkong.data.provider.browsehistory.BrowseHistoryCursor;
import org.cryse.lkong.data.provider.browsehistory.BrowseHistoryModel;

import java.util.Date;

public class BrowseHistory implements BrowseHistoryModel, SimpleCollectionItem {
    long userId;
    long threadId;
    String threadTitle;
    Long forumId;
    Long postId;
    String forumTitle;
    long authorId;
    String authorName;
    Date lastReadTime;

    public BrowseHistory(long userId, long threadId, String threadTitle, Long forumId, Long postId, String forumTitle, long authorId, String authorName, Date lastReadTime) {
        this.userId = userId;
        this.threadId = threadId;
        this.threadTitle = threadTitle;
        this.forumId = forumId;
        this.postId = postId;
        this.forumTitle = forumTitle;
        this.authorId = authorId;
        this.authorName = authorName;
        this.lastReadTime = lastReadTime;
    }
    public BrowseHistory(BrowseHistoryCursor cursor) {
        this.userId = cursor.getUserId();
        this.threadId = cursor.getThreadId();
        this.threadTitle = cursor.getThreadTitle();
        this.forumId = cursor.getForumId();
        this.postId = cursor.getPostId();
        this.forumTitle = cursor.getForumTitle();
        this.authorId = cursor.getThreadAuthorId();
        this.authorName = cursor.getThreadAuthorName();
        this.lastReadTime = new Date(cursor.getLastReadTime());
    }
    @Override
    public long getUserId() {
        return userId;
    }

    @Nullable
    @Override
    public Long getForumId() {
        return forumId;
    }

    @Nullable
    @Override
    public String getForumTitle() {
        return forumTitle;
    }

    @Override
    public long getThreadId() {
        return threadId;
    }

    @Nullable
    @Override
    public Long getPostId() {
        return postId;
    }

    @NonNull
    @Override
    public String getThreadTitle() {
        return threadTitle;
    }

    @Override
    public long getThreadAuthorId() {
        return authorId;
    }

    @NonNull
    @Override
    public String getThreadAuthorName() {
        return authorName;
    }

    @Override
    public long getLastReadTime() {
        return lastReadTime.getTime();
    }

    public Date getLastReadTimeDate() {
        return lastReadTime;
    }

    @Override
    public long getSortKey() {
        return lastReadTime.getTime();
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public void setThreadTitle(String threadTitle) {
        this.threadTitle = threadTitle;
    }

    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setForumTitle(String forumTitle) {
        this.forumTitle = forumTitle;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setLastReadTime(Date lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = new Date(lastReadTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeLong(this.threadId);
        dest.writeString(this.threadTitle);
        dest.writeValue(this.forumId);
        dest.writeValue(this.postId);
        dest.writeString(this.forumTitle);
        dest.writeLong(this.authorId);
        dest.writeString(this.authorName);
        dest.writeLong(lastReadTime != null ? lastReadTime.getTime() : -1);
    }

    public BrowseHistory() {
    }

    protected BrowseHistory(Parcel in) {
        this.userId = in.readLong();
        this.threadId = in.readLong();
        this.threadTitle = in.readString();
        this.forumId = (Long) in.readValue(Long.class.getClassLoader());
        this.postId = (Long) in.readValue(Long.class.getClassLoader());
        this.forumTitle = in.readString();
        this.authorId = in.readLong();
        this.authorName = in.readString();
        long tmpLastReadTime = in.readLong();
        this.lastReadTime = tmpLastReadTime == -1 ? null : new Date(tmpLastReadTime);
    }

    public static final Creator<BrowseHistory> CREATOR = new Creator<BrowseHistory>() {
        public BrowseHistory createFromParcel(Parcel source) {
            return new BrowseHistory(source);
        }

        public BrowseHistory[] newArray(int size) {
            return new BrowseHistory[size];
        }
    };
}
