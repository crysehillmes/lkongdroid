package org.cryse.lkong.account;

import android.accounts.Account;

import org.cryse.lkong.utils.CookieUtils;
import org.cryse.utils.http.cookie.SerializableCookie;

import java.io.Serializable;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class UserAccount implements Serializable {
    private long userId;
    private String userName;
    private String userEmail;
    private String userAvatar;
    private HttpUrl authUrl;
    private HttpUrl dzsbheyUrl;
    private Cookie authCookie;
    private Cookie dzsbheyCookie;
    private Account mAccount;

    public UserAccount(Account account, long userId, String userName, String userEmail, String userAvatar, String serializedAuthCookie, String serializedDzsbheyCookie) {
        this.mAccount = account;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAvatar = userAvatar;
        setAuthCookie(serializedAuthCookie);
        setDzsbheyCookie(serializedDzsbheyCookie);
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public void setAuthCookie(HttpUrl url, Cookie cookie) {
        this.authUrl = url;
        this.authCookie = cookie;
    }

    public void setAuthCookie(String serialized) {
        SerializableCookie cookie = SerializableCookie.decode(serialized);
        setAuthCookie(HttpUrl.parse(cookie.getUrl()), cookie.getCookie());
    }

    public HttpUrl getAuthUrl() {
        return authUrl;
    }

    public Cookie getAuthCookie() {
        return authCookie;
    }

    public void setDzsbheyCookie(HttpUrl uri, Cookie cookie) {
        this.dzsbheyUrl = uri;
        this.dzsbheyCookie = cookie;
    }

    public void setDzsbheyCookie(String serialized) {
        SerializableCookie cookie = SerializableCookie.decode(serialized);
        setDzsbheyCookie(HttpUrl.parse(cookie.getUrl()), cookie.getCookie());
    }

    public HttpUrl getDzsbheyUrl() {
        return dzsbheyUrl;
    }

    public Cookie getDzsbheyCookie() {
        return dzsbheyCookie;
    }

    public Account getAccount() {
        return mAccount;
    }
}
