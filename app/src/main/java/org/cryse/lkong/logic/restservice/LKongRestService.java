package org.cryse.lkong.logic.restservice;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.tika.Tika;
import org.cryse.lkong.logic.ThreadListType;
import org.cryse.lkong.logic.restservice.exception.IdentityExpiredException;
import org.cryse.lkong.logic.restservice.exception.NeedIdentityException;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.logic.restservice.exception.SignInExpiredException;
import org.cryse.lkong.logic.restservice.model.LKForumInfo;
import org.cryse.lkong.logic.restservice.model.LKForumListItem;
import org.cryse.lkong.logic.restservice.model.LKForumNameList;
import org.cryse.lkong.logic.restservice.model.LKForumThreadList;
import org.cryse.lkong.logic.restservice.model.LKNewPostResult;
import org.cryse.lkong.logic.restservice.model.LKNewThreadResult;
import org.cryse.lkong.logic.restservice.model.LKPostList;
import org.cryse.lkong.logic.restservice.model.LKThreadInfo;
import org.cryse.lkong.logic.restservice.model.LKUserInfo;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.NewPostResult;
import org.cryse.lkong.model.NewThreadResult;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.model.ForumThreadModel;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.CookieUtils;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.utils.MiniIOUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private final Tika tika = new Tika();
    @Inject
    public LKongRestService(Context context) {
        this.okHttpClient = new OkHttpClient();
        this.cookieManager = new CookieManager(
        );
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.okHttpClient.setCookieHandler(cookieManager);

        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
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
        clearCookies();

        return signInResult;
    }

    private UserInfoModel getUserConfigInfo() throws Exception {
        // when call this method, the cookie manager should at least contain auth and dzsbhey cookie
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(LKONG_INDEX_URL + "?mod=ajax&action=userconfig")
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKUserInfo lkUserInfo = gson.fromJson(responseString, LKUserInfo.class);
        UserInfoModel userInfoModel = ModelConverter.toUserInfoModel(lkUserInfo);
        return userInfoModel;
    }

    public UserInfoModel getUserInfo(LKAuthObject authObject) throws Exception {
        checkSignInStatus(authObject, false);
        applyAuthCookies(authObject);

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
        clearCookies();
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
            forumModel.setIcon(ModelConverter.fidToForumIconUrl(item.getFid()));

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
    
    public List<ForumThreadModel> getForumThreadList(long fid, long start, int listType) throws Exception {
        String url = String.format(LKONG_INDEX_URL + "?mod=data&sars=forum/%d%s", fid, ThreadListType.typeToRequestParam(listType));
        url = url + (start >= 0 ? "&nexttime=" + Long.toString(start) : "");
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKForumThreadList lKThreadList = gson.fromJson(responseString, LKForumThreadList.class);
        Timber.d(String.format("LKongRestService::getForumThreadList() lkThreadList.size() = %d ", lKThreadList.getData().size()), LOG_TAG);
        List<ForumThreadModel> threadList = ModelConverter.toForumThreadModel(lKThreadList, false);
        Timber.d(String.format("LKongRestService::getForumThreadList() threadList.size() = %d ", threadList.size()), LOG_TAG);
        return threadList;
    }

    public ThreadInfoModel getThreadInfo(long tid) throws Exception {
        String url = String.format(LKONG_INDEX_URL + "?mod=ajax&action=threadconfig_%d", tid);
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKThreadInfo lkThreadInfo = gson.fromJson(responseString, LKThreadInfo.class);
        ThreadInfoModel threadInfoModel = ModelConverter.toThreadInfoModel(lkThreadInfo);
        return threadInfoModel;
    }

    public List<PostModel> getThreadPostList(long tid, int page) throws Exception {
        String url = String.format(LKONG_INDEX_URL + "?mod=data&sars=thread/%d/%s", tid, page);
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKPostList lkPostList = gson.fromJson(responseString, LKPostList.class);
        Timber.d(String.format("LKongRestService::getForumThreadList() lkThreadList.size() = %d ", lkPostList.getData().size()), LOG_TAG);
        List<PostModel> postList = ModelConverter.toPostModelList(lkPostList);
        Timber.d(String.format("LKongRestService::getForumThreadList() threadList.size() = %d ", postList.size()), LOG_TAG);
        return postList;
    }

    public String uploadImageToLKong(LKAuthObject authObject, String imagePath) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        File fileToUpload = new File(imagePath);
        String mimeTypeString = tika.detect(fileToUpload);

        RequestBody formBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", imagePath.substring(imagePath.lastIndexOf("/")), RequestBody
                        .create(MediaType.parse(mimeTypeString), fileToUpload))
                .build();
        String url = "http://lkong.cn:1337/upload";
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = response.body().string();
        Timber.d(responseString, LOG_TAG);
        JSONObject jsonObject = new JSONObject(responseString);
        String newUrl = jsonObject.getString("filelink");
        clearCookies();
        return newUrl;
    }

    public NewPostResult newPostReply(LKAuthObject authObject, long tid, Long pid, String content) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        FormEncodingBuilder builder= new FormEncodingBuilder()
                .add("type", "reply")
                .add("tid", Long.toString(tid))
                .add("myrequestid", pid == null ? String.format("thread_%d", tid) : String.format("post_%d", pid))
                .add("content", content);
        if(pid != null) {
            builder.add("replyid", pid.toString());
        }
        RequestBody formBody = builder.build();
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url("http://lkong.cn/forum/index.php?mod=post")
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseBody = getStringFromGzipResponse(response);
        Timber.d(responseBody, LOG_TAG);
        LKNewPostResult lkNewPostResult = gson.fromJson(responseBody, LKNewPostResult.class);
        NewPostResult newPostResult = new NewPostResult();
        if(lkNewPostResult == null || !lkNewPostResult.isSuccess()) {
            newPostResult.setSuccess(false);
            newPostResult.setErrorMessage(lkNewPostResult != null ? lkNewPostResult.getError() : "");
            Timber.d("NewPost failed", LOG_TAG);
        } else {
            newPostResult.setSuccess(true);
            newPostResult.setTid(lkNewPostResult.getTid());
            newPostResult.setPageCount(lkNewPostResult.getPage());
            newPostResult.setReplyCount(lkNewPostResult.getLou());
            Timber.d("NewPost success", LOG_TAG);
        }
        clearCookies();

        return newPostResult;
    }

    public NewThreadResult newPostThread(LKAuthObject authObject, String title, long fid, String content, boolean follow) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        FormEncodingBuilder builder= new FormEncodingBuilder()
                .add("title", title)
                .add("type", "new")
                .add("fid", Long.toString(fid))
                .add("content", content)
                .add("follow", follow ? "1" : "0");
        RequestBody formBody = builder.build();
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url("http://lkong.cn/post/new/index.php?mod=post")
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseBody = getStringFromGzipResponse(response);
        Timber.d(responseBody, LOG_TAG);
        LKNewThreadResult lkNewThreadResult = gson.fromJson(responseBody, LKNewThreadResult.class);
        NewThreadResult newThreadResult = new NewThreadResult();
        if(lkNewThreadResult == null || !lkNewThreadResult.isSuccess()) {
            newThreadResult.setSuccess(false);
            newThreadResult.setErrorMessage(lkNewThreadResult != null ? lkNewThreadResult.getError() : "");
            Timber.d("newPostThread failed", LOG_TAG);
        } else {
            newThreadResult.setSuccess(true);
            newThreadResult.setTid(lkNewThreadResult.getTid());
            newThreadResult.setType(lkNewThreadResult.getType());
            Timber.d("newPostThread success", LOG_TAG);
        }
        clearCookies();

        return newThreadResult;
    }

    public List<ForumThreadModel> getFavorites(LKAuthObject authObject, long start) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        String url = String.format(LKONG_INDEX_URL + "?mod=data&sars=my/favorite");
        url = url + (start >= 0 ? "&nexttime=" + Long.toString(start) : "");
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKForumThreadList lKThreadList = gson.fromJson(responseString, LKForumThreadList.class);
        if(lKThreadList.getData() == null || lKThreadList.getData().size() == 0)
            return new ArrayList<ForumThreadModel>();
        Timber.d(String.format("LKongRestService::getForumThreadList() lkThreadList.size() = %d ", lKThreadList.getData().size()), LOG_TAG);
        List<ForumThreadModel> threadList = ModelConverter.toForumThreadModel(lKThreadList, true);
        Timber.d(String.format("LKongRestService::getForumThreadList() threadList.size() = %d ", threadList.size()), LOG_TAG);
        clearCookies();
        return threadList;
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

    private void checkSignInStatus(LKAuthObject authObject, boolean checkIdentity) {
        if(!authObject.isSignedIn()) {
            if(authObject.hasExpired()) {
                throw new SignInExpiredException();
            } else {
                throw new NeedSignInException();
            }
        }
        if(checkIdentity) {
            if(authObject.hasIdentity()) {
                if(authObject.hasIdentityExpired())
                    throw new IdentityExpiredException();
            } else {
                throw new NeedIdentityException();
            }
        }
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
                } else if (cookie.getName().compareToIgnoreCase("identity") == 0) {
                    // identity cookie pair
                    if(cookie.hasExpired())
                        continue;
                    Timber.d(String.format("URI: %s, COOKIE: %s", uri, cookie.getName()), LOG_TAG);
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
            signInResult.setIdentityCookie(CookieUtils.serializeHttpCookie(identityURI, identityHttpCookie));
        } else {
            throw new NeedSignInException("Cookie expired.");
        }
    }

    private void applyAuthCookies(LKAuthObject authObject) {
        clearCookies();
        cookieManager.getCookieStore().add(authObject.getAuthURI(), authObject.getAuthHttpCookie());
        cookieManager.getCookieStore().add(authObject.getDzsbheyURI(), authObject.getDzsbheyHttpCookie());
        cookieManager.getCookieStore().add(authObject.getIdentityURI(), authObject.getIdentityHttpCookie());
    }

    private void clearCookies() {
        cookieManager.getCookieStore().removeAll();
    }
}
