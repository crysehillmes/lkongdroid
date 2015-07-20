package org.cryse.lkong.account;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.logic.restservice.model.LKUserInfo;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.CookieUtils;
import org.cryse.lkong.utils.GzipUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class LKongServerAuthenticate {
    private static final String LOG_TAG = LKongServerAuthenticate.class.getName();
    OkHttpClient okHttpClient;
    CookieManager cookieManager;
    Gson gson;

    public LKongServerAuthenticate() {
        this.okHttpClient = new OkHttpClient();
        this.okHttpClient.setConnectTimeout(1, TimeUnit.MINUTES);
        this.okHttpClient.setReadTimeout(1, TimeUnit.MINUTES);
        this.cookieManager = new CookieManager(
        );
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.okHttpClient.setCookieHandler(cookieManager);

        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public LKongAuthenticateResult userSignIn(String email, String password) throws Exception {
        RequestBody formBody = new FormEncodingBuilder()
                .add("action", "login")
                .add("email", email)
                .add("password", password)
                .add("rememberme", "on")
                .build();
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url("http://lkong.cn/index.php?mod=login")
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseBody = GzipUtils.decompress(response.body().bytes());
        Log.d(LOG_TAG, responseBody);
        JSONObject jsonObject = new JSONObject(responseBody);
        if(responseBody.contains("\"error\":")) {
            String errorMessage = jsonObject.getString("error");
            Timber.i("SIGNIN_FAILED" + errorMessage, LOG_TAG, responseBody);
            throw new Exception(errorMessage);
        } else {
            Timber.i("SIGNIN_SUCCESS", LOG_TAG, responseBody);
            LKongAuthenticateResult result = new LKongAuthenticateResult();
            result.userEmail = email;
            result.combinedCookie = readCookies();
            UserInfoModel userModel = getUserConfigInfo();
            result.userId = userModel.getUid();
            result.userName = userModel.getUserName();
            result.userAvatar = ModelConverter.uidToAvatarUrl(result.userId);
            cookieManager.getCookieStore().removeAll();
            return result;
        }
    }

    private UserInfoModel getUserConfigInfo() throws Exception {
        // when call this method, the cookie manager should at least contain auth and dzsbhey cookie
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url("http://lkong.cn/index.php?mod=ajax&action=userconfig")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = GzipUtils.decompress(response.body().bytes());
        LKUserInfo lkUserInfo = gson.fromJson(responseString, LKUserInfo.class);
        return ModelConverter.toUserInfoModel(lkUserInfo);
    }


    private String readCookies() {
        URI authURI = null, dzsbheyURI = null, identityURI = null;
        HttpCookie authHttpCookie = null, dzsbheyHttpCookie = null, identityHttpCookie = null;

        List<URI> uris = cookieManager.getCookieStore().getURIs();
        for(URI uri : uris) {
            List<HttpCookie> httpCookies = cookieManager.getCookieStore().get(uri);
            for(HttpCookie cookie : httpCookies) {
                if(cookie.getName().compareToIgnoreCase("auth") == 0) {
                    // auth cookie pair
                    if(cookie.hasExpired())
                        continue;
                    Timber.d(String.format("URI: %s, COOKIE: %s", uri, cookie.getName()), LOG_TAG);
                    authURI = uri;
                    authHttpCookie = cookie;
                } else if (cookie.getName().compareToIgnoreCase("dzsbhey") == 0) {
                    // dzsbhey cookie pair
                    if(cookie.hasExpired())
                        continue;
                    Timber.d(String.format("URI: %s, COOKIE: %s", uri, cookie.getName()), LOG_TAG);
                    dzsbheyURI = uri;
                    dzsbheyHttpCookie = cookie;
                }
            }
        }
        if(authURI != null && authHttpCookie != null &&
                dzsbheyURI != null && dzsbheyHttpCookie != null) {
            String authCookieString = CookieUtils.serializeHttpCookie(authURI, authHttpCookie);
            String dzsbheyCookieString = CookieUtils.serializeHttpCookie(dzsbheyURI, dzsbheyHttpCookie);
            return CookieUtils.combineToOne(authCookieString, dzsbheyCookieString);
        } else {
            throw new NeedSignInException("Error");
        }
    }
}
