package org.cryse.lkong.logic.restservice;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

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
import org.cryse.lkong.logic.TimelineListType;
import org.cryse.lkong.logic.restservice.exception.IdentityExpiredException;
import org.cryse.lkong.logic.restservice.exception.NeedIdentityException;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.logic.restservice.exception.SignInExpiredException;
import org.cryse.lkong.logic.restservice.model.LKCheckNoticeCountResult;
import org.cryse.lkong.logic.restservice.model.LKDataItemLocation;
import org.cryse.lkong.logic.restservice.model.LKForumInfo;
import org.cryse.lkong.logic.restservice.model.LKForumListItem;
import org.cryse.lkong.logic.restservice.model.LKForumNameList;
import org.cryse.lkong.logic.restservice.model.LKForumThreadList;
import org.cryse.lkong.logic.restservice.model.LKNewPostResult;
import org.cryse.lkong.logic.restservice.model.LKNewThreadResult;
import org.cryse.lkong.logic.restservice.model.LKNoticeRateResult;
import org.cryse.lkong.logic.restservice.model.LKNoticeResult;
import org.cryse.lkong.logic.restservice.model.LKPostList;
import org.cryse.lkong.logic.restservice.model.LKPostRateItem;
import org.cryse.lkong.logic.restservice.model.LKThreadInfo;
import org.cryse.lkong.logic.restservice.model.LKTimelineData;
import org.cryse.lkong.logic.restservice.model.LKUserInfo;
import org.cryse.lkong.model.DataItemLocationModel;
import org.cryse.lkong.model.EditPostResult;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.NewPostResult;
import org.cryse.lkong.model.NewThreadResult;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.NoticeModel;
import org.cryse.lkong.model.NoticeRateModel;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.PunchResult;
import org.cryse.lkong.model.SearchDataSet;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.CookieUtils;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.utils.MiniIOUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
        this.okHttpClient.setConnectTimeout(1, TimeUnit.MINUTES);
        this.okHttpClient.setReadTimeout(1, TimeUnit.MINUTES);
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
        SignInResult signInResult = new SignInResult();
        JSONObject jsonObject = new JSONObject(responseBody);
        if(responseBody.contains("\"error\":")) {
            String errorMessage = jsonObject.getString("error");
            signInResult.setSuccess(false);
            signInResult.setErrorMessage(TextUtils.isEmpty(errorMessage) ? "" : errorMessage);
            return signInResult;
        }
        boolean success = jsonObject.getBoolean("success");
        UserInfoModel me = getUserConfigInfo();

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

    public UserInfoModel getUserInfo(LKAuthObject authObject, long uid) throws Exception {
        checkSignInStatus(authObject, false);
        applyAuthCookies(authObject);
        String url = LKONG_INDEX_URL + String.format("?mod=ajax&action=userconfig_%06d", uid);
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
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
    
    public List<ThreadModel> getForumThreadList(long fid, long start, int listType) throws Exception {
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
        List<ThreadModel> threadList = ModelConverter.toForumThreadModel(lKThreadList, false);
        if(listType == ThreadListType.TYPE_SORT_BY_POST)
            Collections.reverse(threadList);
        return threadList;
    }

    public ThreadInfoModel getThreadInfo(LKAuthObject authObject, long tid) throws Exception {checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
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
        clearCookies();
        return threadInfoModel;
    }

    public List<PostModel> getThreadPostList(LKAuthObject authObject, long tid, int page) throws Exception {
        if(authObject != null) {
            checkSignInStatus(authObject, true);
            applyAuthCookies(authObject);
        }
        String url = String.format(LKONG_INDEX_URL + "?mod=data&sars=thread/%d/%s", tid, page);
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKPostList lkPostList = gson.fromJson(responseString, LKPostList.class);
        List<PostModel> postList = ModelConverter.toPostModelList(lkPostList);
        clearCookies();
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

    public List<ThreadModel> getFavorites(LKAuthObject authObject, long start) throws Exception {
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
            return new ArrayList<ThreadModel>();
        List<ThreadModel> favorites = ModelConverter.toForumThreadModel(lKThreadList, true);
        clearCookies();
        return favorites;
    }

    public Boolean addOrRemoveFavorite(LKAuthObject authObject, long tid, boolean remove) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        String url = String.format(LKONG_INDEX_URL + String.format("?mod=ajax&action=favorite&tid=%d", tid));
        url = url + (remove ? "&type=-1" : "");
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        JSONObject jsonObject = new JSONObject(responseString);
        if(!jsonObject.has("isfavorite")) return false;
        Boolean isFavorite = jsonObject.getInt("isfavorite") != 0 ;
        clearCookies();
        return isFavorite;
    }

    public List<TimelineModel> getTimeline(LKAuthObject authObject, long start, int listType) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);

        String url = String.format(LKONG_INDEX_URL + TimelineListType.typeToRequestParam(listType));
        url = url + (start >= 0 ? "&nexttime=" + Long.toString(start) : "");
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKTimelineData lkTimelineData = gson.fromJson(responseString, LKTimelineData.class);
        if(lkTimelineData.getData() == null || lkTimelineData.getData().size() == 0)
            return new ArrayList<TimelineModel>();
        List<TimelineModel> timelineList = ModelConverter.toTimelineModel(lkTimelineData);
        Collections.reverse(timelineList);
        clearCookies();
        return timelineList;
    }

    public String forumPunch(LKAuthObject authObject) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        String url = LKONG_INDEX_URL + "?mod=ajax&action=punch";
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return getStringFromGzipResponse(response);
    }

    public NoticeCountModel checkNoticeCount(LKAuthObject authObject) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        String url = LKONG_INDEX_URL + "?mod=ajax&action=langloop";
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        if(responseString.contains("\"error\":")) {
            NoticeCountModel errorModel = new NoticeCountModel();
            JSONObject jsonObject = new JSONObject(responseString);
            String errorMessage = jsonObject.getString("error");
            errorModel.setSuccess(false);
            errorModel.setErrorMessage(errorMessage);
            return errorModel;
        }

        LKCheckNoticeCountResult lkCheckNoticeCountResult = gson.fromJson(responseString, LKCheckNoticeCountResult.class);
        NoticeCountModel noticeCountModel = ModelConverter.toNoticeCountModel(lkCheckNoticeCountResult);
        clearCookies();
        return noticeCountModel;
    }

    public List<NoticeModel> getNotice(LKAuthObject authObject, long start) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        String url = LKONG_INDEX_URL + "?mod=data&sars=my/notice";
        url = url + (start >= 0 ? "&nexttime=" + Long.toString(start) : "");
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKNoticeResult lkNoticeResult = gson.fromJson(responseString, LKNoticeResult.class);
        List<NoticeModel> notices= ModelConverter.toNoticeModel(lkNoticeResult);
        clearCookies();
        return notices;
    }

    public List<NoticeRateModel> getNoticeRateLog(LKAuthObject authObject, long start) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        String url = LKONG_INDEX_URL + "?mod=data&sars=my/rate";
        url = url + (start >= 0 ? "&nexttime=" + Long.toString(start) : "");
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKNoticeRateResult lkNoticeRateResult = gson.fromJson(responseString, LKNoticeRateResult.class);
        List<NoticeRateModel> noticeRates= ModelConverter.toNoticeRateModel(lkNoticeRateResult);
        clearCookies();
        return noticeRates;
    }

    public DataItemLocationModel getDataItemLocation(LKAuthObject authObject, String dataItem) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        String url = LKONG_INDEX_URL + String.format("?mod=ajax&action=panelocation&dataitem=%s", dataItem);
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKDataItemLocation lkDataItemLocation = gson.fromJson(responseString, LKDataItemLocation.class);
        DataItemLocationModel locationModel= ModelConverter.toNoticeRateModel(lkDataItemLocation);
        clearCookies();
        return locationModel;
    }

    public PostModel.PostRate ratePost(LKAuthObject authObject, long postId, int score, String reaseon) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        String url = "http://lkong.cn/thread/index.php?mod=ajax&action=submitbox";
        FormEncodingBuilder builder= new FormEncodingBuilder()
                .add("request", String.format("rate_post_%d", postId))
                .add("num", Integer.toString(score))
                .add("reason", reaseon);
        RequestBody formBody = builder.build();
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        JSONObject rootObj = new JSONObject(responseString);
        JSONObject ratelogObj = rootObj.getJSONObject("ratelog");
        LKPostRateItem lkPostRateItem = gson.fromJson(ratelogObj.toString(), LKPostRateItem.class);
        PostModel.PostRate postRate = ModelConverter.toPostRate(lkPostRateItem);
        clearCookies();
        return postRate;
    }

    public EditPostResult editPost(LKAuthObject authObject, long tid, long pid, String action, String title, String content) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        FormEncodingBuilder builder= new FormEncodingBuilder()
                .add("type", "edit")
                .add("tid", Long.toString(tid))
                .add("pid", Long.toString(pid))
                .add("ac", action)
                .add("content", content);
        if(!TextUtils.isEmpty(title) && action.equalsIgnoreCase("thread")) {
            builder.add("title", title);
        }
        RequestBody formBody = builder.build();
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url("http://lkong.cn/post/edit/index.php?mod=post")
                .post(formBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseBody = getStringFromGzipResponse(response);
        JSONObject jsonObject = new JSONObject(responseBody);
        EditPostResult editPostResult = new EditPostResult();
        editPostResult.setTid(jsonObject.getLong("tid"));
        editPostResult.setSuccess(jsonObject.getBoolean("success"));
        if(jsonObject.has("errorMessage"))
            editPostResult.setErrorMessage(jsonObject.getString("errorMessage"));
        clearCookies();

        return editPostResult;
    }

    public SearchDataSet searchLKong(LKAuthObject authObject, long start, String queryString) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        String url = LKONG_INDEX_URL + String.format("?mod=data&sars=search/%s", URLEncoder.encode(queryString, "UTF-8"));
        if(start > 0) {
            url = url + "&nexttime=" + Long.toString(start);
        }
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        SearchDataSet dataSet = new SearchDataSet();
        dataSet.parseData(responseString);
        clearCookies();
        return dataSet;
    }

    public List<TimelineModel> getUserAll(LKAuthObject authObject, long start, long uid) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);

        String url = String.format("http://lkong.cn/user/index.php?mod=data&sars=user/%06d", uid);
        url = url + (start >= 0 ? "&nexttime=" + Long.toString(start) : "");
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKTimelineData lkTimelineData = gson.fromJson(responseString, LKTimelineData.class);
        if(lkTimelineData.getData() == null || lkTimelineData.getData().size() == 0)
            return new ArrayList<TimelineModel>();
        List<TimelineModel> timelineList = ModelConverter.toTimelineModel(lkTimelineData);
        Collections.reverse(timelineList);
        clearCookies();
        return timelineList;
    }

    public List<ThreadModel> getUserThreads(LKAuthObject authObject, long start, long uid, boolean isDigest) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        String url = String.format("http://lkong.cn/user/%06d/index.php?mod=data&sars=user/%06d/", uid, uid);
        url = url + (isDigest ? "digest" : "thread");
        url = url + (start >= 0 ? "&nexttime=" + Long.toString(start) : "");
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        LKForumThreadList lKThreadList = gson.fromJson(responseString, LKForumThreadList.class);
        List<ThreadModel> threadList = ModelConverter.toForumThreadModel(lKThreadList, false);
        clearCookies();
        return threadList;
    }

    public PunchResult punch(LKAuthObject authObject) throws Exception {
        checkSignInStatus(authObject, true);
        applyAuthCookies(authObject);
        String url = "http://lkong.cn/index.php?mod=ajax&action=punch";
        Request request = new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = getStringFromGzipResponse(response);
        JSONObject jsonObject = new JSONObject(responseString);
        if(!jsonObject.has("punchtime") || !jsonObject.has("punchday")) {
            clearCookies();
            return null;
        }
        PunchResult result = new PunchResult();
        long dateLong = jsonObject.getLong("punchtime");
        int punchDay = jsonObject.getInt("punchday");
        result.setPunchDay(punchDay);
        result.setPunchTime(new Date(dateLong * 1000));
        clearCookies();
        return result;
    }

    public void saveToSDCard(String filename, String content)throws Exception {
        File file = new File(Environment.getExternalStorageDirectory(), filename);//指定文件存储目录为SD卡，文件名
        FileOutputStream outStream = new FileOutputStream(file);//输出文件流
        outStream.write(content.getBytes());
        outStream.close();
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
