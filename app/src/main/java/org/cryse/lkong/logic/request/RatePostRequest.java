package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.restservice.model.LKPostRateItem;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;
import org.json.JSONObject;

public class RatePostRequest extends AbstractAuthedHttpRequest<PostModel.PostRate> {
    private long mPostId;
    private int mScore;
    private String mReason;
    public RatePostRequest(LKAuthObject authObject, long postId, int score, String reason) {
        super(authObject);
        this.mPostId = postId;
        this.mScore = score;
        this.mReason = reason;
    }

    public RatePostRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long postId, int score, String reason) {
        super(httpDelegate, authObject);
        this.mPostId = postId;
        this.mScore = score;
        this.mReason = reason;
    }

    @Override
    protected Request buildRequest() throws Exception {
        String url = "http://lkong.cn/thread/index.php?mod=ajax&action=submitbox";
        FormBody.Builder builder= new FormBody.Builder()
                .add("request", String.format("rate_post_%d", mPostId))
                .add("num", Integer.toString(mScore))
                .add("reason", mReason);
        RequestBody formBody = builder.build();
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .post(formBody)
                .build();
    }

    @Override
    protected PostModel.PostRate parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        JSONObject rootObj = new JSONObject(responseString);
        JSONObject ratelogObj = rootObj.getJSONObject("ratelog");
        Gson gson = GsonUtils.getGson();
        LKPostRateItem lkPostRateItem = gson.fromJson(ratelogObj.toString(), LKPostRateItem.class);
        PostModel.PostRate postRate = ModelConverter.toPostRate(lkPostRateItem);
        return postRate;
    }
}
