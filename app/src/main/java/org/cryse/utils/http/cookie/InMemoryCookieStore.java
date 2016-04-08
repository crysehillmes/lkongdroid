package org.cryse.utils.http.cookie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Cookie;

public class InMemoryCookieStore implements CookieStore {
    Map<String, Collection<Cookie>> mStoreByHttpUrl;

    public InMemoryCookieStore() {
        mStoreByHttpUrl = new HashMap<>();
    }

    @Override
    public void addAll(String url, Collection<Cookie> cookies) {
        if(mStoreByHttpUrl.containsKey(url)) {
            Collection<Cookie> oldCollection = mStoreByHttpUrl.get(url);
            Collection<Cookie> newCollection = new ArrayList<>(oldCollection);
            mStoreByHttpUrl.put(url, newCollection);
            for(Iterator<Cookie> iterator = newCollection.iterator(); iterator.hasNext();) {
                Cookie cookie = iterator.next();
                for(Iterator<Cookie> newIterator = cookies.iterator(); newIterator.hasNext();) {
                    Cookie newCookie = newIterator.next();
                    if(cookie.name().compareTo(newCookie.name()) == 0 && cookie.domain().compareTo(newCookie.domain()) == 0) {
                        iterator.remove();
                    } else if(CookieUtils.hasExpired(cookie)) {
                        iterator.remove();
                    }
                }
            }
            newCollection.addAll(cookies);
        } else {
            mStoreByHttpUrl.put(url, cookies);
        }
    }

    @Override
    public Collection<Map.Entry<String, Collection<Cookie>>> getAll() {
        return mStoreByHttpUrl.entrySet();
    }

    @Override
    public Collection<Cookie> getForUrl(String url) {
        Collection<Cookie> result = mStoreByHttpUrl.get(url);
        return result == null ? Collections.EMPTY_LIST : result;
    }

    @Override
    public void clear() {
        mStoreByHttpUrl.clear();
    }
}
