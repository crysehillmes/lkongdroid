package org.cryse.lkong.logic.request;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.utils.GzipUtils;

import java.io.IOException;
import java.net.CookieManager;

public abstract class AbstractHttpRequest<ResponseType> {
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ACCEPT_ENCODING_GZIP = "gzip";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CACHE_CONTROL_NO_CACHE = "no-cache";
    public static final String CACHE_CONTROL_ONLY_IF_CACHED = "only-if-cached";
    public static final String CACHE_CONTROL_MAX_STALE = "max-stale=";
    public static final String CACHE_CONTROL_MAX_AGE = "max-age=";

    private HttpDelegate mHttpDelegate;

    public AbstractHttpRequest() {
        this(HttpDelegate.getDefault());
    }

    public AbstractHttpRequest(HttpDelegate httpDelegate) {
        this.mHttpDelegate = httpDelegate;
    }

    protected CookieManager getCookieManager() {
        return mHttpDelegate.getCookieManager();
    }

    protected OkHttpClient getOkHttpClient() {
        return mHttpDelegate.getOkHttpClient();
    }

    protected abstract Request buildRequest() throws Exception;

    protected Request.Builder setCache(Request.Builder builder, int time) {
        builder.addHeader("Cache-Control", "max-stale=" + time);
        return builder;
    }

    protected Request.Builder ignoreCache(Request.Builder builder) {
        builder.addHeader("Cache-Control", "no-cache");
        return builder;
    }

    protected Request.Builder onlyIfCached(Request.Builder builder) {
        builder.addHeader("Cache-Control", "only-if-cached");
        return builder;
    }

    protected Response executeHttpRequest() throws Exception {
        return getOkHttpClient().newCall(buildRequest()).execute();
    }

    protected void clearCookies() {
        mHttpDelegate.getCookieManager().getCookieStore().removeAll();
    }

    public ResponseType execute() throws Exception {
        onPreExecute();
        Response response = executeHttpRequest();
        ResponseType result = parseResponse(response);
        onPostExecute();
        return result;
    }

    protected abstract ResponseType parseResponse(Response response) throws Exception;

    protected void onPreExecute() {

    }

    protected void onPostExecute() {
        clearCookies();
    }

    protected String gzipToString(Response response) throws Exception {
        return GzipUtils.responseToString(response);
    }
}
