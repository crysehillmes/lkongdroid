package org.cryse.lkong.model;

import java.io.Serializable;
import java.util.Date;

public class UserInfoModel implements Serializable {
    private int email;
    private int gender;
    private int phoneNum;
    private Date regDate;
    private long uid;
    private String userIcon;
    private long me;
    private String userName;
    private int fansCount;
    private int followCount;
    private String blacklists;
    private String customStatus;
    private int digestPosts;
    private int posts;
    private int threads;

    public int getEmail() {
        return email;
    }

    public void setEmail(int email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(int phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public long getMe() {
        return me;
    }

    public void setMe(long me) {
        this.me = me;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public String getBlacklists() {
        return blacklists;
    }

    public void setBlacklists(String blacklists) {
        this.blacklists = blacklists;
    }

    public String getCustomStatus() {
        return customStatus;
    }

    public void setCustomStatus(String customStatus) {
        this.customStatus = customStatus;
    }

    public int getDigestPosts() {
        return digestPosts;
    }

    public void setDigestPosts(int digestPosts) {
        this.digestPosts = digestPosts;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }
}
