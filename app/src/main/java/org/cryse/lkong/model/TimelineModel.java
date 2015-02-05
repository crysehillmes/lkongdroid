package org.cryse.lkong.model;

import java.util.Date;

public class TimelineModel {
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
}
