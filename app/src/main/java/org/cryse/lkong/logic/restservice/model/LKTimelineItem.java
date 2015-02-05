package org.cryse.lkong.logic.restservice.model;

public class LKTimelineItem {
    private boolean isquote;
    private String uid;
    private String username;
    private String dateline;
    private String message;
    private boolean isthread;
    private String tid;
    private String subject;
    private String t_author;
    private long t_authorid;
    private int t_replynum;
    private String id;
    private long sortkey;

    public boolean isIsquote() {
        return isquote;
    }

    public void setIsquote(boolean isquote) {
        this.isquote = isquote;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsthread() {
        return isthread;
    }

    public void setIsthread(boolean isthread) {
        this.isthread = isthread;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getT_author() {
        return t_author;
    }

    public void setT_author(String t_author) {
        this.t_author = t_author;
    }

    public long getT_authorid() {
        return t_authorid;
    }

    public void setT_authorid(long t_authorid) {
        this.t_authorid = t_authorid;
    }

    public int getT_replynum() {
        return t_replynum;
    }

    public void setT_replynum(int t_replynum) {
        this.t_replynum = t_replynum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSortkey() {
        return sortkey;
    }

    public void setSortkey(long sortkey) {
        this.sortkey = sortkey;
    }
}
