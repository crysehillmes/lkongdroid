package org.cryse.lkong.utils.cookie;

import java.net.HttpCookie;
import java.net.URI;

public class CookieUtils {
    private static final String SP_KEY_DELIMITER = "||-||"; // Unusual char in URL
    private static final String SP_KEY_COMBINE_DELIMITER = "<[||-||]>"; // Unusual char in URL
    private static final String SP_KEY_COMBINE_DELIMITER_REGEX = "\\<\\[\\|\\|\\-\\|\\|\\]\\>"; // Unusual char in URL
    private static final String SP_KEY_DELIMITER_REGEX = "\\|\\|\\-\\|\\|"; // Unusual char in URL
    public static String serializeHttpCookie(URI uri, HttpCookie httpCookie) {
        SerializableHttpCookie serializableHttpCookie = new SerializableHttpCookie();
        return uri.toString() + SP_KEY_DELIMITER + serializableHttpCookie.encode(httpCookie);
    }

    public static URI deserializeHttpCookieForURI(String data) {
        String[] parts = data.split(SP_KEY_DELIMITER_REGEX, 2);
        return URI.create(parts[0]);
    }

    public static HttpCookie deserializeHttpCookieForCookie(String data) {
        String[] parts = data.split(SP_KEY_DELIMITER_REGEX, 2);
        SerializableHttpCookie serializableHttpCookie = new SerializableHttpCookie();
        return serializableHttpCookie.decode(parts[1]);
    }

    public static String combineToOne(String...serializedCookieStrings) {
        StringBuilder builder = new StringBuilder();
        int count = serializedCookieStrings.length;
        for (int i = 0; i < count; i++) {
            if(i < count - 1 && i >= 0) {
                builder.append(serializedCookieStrings[i]).append(SP_KEY_COMBINE_DELIMITER);
            } else {
                builder.append(serializedCookieStrings[i]);
            }
        }
        return builder.toString();
    }

    public static String[] splitToSerializedCookies(String data) {
        return data.split(SP_KEY_COMBINE_DELIMITER_REGEX, 2);
    }
}
