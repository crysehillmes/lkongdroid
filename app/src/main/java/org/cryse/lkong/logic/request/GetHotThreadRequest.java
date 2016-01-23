package org.cryse.lkong.logic.request;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.model.HotThreadModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetHotThreadRequest extends AbstractAuthedHttpRequest<List<HotThreadModel>> {
    private boolean mDigest;
    public GetHotThreadRequest(LKAuthObject authObject, boolean digest) {
        super(authObject);
        this.mDigest = digest;
    }

    public GetHotThreadRequest(HttpDelegate httpDelegate, LKAuthObject authObject, boolean digest) {
        super(httpDelegate, authObject);
        this.mDigest = digest;
    }

    @Override
    protected Request buildRequest() throws Exception {
        String url = String.format("http://lkong.cn/index.php?mod=ajax&action=%s", mDigest ? "digest" : "hotthread");
        return new Request.Builder()
                .addHeader(ACCEPT_ENCODING, ACCEPT_ENCODING_GZIP)
                .url(url)
                .build();
    }

    @Override
    protected List<HotThreadModel> parseResponse(Response response) throws Exception {
        String responseBody = gzipToString(response);
        List<HotThreadModel> results = new ArrayList<>();
        JSONObject object = new JSONObject(responseBody);
        if(object.has("thread")) {
            JSONArray array = object.getJSONArray("thread");
            int size = array.length();
            for(int i = 0; i < size; i++) {
                JSONObject item = array.getJSONObject(i);
                HotThreadModel model = new HotThreadModel();
                if(item.has("tid")) {
                    model.tid = Long.valueOf(item.getString("tid"));
                }
                if(item.has("subject")) {
                    model.subject = item.getString("subject");
                }
                results.add(model);
            }
        }
        return results;
    }
}
