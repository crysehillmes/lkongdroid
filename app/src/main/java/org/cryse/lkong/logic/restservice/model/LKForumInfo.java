package org.cryse.lkong.logic.restservice.model;

public class LKForumInfo {
    private long fid;
    private String name;
    private String description;
    private String status;
    private int sortbydateline;
    private String threads;
    private String todayposts;
    private int fansnum;
    private String blackboard;
    private String[] moderators;

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

    public int getSortbydateline() {
        return sortbydateline;
    }

    public void setSortbydateline(int sortbydateline) {
        this.sortbydateline = sortbydateline;
    }

    public String getThreads() {
        return threads;
    }

    public void setThreads(String threads) {
        this.threads = threads;
    }

    public String getTodayposts() {
        return todayposts;
    }

    public void setTodayposts(String todayposts) {
        this.todayposts = todayposts;
    }

    public int getFansnum() {
        return fansnum;
    }

    public void setFansnum(int fansnum) {
        this.fansnum = fansnum;
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
}