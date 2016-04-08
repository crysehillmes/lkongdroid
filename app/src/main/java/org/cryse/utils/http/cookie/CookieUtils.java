package org.cryse.utils.http.cookie;

import okhttp3.Cookie;

public class CookieUtils {
    public static boolean hasExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }
}
