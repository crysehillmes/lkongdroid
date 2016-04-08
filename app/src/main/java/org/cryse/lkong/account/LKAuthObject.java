package org.cryse.lkong.account;

import org.cryse.utils.http.cookie.SerializableCookie;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class LKAuthObject {
    long mUserId;
    String mUserName;
    HttpUrl mAuthUrl;
    HttpUrl mDzsbheyUrl;
    HttpUrl mIdentityUrl;
    Cookie mAuthCookie;
    Cookie mDzsbheyCookie;
    Cookie mIdentityCookie;

    public LKAuthObject(long userId, String auth, String dzsbhey, String identity) {
        mUserId = userId;
        SerializableCookie serializableAuthCookie = SerializableCookie.decode(auth);
        SerializableCookie serializableDzsbheyCookie = SerializableCookie.decode(dzsbhey);
        SerializableCookie serializableIdentityCookie = SerializableCookie.decode(identity);
        mAuthUrl = HttpUrl.parse(serializableAuthCookie.getUrl());
        mAuthCookie = serializableAuthCookie.getCookie();
        mDzsbheyUrl = HttpUrl.parse(serializableDzsbheyCookie.getUrl());
        mDzsbheyCookie = serializableDzsbheyCookie.getCookie();
        mIdentityUrl = HttpUrl.parse(serializableIdentityCookie.getUrl());
        mIdentityCookie = serializableIdentityCookie.getCookie();
    }

    public LKAuthObject(
            long userId,
            String userName,
            HttpUrl authUrl,
            Cookie authCookie,
            HttpUrl dzsbheyUrl,
            Cookie dzsbheyCookie

    ) {
        mUserId = userId;
        mUserName = userName;
        mAuthUrl = authUrl;
        mAuthCookie = authCookie;
        mDzsbheyUrl = dzsbheyUrl;
        mDzsbheyCookie = dzsbheyCookie;
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long userId) {
        this.mUserId = userId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public HttpUrl getAuthUrl() {
        return mAuthUrl;
    }

    public void setAuthUrl(HttpUrl authUrl) {
        this.mAuthUrl = authUrl;
    }

    public HttpUrl getDzsbheyUrl() {
        return mDzsbheyUrl;
    }

    public void setDzsbheyUrl(HttpUrl dzsbheyUrl) {
        this.mDzsbheyUrl = dzsbheyUrl;
    }

    public HttpUrl getIdentityUrl() {
        return mIdentityUrl;
    }

    public void setIdentityUrl(HttpUrl identityUrl) {
        this.mIdentityUrl = identityUrl;
    }

    public Cookie getAuthCookie() {
        return mAuthCookie;
    }

    public void setAuthCookie(Cookie authCookie) {
        this.mAuthCookie = authCookie;
    }

    public Cookie getDzsbheyCookie() {
        return mDzsbheyCookie;
    }

    public void setDzsbheyCookie(Cookie dzsbheyCookie) {
        this.mDzsbheyCookie = dzsbheyCookie;
    }

    public Cookie getIdentityCookie() {
        return mIdentityCookie;
    }

    public void setIdentityCookie(Cookie identityCookie) {
        this.mIdentityCookie = identityCookie;
    }

    public boolean hasExpired() {
        return (mAuthCookie != null && hasExpired(mAuthCookie)) ||
                (mDzsbheyCookie != null && hasExpired(mDzsbheyCookie));
    }

    public boolean isSignedIn() {
        return (mAuthCookie != null && !hasExpired(mAuthCookie)) && (mDzsbheyCookie != null && !hasExpired(mDzsbheyCookie));
    }

    public boolean hasIdentityExpired() {
        return (mIdentityCookie != null && hasExpired(mIdentityCookie));
    }

    public boolean hasIdentity() {
        return (mIdentityCookie != null && !hasExpired(mIdentityCookie));
    }

    private boolean hasExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }
}
