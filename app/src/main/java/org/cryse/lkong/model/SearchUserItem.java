package org.cryse.lkong.model;

public class SearchUserItem extends AbstractSearchResult {
    private String avatarUrl;
    private CharSequence customStatus; // "customstatus": "",
    private CharSequence signHtml; // "sightml": "",
    private int gender; //        "gender": 1,
    private long userId; //        "uid": "764822",
    private CharSequence userName; //        "username": "<em>tyk<\/em><em>5555<\/em>",
    private String id; //        "id": "user_764822"

    public CharSequence getCustomStatus() {
        return customStatus;
    }

    public void setCustomStatus(CharSequence customStatus) {
        this.customStatus = customStatus;
    }

    public CharSequence getSignHtml() {
        return signHtml;
    }

    public void setSignHtml(CharSequence signHtml) {
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

    public CharSequence getUserName() {
        return userName;
    }

    public void setUserName(CharSequence userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
