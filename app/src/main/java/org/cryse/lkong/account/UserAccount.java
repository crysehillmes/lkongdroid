package org.cryse.lkong.account;

import android.accounts.Account;

import org.cryse.lkong.utils.CookieUtils;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.HttpCookie;
import java.net.URI;

public class UserAccount implements Serializable {
    private long userId;
    private String userName;
    private String userEmail;
    private String userAvatar;
    private URI authURI;
    private URI dzsbheyURI;
    private HttpCookie authCookie;
    private HttpCookie dzsbheyCookie;
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

    public void setAuthCookie(URI uri, HttpCookie cookie) {
        this.authURI = uri;
        this.authCookie = cookie;
    }

    public void setAuthCookie(String serialized) {
        setAuthCookie(
                CookieUtils.deserializeHttpCookieForURI(serialized),
                CookieUtils.deserializeHttpCookieForCookie(serialized)
        );
    }

    public URI getAuthURI() {
        return authURI;
    }

    public HttpCookie getAuthCookie() {
        return authCookie;
    }

    public void setDzsbheyCookie(URI uri, HttpCookie cookie) {
        this.dzsbheyURI = uri;
        this.dzsbheyCookie = cookie;
    }

    public void setDzsbheyCookie(String serialized) {
        setDzsbheyCookie(
                CookieUtils.deserializeHttpCookieForURI(serialized),
                CookieUtils.deserializeHttpCookieForCookie(serialized)
        );
    }

    public URI getDzsbheyURI() {
        return dzsbheyURI;
    }

    public HttpCookie getDzsbheyCookie() {
        return dzsbheyCookie;
    }

    public Account getAccount() {
        return mAccount;
    }
}
