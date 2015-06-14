package org.cryse.lkong.logic.restservice.model;

public class LKPostUser {
    private String adminid;
    private String customstatus;
    private int gender;
    private long regdate;
    private long uid;
    private String username;
    private boolean verify;
    private String verifymessage;
    private String color;
    private String stars;
    private String ranktitle;

    public String getAdminid() {
        return adminid;
    }

    public void setAdminid(String adminid) {
        this.adminid = adminid;
    }

    public String getCustomstatus() {
        return customstatus;
    }

    public void setCustomstatus(String customstatus) {
        this.customstatus = customstatus;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public long getRegdate() {
        return regdate;
    }

    public void setRegdate(long regdate) {
        this.regdate = regdate;
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

    public boolean isVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    public String getVerifymessage() {
        return verifymessage;
    }

    public void setVerifymessage(String verifymessage) {
        this.verifymessage = verifymessage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getRanktitle() {
        return ranktitle;
    }

    public void setRanktitle(String ranktitle) {
        this.ranktitle = ranktitle;
    }
}
