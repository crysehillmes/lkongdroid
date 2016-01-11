package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.restservice.model.LKForumThreadList;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;

import java.util.List;

public class GetUserThreadsRequest extends AbstractAuthedHttpRequest<List<ThreadModel>> {
    private long mUid;
    private long mStart;
    private boolean mIsDigest;
    public GetUserThreadsRequest(LKAuthObject authObject, long uid, long start, boolean isDigest) {
        super(authObject);
        this.mUid = uid;
        this.mStart = start;
        this.mIsDigest = isDigest;
    }

    public GetUserThreadsRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long uid, long start, boolean isDigest) {
        super(httpDelegate, authObject);
        this.mUid = uid;
        this.mStart = start;
        this.mIsDigest = isDigest;
    }

    @Override
    protected Request buildRequest() throws Exception {
        String url = String.format("http://lkong.cn/user/%06d/index.php?mod=data&sars=user/%06d/", mUid, mUid);
        url = url + (mIsDigest ? "digest" : "thread");
        url = url + (mStart >= 0 ? "&nexttime=" + Long.toString(mStart) : "");
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
        for(ThreadModel model : threadList) {
            model.setUid(mUid);
            model.setUserIcon(ModelConverter.uidToAvatarUrl(mUid));
        }
        return threadList;
    }
}
