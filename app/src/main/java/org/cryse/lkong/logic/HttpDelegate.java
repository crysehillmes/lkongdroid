package org.cryse.lkong.logic;

import org.cryse.utils.http.ClearableCookieJar;
import org.cryse.utils.http.SimpleCookieJar;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class HttpDelegate {
    private static final SimpleCookieJar sCookieJar;

    private static final OkHttpClient sOkHttpClient;

    private OkHttpClient mOkHttpClient;

    static {
        sCookieJar = new SimpleCookieJar();
        sOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .cookieJar(sCookieJar)
                .build();
    }

    public static HttpDelegate getDefault() {
        return new HttpDelegate(sOkHttpClient);
    }

    protected HttpDelegate(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public ClearableCookieJar getCookieJar() {
        return sCookieJar;
    }
}
