package org.cryse.lkong.data.model;

import android.support.annotation.NonNull;

import org.cryse.lkong.data.provider.followedthread.FollowedThreadModel;

public class FollowedThread implements FollowedThreadModel {
    private long userId;
    private long threadId;
    private String threadTitle;
    private long threadAuthorId;
    private String threadAuthorName;
    private long threadTimeStamp;
    private int threadReplyCount;

    public FollowedThread(long userId, long threadId, String threadTitle, long threadAuthorId, String threadAuthorName, long threadTimeStamp, int threadReplyCount) {
        this.userId = userId;
        this.threadId = threadId;
        this.threadTitle = threadTitle;
        this.threadAuthorId = threadAuthorId;
        this.threadAuthorName = threadAuthorName;
        this.threadTimeStamp = threadTimeStamp;
        this.threadReplyCount = threadReplyCount;
    }

    public FollowedThread(FollowedThreadModel model) {
        this.userId = model.getUserId();
        this.threadId = model.getThreadId();
        this.threadTitle = model.getThreadTitle();
        this.threadAuthorId = model.getThreadAuthorId();
        this.threadAuthorName = model.getThreadAuthorName();
        this.threadTimeStamp = model.getThreadTimestamp();
        this.threadReplyCount = model.getThreadReplyCount();
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public long getThreadId() {
        return threadId;
    }

    @NonNull
    @Override
    public String getThreadTitle() {
        return threadTitle;
    }

    @Override
    public long getThreadAuthorId() {
        return threadAuthorId;
    }

    @NonNull
    @Override
    public String getThreadAuthorName() {
        return threadAuthorName;
    }

    public long getThreadTimestamp() {
        return threadTimeStamp;
    }

    @Override
    public int getThreadReplyCount() {
        return threadReplyCount;
    }
}
