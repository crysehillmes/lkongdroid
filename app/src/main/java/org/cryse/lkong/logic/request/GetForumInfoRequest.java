package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.logic.restservice.model.LKForumInfo;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;
import org.cryse.lkong.utils.LKAuthObject;

public class GetForumInfoRequest extends AbstractAuthedHttpRequest<ForumModel> {
    private long mForumId;
    public GetForumInfoRequest(LKAuthObject authObject, long forumId) {
        super(authObject);
        this.mForumId = forumId;
    }

    public GetForumInfoRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long forumId) {
        super(httpDelegate, authObject);
        this.mForumId = forumId;
    }

    @Override
    protected Request buildRequest() {
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(String.format(LKongWebConstants.LKONG_FORUM_CONFIG_REQUEST_URL, mForumId))
                .build();
    }

    @Override
    protected ForumModel parseResponse(Response response) throws Exception {
        Gson gson = GsonUtils.getGson();
        String responseString = gzipToString(response);
        LKForumInfo forumInfo = gson.fromJson(responseString, LKForumInfo.class);
        ForumModel forumModel = new ForumModel();
        forumModel.setFid(forumInfo.getFid());
        forumModel.setName(forumInfo.getName());
        forumModel.setDescription(forumInfo.getDescription());
        forumModel.setIcon(ModelConverter.fidToForumIconUrl(forumInfo.getFid()));
        forumModel.setBlackboard(forumInfo.getBlackboard());
        forumModel.setFansNum(forumInfo.getFansnum());
        forumModel.setStatus(forumInfo.getStatus());
        forumModel.setSortByDateline(forumInfo.getSortbydateline());
        forumModel.setThreads(Integer.parseInt(forumInfo.getThreads()));
        forumModel.setTodayPosts(Integer.parseInt(forumInfo.getTodayposts()));
        return forumModel;
    }
}
