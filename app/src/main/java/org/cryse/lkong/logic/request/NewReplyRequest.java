package org.cryse.lkong.logic.request;

import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.restservice.model.LKNewPostResult;
import org.cryse.lkong.model.NewPostResult;
import org.cryse.lkong.utils.GsonUtils;

public class NewReplyRequest extends AbstractAuthedHttpRequest<NewPostResult> {
    private long mTid;
    private Long mPid;
    private String mContent;
    public NewReplyRequest(LKAuthObject authObject, long tid, Long pid, String content) {
        super(authObject);
        this.mTid = tid;
        this.mPid = pid;
        this.mContent = content;
    }

    public NewReplyRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long tid, Long pid, String content) {
        super(httpDelegate, authObject);
        this.mTid = tid;
        this.mPid = pid;
        this.mContent = content;
    }

    @Override
    protected Request buildRequest() throws Exception {
        FormBody.Builder builder= new FormBody.Builder()
                .add("type", "reply")
                .add("tid", Long.toString(mTid))
                .add("myrequestid", mPid == null ? String.format("thread_%d", mTid) : String.format("post_%d", mPid))
                .add("content", mContent);
        if(mPid != null) {
            builder.add("replyid", mPid.toString());
        }
        RequestBody formBody = builder.build();
        return new Request.Builder()
                .addHeader(ACCEPT_ENCODING, ACCEPT_ENCODING_GZIP)
                .url("http://lkong.cn/forum/index.php?mod=post")
                .post(formBody)
                .build();
    }

    @Override
    protected NewPostResult parseResponse(Response response) throws Exception {
        String responseBody = gzipToString(response);
        Gson gson = GsonUtils.getGson();
        LKNewPostResult lkNewPostResult = gson.fromJson(responseBody, LKNewPostResult.class);
        NewPostResult newPostResult = new NewPostResult();
        if(lkNewPostResult == null || !lkNewPostResult.isSuccess()) {
            newPostResult.setSuccess(false);
            newPostResult.setErrorMessage(lkNewPostResult != null ? lkNewPostResult.getError() : "");
        } else {
            newPostResult.setSuccess(true);
            newPostResult.setTid(lkNewPostResult.getTid());
            newPostResult.setPageCount(lkNewPostResult.getPage());
            newPostResult.setReplyCount(lkNewPostResult.getLou());
        }

        return newPostResult;
    }
}
