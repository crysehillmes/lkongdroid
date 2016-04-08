package org.cryse.lkong.logic.request;

import com.google.gson.Gson;

import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.cryse.lkong.account.LKongAuthenticateResult;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.logic.restservice.model.LKUserInfo;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;
import org.cryse.utils.http.cookie.CookieUtils;
import org.cryse.utils.http.cookie.SerializableCookie;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

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
        FormBody formBody = new FormBody.Builder()
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
        HttpUrl authUrl = null, dzsbheyUrl = null, identityUrl = null;
        Cookie authCookie = null, dzsbheyCookie = null, identityCookie = null;

            Collection<Map.Entry<String, Collection<Cookie>>> cookiesCollections = getCookieJar().getCookieStore().getAll();
            for (Iterator<Map.Entry<String, Collection<Cookie>>> collectionIterator = cookiesCollections.iterator(); collectionIterator.hasNext();) {
                Map.Entry<String, Collection<Cookie>> currentCollection = collectionIterator.next();
                for (Iterator<Cookie> cookieIterator = currentCollection.getValue().iterator(); cookieIterator.hasNext();) {
                    Cookie cookie = cookieIterator.next();
                    if(cookie.name().compareToIgnoreCase("auth") == 0) {
                        // auth cookie pair
                        if(CookieUtils.hasExpired(cookie))
                            continue;
                        authUrl = HttpUrl.parse(currentCollection.getKey());
                        authCookie = cookie;
                        Timber.d(String.format("URI: %s, COOKIE: %s", currentCollection.getKey(), cookie.name()), LOG_TAG);
                    } else if (cookie.name().compareToIgnoreCase("dzsbhey") == 0) {
                        // dzsbhey cookie pair
                        if(CookieUtils.hasExpired(cookie))
                            continue;
                        dzsbheyUrl = HttpUrl.parse(currentCollection.getKey());
                        dzsbheyCookie = cookie;
                        Timber.d(String.format("URI: %s, COOKIE: %s", currentCollection.getKey(), cookie.name()), LOG_TAG);
                    }
                }
            }
        if(authUrl != null && authCookie != null &&
                dzsbheyUrl != null && dzsbheyCookie != null) {
            result.authCookie = SerializableCookie.encode(authUrl.toString(), authCookie);
            result.dzsbheyCookie = SerializableCookie.encode(dzsbheyUrl.toString(), dzsbheyCookie);
        } else {
            throw new NeedSignInException("Error");
        }
    }

    @Override
    protected void onPostExecute() {
        super.onPostExecute();
    }
}
