package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.TimelineListType;
import org.cryse.lkong.logic.restservice.model.LKTimelineData;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetTimelineRequest extends AbstractAuthedHttpRequest<List<TimelineModel>> {
    long mStart;
    int mListType;
    boolean mOnlyThread;
    public GetTimelineRequest(LKAuthObject authObject, long start, int listType, boolean onlyThread) {
        super(authObject);
        this.mStart = start;
        this.mListType = listType;
        this.mOnlyThread = onlyThread;
    }

    public GetTimelineRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long start, int listType, boolean onlyThread) {
        super(httpDelegate, authObject);
        this.mStart = start;
        this.mListType = listType;
        this.mOnlyThread = onlyThread;
    }

    @Override
    protected Request buildRequest() throws Exception {
        String url;
        if(mOnlyThread) {
            url = "http://lkong.cn/index/index.php?mod=data&sars=index/thread";
        } else {
            url = "http://lkong.cn/index.php" + TimelineListType.typeToRequestParam(mListType);
        }
        url = url + (mStart >= 0 ? "&nexttime=" + Long.toString(mStart) : "");
        return new Request.Builder()
                .addHeader(ACCEPT_ENCODING, ACCEPT_ENCODING_GZIP)
                .url(url)
                .build();
    }

    @Override
    protected List<TimelineModel> parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        Gson gson = GsonUtils.getGson();
        LKTimelineData lkTimelineData = gson.fromJson(responseString, LKTimelineData.class);
        if (lkTimelineData.getData() == null || lkTimelineData.getData().size() == 0)
            return new ArrayList<TimelineModel>();
        List<TimelineModel> timelineList = ModelConverter.toTimelineModel(lkTimelineData);
        Collections.reverse(timelineList);
        return timelineList;
    }
}
