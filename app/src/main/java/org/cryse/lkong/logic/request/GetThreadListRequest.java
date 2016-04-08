package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import okhttp3.Request;
import okhttp3.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.logic.ThreadListType;
import org.cryse.lkong.logic.restservice.model.LKForumThreadList;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;

import java.util.Collections;
import java.util.List;

public class GetThreadListRequest extends AbstractAuthedHttpRequest<List<ThreadModel>> {
    private long mForumId;
    private long mStartSortKey;
    private int mListType;

    public GetThreadListRequest(LKAuthObject authObject, long forumId, long startSortKey, int listType) {
        super(authObject);
        this.mForumId = forumId;
        this.mStartSortKey = startSortKey;
        this.mListType = listType;
    }

    public GetThreadListRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long forumId, long startSortKey, int listType) {
        super(httpDelegate, authObject);
        this.mForumId = forumId;
        this.mStartSortKey = startSortKey;
        this.mListType = listType;
    }

    @Override
    protected Request buildRequest() {
        String url = String.format(LKongWebConstants.FORUM_THREAD_LIST_URL, mForumId, ThreadListType.typeToRequestParam(mListType));
        url = url + (mStartSortKey >= 0 ? "&nexttime=" + Long.toString(mStartSortKey) : "");
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected List<ThreadModel> parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        Gson gson = GsonUtils.getGson();
        LKForumThreadList lKThreadList = gson.fromJson(responseString, LKForumThreadList.class);
        List<ThreadModel> threadList = ModelConverter.toForumThreadModel(lKThreadList, false);
        if(mListType == ThreadListType.TYPE_SORT_BY_POST)
            Collections.reverse(threadList);
        return threadList;
    }
}
