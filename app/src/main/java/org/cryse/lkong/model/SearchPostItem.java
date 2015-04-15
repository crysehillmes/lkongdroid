package org.cryse.lkong.model;

import java.util.Date;

public class SearchPostItem {
    private int replyCount; //"replynum": "5",
    private String userName; //        "username": "\u51b0\u83b2\u96ea\u5983",
    private long userId; //        "uid": "694795",
    private Date dateline; //        "dateline": "2014-05-12 01:08:49",
    private CharSequence subject; //        "subject": "<em>\u767e\u5ea6<\/em>\u4e66\u57ce\u2026\u2026<em>\u767e\u5ea6<\/em>\u6587\u5b66\u2026\u2026",
    private long sortKey; //        "sortkey": 1000000,
    private String id; //        "id": "thread_975166"

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getDateline() {
        return dateline;
    }

    public void setDateline(Date dateline) {
        this.dateline = dateline;
    }

    public CharSequence getSubject() {
        return subject;
    }

    public void setSubject(CharSequence subject) {
        this.subject = subject;
    }

    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
