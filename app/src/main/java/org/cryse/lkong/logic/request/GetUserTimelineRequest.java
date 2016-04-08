package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import okhttp3.Request;
import okhttp3.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.restservice.model.LKTimelineData;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetUserTimelineRequest extends AbstractAuthedHttpRequest<List<TimelineModel>> {
    private long mUid;
    private long mStart;
    public GetUserTimelineRequest(LKAuthObject authObject, long uid, long start) {
        super(authObject);
        this.mUid = uid;
        this.mStart = start;
    }

    public GetUserTimelineRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long uid, long start) {
        super(httpDelegate, authObject);
        this.mUid = uid;
        this.mStart = start;
    }

    @Override
    protected Request buildRequest() throws Exception {
        String url = String.format("http://lkong.cn/user/index.php?mod=data&sars=user/%06d", mUid);
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
        if(lkTimelineData.getData() == null || lkTimelineData.getData().size() == 0)
            return new ArrayList<TimelineModel>();
        List<TimelineModel> timelineList = ModelConverter.toTimelineModel(lkTimelineData);
        Collections.reverse(timelineList);
        return timelineList;
    }
}
