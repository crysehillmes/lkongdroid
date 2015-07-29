package org.cryse.lkong.model;

import java.io.Serializable;
import java.util.Date;

public class UserInfoModel implements Serializable {
    private int email;
    private int gender;
    private int phoneNum;
    private Date regDate;
    private long uid;
    private long me;
    private String userName;
    private String userIcon;
    private int fansCount;
    private int followCount;
    private String blacklists;
    private String customStatus;
    private int digestPosts;
    private int posts;
    private int threads;
    private String sigHtml;
    private String smartMessage;
    private int activePoints;
    private int dragonMoney;
    private int dragonCrystal;
    private int totalPunchCount;
    private int longestContinuousPunch;
    private int currentContinuousPunch;
    private Date lastPunchTime;

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

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
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

    public String getSigHtml() {
        return sigHtml;
    }

    public void setSigHtml(String sigHtml) {
        this.sigHtml = sigHtml;
    }

    public String getSmartMessage() {
        return smartMessage;
    }

    public void setSmartMessage(String smartMessage) {
        this.smartMessage = smartMessage;
    }

    public int getActivePoints() {
        return activePoints;
    }

    public void setActivePoints(int activePoints) {
        this.activePoints = activePoints;
    }

    public int getDragonMoney() {
        return dragonMoney;
    }

    public void setDragonMoney(int dragonMoney) {
        this.dragonMoney = dragonMoney;
    }

    public int getDragonCrystal() {
        return dragonCrystal;
    }

    public void setDragonCrystal(int dragonCrystal) {
        this.dragonCrystal = dragonCrystal;
    }

    public int getTotalPunchCount() {
        return totalPunchCount;
    }

    public void setTotalPunchCount(int totalPunchCount) {
        this.totalPunchCount = totalPunchCount;
    }

    public int getLongestContinuousPunch() {
        return longestContinuousPunch;
    }

    public void setLongestContinuousPunch(int longestContinuousPunch) {
        this.longestContinuousPunch = longestContinuousPunch;
    }

    public int getCurrentContinuousPunch() {
        return currentContinuousPunch;
    }

    public void setCurrentContinuousPunch(int currentContinuousPunch) {
        this.currentContinuousPunch = currentContinuousPunch;
    }

    public Date getLastPunchTime() {
        return lastPunchTime;
    }

    public void setLastPunchTime(Date lastPunchTime) {
        this.lastPunchTime = lastPunchTime;
    }
}
