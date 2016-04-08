package org.cryse.utils.http.cookie;


import java.util.Collection;
import java.util.Map;

import okhttp3.Cookie;

public interface CookieStore {
    void addAll(String url, Collection<Cookie> cookies);
    Collection<Map.Entry<String, Collection<Cookie>>> getAll();
    Collection<Cookie> getForUrl(String url);
    void clear();
}
