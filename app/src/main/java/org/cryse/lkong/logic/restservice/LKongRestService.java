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
import org.cryse.lkong.logic.restservice.model.LKForumInfo;
import org.cryse.lkong.logic.restservice.model.LKForumListItem;
import org.cryse.lkong.logic.restservice.model.LKForumNameList;
import org.cryse.lkong.logic.restservice.model.LKUserInfo;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.CookieUtils;
import org.cryse.lkong.utils.SerializableHttpCookie;
import org.cryse.utils.MiniIOUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

import timber.log.Timber;

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
        );
        this.okHttpClient.setCookieHandler(cookieManager);
        this.gson = new Gson();
    }

    public SignInResult signIn(String email, String password) throws Exception {
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
        boolean success = jsonObject.getBoolean("success");
        UserInfoModel me = getUserConfigInfo();
        SignInResult signInResult = new SignInResult();

        signInResult.setSuccess(success);
        signInResult.setMe(me);
        readCookies(signInResult);
        cookieManager.getCookieStore().removeAll();

        return signInResult;
    }

    public UserInfoModel getUserConfigInfo() throws Exception {
        checkSignInStatus();
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(LKONG_INDEX_URL + "?mod=ajax&action=userconfig")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        Gson customGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        LKUserInfo lkUserInfo = customGson.fromJson(responseString, LKUserInfo.class);
        UserInfoModel userInfoModel = ModelConverter.toUserInfoModel(lkUserInfo);
        return userInfoModel;
    }

    public List<ForumModel> getForumList() throws Exception {
        // checkSignInStatus();
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(LKONG_INDEX_URL + "?mod=ajax&action=forumlist")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKForumNameList lkForumNameList = gson.fromJson(responseString, LKForumNameList.class);

        List<ForumModel> forumModels = new ArrayList<ForumModel>(lkForumNameList.getForumlist().size());
        for(LKForumListItem item : lkForumNameList.getForumlist()) {
            Response itemInfoResponse = null;
            ForumModel forumModel = new ForumModel();
            forumModel.setFid(item.getFid());
            forumModel.setName(item.getName());
            forumModel.setIcon(getLKForumIconUrl(item.getFid()));

            try {
                Request itemInfoRequest = new Request.Builder()
                        .addHeader("Accept-Encoding", "gzip")
                        .url(LKONG_INDEX_URL + "?mod=ajax&action=forumconfig_" + Long.toString(item.getFid()))
                        .build();

                itemInfoResponse = okHttpClient.newCall(itemInfoRequest).execute();
                if (!response.isSuccessful())
                    throw new IOException("Get forum detail info failed, reason: " + response);
                String itemInfoResponseString = getStringFromGzipResponse(itemInfoResponse);
                LKForumInfo forumInfo = gson.fromJson(itemInfoResponseString, LKForumInfo.class);
                forumModel.setDescription(forumInfo.getDescription());
                forumModel.setBlackboard(forumInfo.getBlackboard());
                forumModel.setFansNum(forumInfo.getFansnum());
                forumModel.setStatus(forumInfo.getStatus());
                forumModel.setSortByDateline(forumInfo.getSortbydateline());
                forumModel.setThreads(Integer.parseInt(forumInfo.getThreads()));
                forumModel.setTodayPosts(Integer.parseInt(forumInfo.getTodayposts()));
            } catch (Exception ex) {
                Timber.e(ex, "Get forum detail info exception.", LOG_TAG);
            } finally {
                forumModels.add(forumModel);
            }
        }
        return forumModels;
    }

    public static final int STATUS_NOT_SIGNEDIN = 0;
    public static final int STATUS_EXPIRED = 1;
    public static final int STATUS_SIGNEDIN = 2;

    public int isSignedIn() {
        String auth = null;
        String dzsbhey = null;
        for(HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
            if(cookie.getName().compareToIgnoreCase("auth") == 0) {
                // auth cookie pair
                if(cookie.hasExpired()) return STATUS_EXPIRED;
                auth = cookie.getValue();
            } else if (cookie.getName().compareToIgnoreCase("dzsbhey") == 0) {
                // dzsbhey cookie pair
                if(cookie.hasExpired()) return STATUS_EXPIRED;
                dzsbhey = cookie.getValue();
            }
        }
        if(!TextUtils.isEmpty(auth) && !TextUtils.isEmpty(dzsbhey))
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

    private String getLKForumIconUrl(long fid) {
        String fidString = String.format("%1$06d", fid);
        String iconUrl = String.format("http://img.lkong.cn/forumavatar/000/%s/%s/%s_avatar_middle.jpg",
                fidString.substring(0, 2),
                fidString.substring(2, 4),
                fidString.substring(4, 6)
        );
        return iconUrl;
    }

    private void readCookies(SignInResult signInResult) {
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
                    authURI = uri;
                    authHttpCookie = cookie;
                } else if (cookie.getName().compareToIgnoreCase("dzsbhey") == 0) {
                    // dzsbhey cookie pair
                    if(cookie.hasExpired())
                        continue;;
                    dzsbheyURI = uri;
                    dzsbheyHttpCookie = cookie;
                } else if (cookie.getName().compareToIgnoreCase("identity") == 0) {
                    // identity cookie pair
                    if(cookie.hasExpired())
                        continue;;
                    identityURI = uri;
                    identityHttpCookie = cookie;
                }
            }
        }
        if(authURI != null && authHttpCookie != null &&
                dzsbheyURI != null && dzsbheyHttpCookie != null &&
                identityURI != null && identityHttpCookie != null) {
            signInResult.setAuthCookie(CookieUtils.serializeHttpCookie(authURI, authHttpCookie));
            signInResult.setDzsbheyCookie(CookieUtils.serializeHttpCookie(dzsbheyURI, dzsbheyHttpCookie));
            signInResult.setDzsbheyCookie(CookieUtils.serializeHttpCookie(identityURI, identityHttpCookie));
        } else {
            throw new NeedSignInException("Cookie expired.");
        }
    }
}
