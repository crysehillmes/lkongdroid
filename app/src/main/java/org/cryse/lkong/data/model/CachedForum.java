package org.cryse.lkong.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CachedForum extends RealmObject {
    public static final int TYPE_MAIN = 0;
    public static final int TYPE_FOLLOWED = 1;

    private long id;
    private long uid;
    private int type;
    private String name;
    private String icon;
    private String description;
    private String status;
    private int sortByDateline;
    private int threads;
    private int todayPosts;
    private int fansNum;
    private String blackboard;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
}
