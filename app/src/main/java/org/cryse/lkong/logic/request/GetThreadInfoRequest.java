package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.logic.restservice.model.LKThreadInfo;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;
import org.cryse.lkong.account.LKAuthObject;

public class GetThreadInfoRequest extends AbstractAuthedHttpRequest<ThreadInfoModel> {
    private long mThreadId;
    public GetThreadInfoRequest(LKAuthObject authObject, long threadId) {
        super(authObject);
        this.mThreadId = threadId;
    }

    public GetThreadInfoRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long threadId) {
        super(httpDelegate, authObject);
        this.mThreadId = threadId;
    }

    @Override
    protected Request buildRequest() {
        String url = String.format(LKongWebConstants.THREAD_INFO_URL, mThreadId);
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected ThreadInfoModel parseResponse(Response response) throws Exception {
        Gson gson = GsonUtils.getGson();
        String responseString = gzipToString(response);
        LKThreadInfo lkThreadInfo = gson.fromJson(responseString, LKThreadInfo.class);
        return ModelConverter.toThreadInfoModel(lkThreadInfo);
    }
}
