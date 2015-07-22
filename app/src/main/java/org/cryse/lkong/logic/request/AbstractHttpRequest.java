package org.cryse.lkong.logic.request;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.HttpDelegate;

import java.io.IOException;
import java.net.CookieManager;

public abstract class AbstractHttpRequest<ResponseType> {
    private HttpDelegate mHttpDelegate;

    public AbstractHttpRequest() {
        this(HttpDelegate.createNew());
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

    protected abstract Request buildRequest();

    protected Response executeHttpRequest() throws IOException {
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
}
