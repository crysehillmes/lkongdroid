package org.cryse.lkong.utils;

import java.net.HttpCookie;
import java.net.URI;

public class LKAuthObject {
    URI mAuthURI;
    URI mDzsbheyURI;
    URI mIdentityURI;
    HttpCookie mAuthHttpCookie;
    HttpCookie mDzsbheyHttpCookie;
    HttpCookie mIdentityHttpCookie;

    public LKAuthObject(String auth, String dzsbhey, String identity) {
        mAuthURI = CookieUtils.deserializeHttpCookieForURI(auth);
        mAuthHttpCookie = CookieUtils.deserializeHttpCookieForCookie(auth);
        mDzsbheyURI = CookieUtils.deserializeHttpCookieForURI(dzsbhey);
        mDzsbheyHttpCookie = CookieUtils.deserializeHttpCookieForCookie(dzsbhey);
        mIdentityURI = CookieUtils.deserializeHttpCookieForURI(identity);
        mIdentityHttpCookie = CookieUtils.deserializeHttpCookieForCookie(identity);
    }

    public URI getAuthURI() {
        return mAuthURI;
    }

    public void setAuthURI(URI authURI) {
        this.mAuthURI = authURI;
    }

    public URI getDzsbheyURI() {
        return mDzsbheyURI;
    }

    public void setDzsbheyURI(URI dzsbheyURI) {
        this.mDzsbheyURI = dzsbheyURI;
    }

    public URI getIdentityURI() {
        return mIdentityURI;
    }

    public void setIdentityURI(URI identityURI) {
        this.mIdentityURI = identityURI;
    }

    public HttpCookie getAuthHttpCookie() {
        return mAuthHttpCookie;
    }

    public void setAuthHttpCookie(HttpCookie authHttpCookie) {
        this.mAuthHttpCookie = authHttpCookie;
    }

    public HttpCookie getDzsbheyHttpCookie() {
        return mDzsbheyHttpCookie;
    }

    public void setDzsbheyHttpCookie(HttpCookie dzsbheyHttpCookie) {
        this.mDzsbheyHttpCookie = dzsbheyHttpCookie;
    }

    public HttpCookie getIdentityHttpCookie() {
        return mIdentityHttpCookie;
    }

    public void setIdentityHttpCookie(HttpCookie identityHttpCookie) {
        this.mIdentityHttpCookie = identityHttpCookie;
    }

    public boolean hasExpired() {
        return (mAuthHttpCookie != null && mAuthHttpCookie.hasExpired()) ||
                (mDzsbheyHttpCookie != null && mDzsbheyHttpCookie.hasExpired());
    }

    public boolean isSignedIn() {
        return (mAuthHttpCookie != null && !mAuthHttpCookie.hasExpired()) && (mDzsbheyHttpCookie != null && !mDzsbheyHttpCookie.hasExpired());
    }

    public boolean hasIdentityExpired() {
        return (mIdentityHttpCookie != null && mIdentityHttpCookie.hasExpired());
    }

    public boolean hasIdentity() {
        return (mIdentityHttpCookie != null && !mIdentityHttpCookie.hasExpired());
    }
}
