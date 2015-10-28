package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.logic.restservice.model.LKForumInfo;
import org.cryse.lkong.logic.restservice.model.LKForumListItem;
import org.cryse.lkong.logic.restservice.model.LKForumNameList;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ForumListRequest extends AbstractHttpRequest<List<ForumModel>> {
    private static final String LOG_TAG = ForumListRequest.class.getSimpleName();
    private static final int FORUM_DETAIL_CACHE_TIME = 60*10;
    private static final int FORUM_LIST_CACHE_TIME = 60*60*24;
    @Override
    protected Request buildRequest() {
        return new Request.Builder()
                .addHeader(ACCEPT_ENCODING, ACCEPT_ENCODING_GZIP)
                .addHeader(CACHE_CONTROL, CACHE_CONTROL_MAX_AGE + FORUM_LIST_CACHE_TIME)
                .url(LKongWebConstants.LKONG_FORUM_LIST_REQUEST_URL)
                .build();
    }

    @Override
    protected List<ForumModel> parseResponse(Response response) throws Exception {
        Gson gson = GsonUtils.getGson();
        String responseString = gzipToString(response);
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
                        .addHeader(ACCEPT_ENCODING, ACCEPT_ENCODING_GZIP)
                        .addHeader(CACHE_CONTROL, CACHE_CONTROL_MAX_AGE + FORUM_DETAIL_CACHE_TIME)
                        .url(String.format(LKongWebConstants.LKONG_FORUM_CONFIG_REQUEST_URL, item.getFid()))
                        .build();

                itemInfoResponse = getOkHttpClient().newCall(itemInfoRequest).execute();
                if (!response.isSuccessful())
                    throw new IOException("Get forum detail info failed, reason: " + response);
                String itemInfoResponseString = gzipToString(itemInfoResponse);
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
}
