package org.cryse.lkong.logic;

import com.squareup.okhttp.OkHttpClient;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class HttpDelegate {
    private static final OkHttpClient sOkHttpClient = new OkHttpClient();
    private static final CookieManager sCookieManager = new CookieManager();

    private OkHttpClient mOkHttpClient;
    private CookieManager mCookieManager;
    public static HttpDelegate get(OkHttpClient okHttpClient, CookieManager cookieManager) {
        return new HttpDelegate(okHttpClient, cookieManager);
    }

    public static HttpDelegate createNew() {
        return new HttpDelegate(new OkHttpClient(), new CookieManager());
    }

    public static HttpDelegate getDefault() {
        return new HttpDelegate(sOkHttpClient, sCookieManager);
    }

    protected HttpDelegate(OkHttpClient okHttpClient, CookieManager cookieManager) {
        this.mOkHttpClient = okHttpClient;
        this.mCookieManager = cookieManager;
        this.mOkHttpClient.setConnectTimeout(1, TimeUnit.MINUTES);
        this.mOkHttpClient.setReadTimeout(1, TimeUnit.MINUTES);
        this.mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.mOkHttpClient.setCookieHandler(mCookieManager);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public CookieManager getCookieManager() {
        return mCookieManager;
    }

    private void applyCookie(URI uri, HttpCookie httpCookie) {
        getCookieManager().getCookieStore().add(uri, httpCookie);
    }

    private void clearCookies() {
        getCookieManager().getCookieStore().removeAll();
    }
}
