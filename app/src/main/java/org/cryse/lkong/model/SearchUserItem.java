package org.cryse.lkong.model;

public class SearchUserItem {
    private String customStatus; // "customstatus": "",
    private String signHtml; // "sightml": "",
    private int gender; //        "gender": 1,
    private long userId; //        "uid": "764822",
    private String userName; //        "username": "<em>tyk<\/em><em>5555<\/em>",
    private String id; //        "id": "user_764822"

    public String getCustomStatus() {
        return customStatus;
    }

    public void setCustomStatus(String customStatus) {
        this.customStatus = customStatus;
    }

    public String getSignHtml() {
        return signHtml;
    }

    public void setSignHtml(String signHtml) {
        this.signHtml = signHtml;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
