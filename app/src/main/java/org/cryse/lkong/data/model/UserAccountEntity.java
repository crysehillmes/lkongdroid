package org.cryse.lkong.data.model;

import android.text.TextUtils;

import org.cryse.lkong.utils.LKAuthObject;

public class UserAccountEntity {
    private long userId;
    private String email;
    private String userName;
    private String userAvatar;
    private String authCookie;
    private String dzsbheyCookie;
    private String identityCookie;

    private LKAuthObject authObject;

    public UserAccountEntity() {
    }

    public UserAccountEntity(long userId, String email, String userName, String userAvatar, String authCookie, String dzsbheyCookie, String identityCookie) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.authCookie = authCookie;
        this.dzsbheyCookie = dzsbheyCookie;
        this.identityCookie = identityCookie;
    }

    public LKAuthObject getAuthObject() {
        if(!TextUtils.isEmpty(authCookie) && !TextUtils.isEmpty(dzsbheyCookie) && !TextUtils.isEmpty(identityCookie)) {
            if(authObject == null) {
                authObject = new LKAuthObject(authCookie, dzsbheyCookie, identityCookie);
            }
            return authObject;
        }
        throw new IllegalStateException("authCookie, dzsbheyCookie, identityCookie must not be null.");
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getAuthCookie() {
        return authCookie;
    }

    public void setAuthCookie(String authCookie) {
        this.authCookie = authCookie;
    }

    public String getDzsbheyCookie() {
        return dzsbheyCookie;
    }

    public void setDzsbheyCookie(String dzsbheyCookie) {
        this.dzsbheyCookie = dzsbheyCookie;
    }

    public String getIdentityCookie() {
        return identityCookie;
    }

    public void setIdentityCookie(String identityCookie) {
        this.identityCookie = identityCookie;
    }
}
