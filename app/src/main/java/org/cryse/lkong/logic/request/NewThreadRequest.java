package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.restservice.model.LKNewThreadResult;
import org.cryse.lkong.model.NewThreadResult;
import org.cryse.lkong.utils.GsonUtils;

public class NewThreadRequest extends AbstractAuthedHttpRequest<NewThreadResult> {
    private String mTitle;
    private long mFid;
    private String mContent;
    private boolean mFollow;
    public NewThreadRequest(LKAuthObject authObject, String title, long fid, String content, boolean follow) {
        super(authObject);
        this.mTitle = title;
        this.mFid = fid;
        this.mContent = content;
        this.mFollow = follow;
    }

    public NewThreadRequest(HttpDelegate httpDelegate, LKAuthObject authObject, String title, long fid, String content, boolean follow) {
        super(httpDelegate, authObject);
        this.mTitle = title;
        this.mFid = fid;
        this.mContent = content;
        this.mFollow = follow;
    }

    @Override
    protected Request buildRequest() throws Exception {
        FormBody.Builder builder= new FormBody.Builder()
                .add("title", mTitle)
                .add("type", "new")
                .add("fid", Long.toString(mFid))
                .add("content", mContent)
                .add("follow", mFollow ? "1" : "0");
        RequestBody formBody = builder.build();
        return new Request.Builder()
                .addHeader(ACCEPT_ENCODING, ACCEPT_ENCODING_GZIP)
                .url("http://lkong.cn/post/new/index.php?mod=post")
                .post(formBody)
                .build();
    }

    @Override
    protected NewThreadResult parseResponse(Response response) throws Exception {
        String responseBody = gzipToString(response);
        Gson gson = GsonUtils.getGson();
        LKNewThreadResult lkNewThreadResult = gson.fromJson(responseBody, LKNewThreadResult.class);
        NewThreadResult newThreadResult = new NewThreadResult();
        if(lkNewThreadResult == null || !lkNewThreadResult.isSuccess()) {
            newThreadResult.setSuccess(false);
            newThreadResult.setErrorMessage(lkNewThreadResult != null ? lkNewThreadResult.getError() : "");
        } else {
            newThreadResult.setSuccess(true);
            newThreadResult.setTid(lkNewThreadResult.getTid());
            newThreadResult.setType(lkNewThreadResult.getType());
        }
        return newThreadResult;
    }
}
