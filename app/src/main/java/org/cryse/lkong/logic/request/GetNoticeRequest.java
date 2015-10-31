package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.restservice.model.LKNoticeResult;
import org.cryse.lkong.model.NoticeModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;

import java.util.List;

public class GetNoticeRequest extends AbstractAuthedHttpRequest<List<NoticeModel>> {
    private long mStart;
    public GetNoticeRequest(LKAuthObject authObject, long start) {
        super(authObject);
        this.mStart = start;
    }

    public GetNoticeRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long start) {
        super(httpDelegate, authObject);
        this.mStart = start;
    }

    @Override
    protected Request buildRequest() throws Exception {
        String url = "http://lkong.cn/index.php?mod=data&sars=my/notice";
        url = url + (mStart >= 0 ? "&nexttime=" + Long.toString(mStart) : "");
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();

    }

    @Override
    protected List<NoticeModel> parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        Gson gson = GsonUtils.getGson();
        LKNoticeResult lkNoticeResult = gson.fromJson(responseString, LKNoticeResult.class);
        return ModelConverter.toNoticeModel(lkNoticeResult);
    }
}
