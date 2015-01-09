package org.cryse.lkong.logic.restservice;

import android.content.Context;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.restservice.model.UserConfigInfo;
import org.cryse.lkong.utils.PersistentCookieStore;
import org.cryse.utils.MiniIOUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

public class LKongRestService {

    public static final String LKONG_DOMAIN_URL = "http://lkong.cn";
    public static final String LKONG_INDEX_URL = "/index.php";
    public static final String LKONG_SIGNIN_URL = LKONG_DOMAIN_URL + LKONG_INDEX_URL + "?mod=login";
    OkHttpClient okHttpClient;
    CookieManager cookieManager;
    Gson gson;
    @Inject
    public LKongRestService(Context context) {
        this.okHttpClient = new OkHttpClient();
        this.cookieManager = new CookieManager(
                new PersistentCookieStore(context),
                CookiePolicy.ACCEPT_ALL
        );
        this.okHttpClient.setCookieHandler(cookieManager);
        this.gson = new Gson();
    }

    public boolean signIn(String email, String password) throws Exception {
        RequestBody formBody = new FormEncodingBuilder()
                .add("action", "login")
                .add("email", email)
                .add("password", password)
                .add("rememberme", "on")
                .build();
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(LKONG_SIGNIN_URL)
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseBody = getStringFromGzipResponse(response);
        JSONObject jsonObject = new JSONObject(responseBody);
        return jsonObject.getBoolean("success");
    }

    public UserConfigInfo getUserConfigInfo() throws Exception {
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(LKONG_SIGNIN_URL)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        return gson.fromJson(responseString, UserConfigInfo.class);
    }

    public void isSignedIn() {
        // TODO: Check the cookie to find sign in status(not signed in or expired .etc)
    }

    public static String decompress(byte[] bytes) throws Exception {
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
        return MiniIOUtils.toString(gis);
    }

    private String getStringFromGzipResponse(Response response) throws Exception {
        return decompress(response.body().bytes());
    }

}
