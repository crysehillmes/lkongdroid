package org.cryse.lkong.logic.restservice;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.logic.restservice.exception.SignInExpiredException;
import org.cryse.lkong.logic.restservice.model.ForumNameList;
import org.cryse.lkong.logic.restservice.model.UserInfo;
import org.cryse.lkong.utils.PersistentCookieStore;
import org.cryse.utils.MiniIOUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

public class LKongRestService {
    public static final String LOG_TAG = LKongRestService.class.getName();
    public static final String LKONG_DOMAIN_URL = "http://lkong.cn";
    public static final String LKONG_INDEX_URL = LKONG_DOMAIN_URL + "/index.php";
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
                .url(LKONG_INDEX_URL + "?mod=login")
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseBody = getStringFromGzipResponse(response);
        JSONObject jsonObject = new JSONObject(responseBody);
        return jsonObject.getBoolean("success");
    }

    public UserInfo getUserConfigInfo() throws Exception {
        checkSignInStatus();
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(LKONG_INDEX_URL + "?mod=ajax&action=userconfig")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        Gson customGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return customGson.fromJson(responseString, UserInfo.class);
    }

    public ForumNameList getForumList() throws Exception {
        checkSignInStatus();
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(LKONG_INDEX_URL + "?mod=ajax&action=forumlist")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        return gson.fromJson(responseString, ForumNameList.class);
    }

    public static final int STATUS_NOT_SIGNEDIN = 0;
    public static final int STATUS_EXPIRED = 1;
    public static final int STATUS_SIGNEDIN = 2;

    public int isSignedIn() {
        String auth = null;
        String dzsbhey = null;
        String identity = null;
        for(HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
            if(cookie.getName().compareToIgnoreCase("auth") == 0) {
                // auth cookie pair
                if(cookie.hasExpired()) return STATUS_EXPIRED;
                auth = cookie.getValue();
            } else if (cookie.getName().compareToIgnoreCase("dzsbhey") == 0) {
                // dzsbhey cookie pair
                if(cookie.hasExpired()) return STATUS_EXPIRED;
                dzsbhey = cookie.getValue();
            } else if (cookie.getName().compareToIgnoreCase("identity") == 0) {
                // identity cookie pair
                if(cookie.hasExpired()) return STATUS_EXPIRED;
                identity = cookie.getValue();
            }
        }
        if(!TextUtils.isEmpty(auth) && !TextUtils.isEmpty(dzsbhey) && !TextUtils.isEmpty(identity))
            return STATUS_SIGNEDIN;
        else
            return STATUS_NOT_SIGNEDIN;
    }

    private static String decompress(byte[] bytes) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        GZIPInputStream gis = new GZIPInputStream(byteArrayInputStream);
        String resultString = MiniIOUtils.toString(gis);
        gis.close();
        byteArrayInputStream.close();
        return resultString;
    }

    private String getStringFromGzipResponse(Response response) throws Exception {
        return decompress(response.body().bytes());
    }

    private void checkSignInStatus() {
        switch (isSignedIn()) {
            case STATUS_NOT_SIGNEDIN:
                throw new NeedSignInException();
            case STATUS_EXPIRED:
                throw new SignInExpiredException();
            case STATUS_SIGNEDIN:
                break;
        }
    }
}
