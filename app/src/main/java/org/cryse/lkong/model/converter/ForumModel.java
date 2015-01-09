package org.cryse.lkong.model.converter;

public class ForumModel {
    private long fid;
    private String name;
    private String icon;
    private String description;
    private String status;
    private int sortByDateline;
    private int threads;
    private int todayPosts;
    private int fansNum;
    private String blackboard;
    private String[] moderators;

    public ForumModel() {
    }

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public int getSortByDateline() {
        return sortByDateline;
    }

    public void setSortByDateline(int sortByDateline) {
        this.sortByDateline = sortByDateline;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getTodayPosts() {
        return todayPosts;
    }

    public void setTodayPosts(int todayPosts) {
        this.todayPosts = todayPosts;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
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
