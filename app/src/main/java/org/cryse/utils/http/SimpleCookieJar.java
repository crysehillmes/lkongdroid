package org.cryse.utils.http;


import org.cryse.utils.http.cookie.CookieStore;
import org.cryse.utils.http.cookie.InMemoryCookieStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class SimpleCookieJar implements ClearableCookieJar {

    private CookieStore cache;

    public SimpleCookieJar(CookieStore cache) {
        this.cache = cache;
    }

    public SimpleCookieJar() {
        this.cache = new InMemoryCookieStore();
    }

    public CookieStore getCookieStore() {
        return cache;
    }

    @Override
    synchronized public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cache.addAll(url.scheme() + "://" + url.host(), cookies);
    }

    @Override
    synchronized public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> removedCookies = new ArrayList<>();
        List<Cookie> validCookies = new ArrayList<>();

        Collection<Cookie> cookies = cache.getForUrl(url.scheme() + "://" + url.host());
        for (Iterator<Cookie> it = cookies.iterator(); it.hasNext(); ) {
            Cookie currentCookie = it.next();

            if (isCookieExpired(currentCookie)) {
                removedCookies.add(currentCookie);
                it.remove();

            } else if (currentCookie.matches(url)) {
                validCookies.add(currentCookie);
            }
        }

        return  validCookies;
    }

    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    synchronized public void clear() {
        cache.clear();
    }
}