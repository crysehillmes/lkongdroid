package org.cryse.lkong.utils;

import java.net.HttpCookie;
import java.net.URI;

public class CookieUtils {
    private static final String SP_KEY_DELIMITER = "||-||"; // Unusual char in URL

    public static String serializeHttpCookie(URI uri, HttpCookie httpCookie) {
        SerializableHttpCookie serializableHttpCookie = new SerializableHttpCookie();
        return uri.toString() + SP_KEY_DELIMITER + serializableHttpCookie.encode(httpCookie);
    }

    public static URI serializeHttpCookieForURI(String data) {
        String[] parts = data.split(SP_KEY_DELIMITER, 2);
        return URI.create(parts[0]);
    }

    public static HttpCookie serializeHttpCookieForCookie(String data) {
        String[] parts = data.split(SP_KEY_DELIMITER, 2);
        SerializableHttpCookie serializableHttpCookie = new SerializableHttpCookie();
        return serializableHttpCookie.decode(parts[1]);
    }
}
