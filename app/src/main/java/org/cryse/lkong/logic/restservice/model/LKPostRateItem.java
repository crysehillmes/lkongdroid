package org.cryse.lkong.logic.restservice.model;

import java.util.Date;

public class LKPostRateItem {
    private Date dateline;
    private int extcredits;
    private long pid;
    private String reason;
    private int score;
    private long uid;
    private String username;

    public Date getDateline() {
        return dateline;
    }

    public void setDateline(Date dateline) {
        this.dateline = dateline;
    }

    public int getExtcredits() {
        return extcredits;
    }

    public void setExtcredits(int extcredits) {
        this.extcredits = extcredits;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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
}
