package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import okhttp3.Request;
import okhttp3.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.restservice.model.LKNoticeRateResult;
import org.cryse.lkong.model.NoticeRateModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;

import java.util.List;

public class GetNoticeRateLogRequest extends AbstractAuthedHttpRequest<List<NoticeRateModel>> {
    private long mStart;
    public GetNoticeRateLogRequest(LKAuthObject authObject, long start) {
        super(authObject);
        this.mStart = start;
    }

    public GetNoticeRateLogRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long start) {
        super(httpDelegate, authObject);
        this.mStart = start;
    }

    @Override
    protected Request buildRequest() throws Exception {
        String url = "http://lkong.cn/index.php?mod=data&sars=my/rate";
        url = url + (mStart >= 0 ? "&nexttime=" + Long.toString(mStart) : "");
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected List<NoticeRateModel> parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        Gson gson = GsonUtils.getGson();
        LKNoticeRateResult lkNoticeRateResult = gson.fromJson(responseString, LKNoticeRateResult.class);
        List<NoticeRateModel> noticeRates= ModelConverter.toNoticeRateModel(lkNoticeRateResult);
        return noticeRates;
    }
}
