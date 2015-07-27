package org.cryse.lkong.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import org.cryse.lkong.logic.request.SignInRequest;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

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
        SignInRequest signInRequest = new SignInRequest(email, password);
        return signInRequest.execute();
    }
}
