package org.cryse.utils.http.cookie;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.Cookie;

public class SerializableCookie {
    private static final String TAG = SerializableCookie.class
            .getSimpleName();

    private static final String KEY_URL = "url";
    private static final String KEY_NAME = "name";
    private static final String KEY_VALUE = "value";
    private static final String KEY_PERSISTENT = "persistent";
    private static final String KEY_DOMAIN = "domain";
    private static final String KEY_PATH = "path";
    private static final String KEY_SECURE = "secure";
    private static final String KEY_HTTPONLY = "httpOnly";
    private static final String KEY_HOSTONLY = "hostOnly";

    private transient Cookie mCookie;
    private transient String mUrl;

    public static String encode(String url, Cookie cookie) {
        SerializableCookie serializableCookie = new SerializableCookie();
        serializableCookie.mUrl = url;
        serializableCookie.mCookie = cookie;

        JsonObject object = new JsonObject();
        object.addProperty(KEY_URL, url);
        object.addProperty(KEY_NAME, cookie.name());
        object.addProperty(KEY_VALUE, cookie.value());
        object.addProperty(KEY_PERSISTENT, cookie.persistent() ? cookie.expiresAt() : NON_VALID_EXPIRES_AT);
        object.addProperty(KEY_DOMAIN, cookie.domain());
        object.addProperty(KEY_PATH, cookie.path());
        object.addProperty(KEY_SECURE, cookie.secure());
        object.addProperty(KEY_HTTPONLY, cookie.httpOnly());
        object.addProperty(KEY_HOSTONLY, cookie.hostOnly());

        return byteArrayToHexString(object.toString().getBytes());
    }

    /**
     * Using some super basic byte array &lt;-&gt; hex conversions so we don't
     * have to rely on any large Base64 libraries. Can be overridden if you
     * like!
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
    }

    public static SerializableCookie decode(String encodedCookie) {
        byte[] bytes = hexStringToByteArray(encodedCookie);
        String jsonString = new String(bytes);

        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(jsonString);
        String url = object.get(KEY_URL).getAsString();

        Cookie.Builder builder = new Cookie.Builder();

        builder.name(object.get(KEY_NAME).getAsString());

        builder.value(object.get(KEY_VALUE).getAsString());

        long expiresAt = object.get(KEY_PERSISTENT).getAsLong();
        if (expiresAt != NON_VALID_EXPIRES_AT) {
            builder.expiresAt(expiresAt);
        }

        final String domain = object.get(KEY_DOMAIN).getAsString();
        builder.domain(domain);

        builder.path(object.get(KEY_PATH).getAsString());

        if (object.get(KEY_SECURE).getAsBoolean())
            builder.secure();

        if (object.get(KEY_HTTPONLY).getAsBoolean())
            builder.httpOnly();

        if (object.get(KEY_HOSTONLY).getAsBoolean())
            builder.hostOnlyDomain(domain);

        Cookie cookie = builder.build();

        return new SerializableCookie(url, cookie);
    }

    /**
     * Converts hex values from strings to byte array
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
                    .digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    private static long NON_VALID_EXPIRES_AT = -1l;

    public SerializableCookie() {
    }

    public SerializableCookie(String url, Cookie cookie) {
        this.mUrl = url;
        this.mCookie = cookie;
    }

    public Cookie getCookie() {
        return mCookie;
    }

    public String getUrl() {
        return mUrl;
    }
}
