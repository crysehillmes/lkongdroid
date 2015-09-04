package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.LKongAuthenticateResult;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.logic.restservice.model.LKUserInfo;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.cookie.CookieUtils;
import org.cryse.lkong.utils.GsonUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

import timber.log.Timber;

public class SignInRequest extends AbstractHttpRequest<LKongAuthenticateResult> {
    private static final String LOG_TAG = SignInRequest.class.getSimpleName();
    private String mUserEmail;
    private String mUserPassword;
    public SignInRequest(String userEmail, String userPassword) {
        super();
        this.mUserEmail = userEmail;
        this.mUserPassword = userPassword;
    }

    public SignInRequest(HttpDelegate httpDelegate, String userEmail, String userPassword) {
        super(httpDelegate);
        this.mUserEmail = userEmail;
        this.mUserPassword = userPassword;
    }

    @Override
    protected Request buildRequest() {
        RequestBody formBody = new FormEncodingBuilder()
                .add("action", "login")
                .add("email", mUserEmail)
                .add("password", mUserPassword)
                .add("rememberme", "on")
                .build();
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url("http://lkong.cn/index.php?mod=login")
                .post(formBody)
                .build();
    }

    @Override
    public LKongAuthenticateResult parseResponse(Response response) throws Exception {
        String responseBody = gzipToString(response);
        JSONObject jsonObject = new JSONObject(responseBody);
        if(responseBody.contains("\"error\":")) {
            String errorMessage = jsonObject.getString("error");
            throw new Exception(errorMessage);
        } else {
            LKongAuthenticateResult result = new LKongAuthenticateResult();
            result.userEmail = mUserEmail;
            UserInfoModel userModel = getUserConfigInfo();
            result.userId = userModel.getUid();
            result.userName = userModel.getUserName();
            result.userAvatar = ModelConverter.uidToAvatarUrl(result.userId);
            readCookies(result);
            return result;
        }
    }

    private UserInfoModel getUserConfigInfo() throws Exception {
        Gson gson = GsonUtils.getGson();
        // when call this method, the cookie manager should at least contain auth and dzsbhey cookie
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(LKongWebConstants.LKONG_INDEX_URL + "?mod=ajax&action=userconfig")
                .build();

        Response response = getOkHttpClient().newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = gzipToString(response);
        LKUserInfo lkUserInfo = gson.fromJson(responseString, LKUserInfo.class);
        return ModelConverter.toUserInfoModel(lkUserInfo);
    }

    private void readCookies(LKongAuthenticateResult result) {
        URI authURI = null, dzsbheyURI = null, identityURI = null;
        HttpCookie authHttpCookie = null, dzsbheyHttpCookie = null, identityHttpCookie = null;

        List<URI> uris = getCookieManager().getCookieStore().getURIs();
        for(URI uri : uris) {
            List<HttpCookie> httpCookies = getCookieManager().getCookieStore().get(uri);
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
            result.authCookie = CookieUtils.serializeHttpCookie(authURI, authHttpCookie);
            result.dzsbheyCookie = CookieUtils.serializeHttpCookie(dzsbheyURI, dzsbheyHttpCookie);
        } else {
            throw new NeedSignInException("Error");
        }
    }

    @Override
    protected void onPostExecute() {
        super.onPostExecute();
    }
}
