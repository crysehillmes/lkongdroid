package org.cryse.lkong.logic.restservice.model;

import java.util.Date;

public class LKNoticeRateItem {
    private long sortkey;
    private long uid;
    private String username;
    private String message;
    private String extcredits;
    private String score;
    private String reason;
    private Date dateline;
    private long pid;
    private String id;

    public long getSortkey() {
        return sortkey;
    }

    public void setSortkey(long sortkey) {
        this.sortkey = sortkey;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtcredits() {
        return extcredits;
    }

    public void setExtcredits(String extcredits) {
        this.extcredits = extcredits;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDateline() {
        return dateline;
    }

    public void setDateline(Date dateline) {
        this.dateline = dateline;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
