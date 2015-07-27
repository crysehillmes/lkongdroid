package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.logic.restservice.model.LKPostList;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;
import org.cryse.lkong.utils.LKAuthObject;

import java.util.List;

public class GetThreadPostListRequest extends AbstractAuthedHttpRequest<List<PostModel>> {
    private long mThreadId;
    private int mPage;
    public GetThreadPostListRequest(LKAuthObject authObject, long tid, int page) {
        super(authObject);
        this.mThreadId = tid;
        this.mPage = page;
    }

    public GetThreadPostListRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long tid, int page) {
        super(httpDelegate, authObject);
        this.mThreadId = tid;
        this.mPage = page;
    }

    @Override
    protected Request buildRequest() {
        String url = String.format(LKongWebConstants.THREAD_POST_LIST_URL, mThreadId, mPage);
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected List<PostModel> parseResponse(Response response) throws Exception {
        Gson gson = GsonUtils.getGson();
        String responseString = gzipToString(response);
        LKPostList lkPostList = gson.fromJson(responseString, LKPostList.class);
        return ModelConverter.toPostModelList(lkPostList);
    }
}
