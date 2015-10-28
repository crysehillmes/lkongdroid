package org.cryse.lkong.logic.request;

import android.text.TextUtils;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.model.EditPostResult;
import org.json.JSONObject;

public class EditPostRequest extends AbstractAuthedHttpRequest<EditPostResult> {
    private long mTid;
    private long mPid;
    private String mAction;
    private String mTitle;
    private String mContent;
    public EditPostRequest(LKAuthObject authObject, long tid, long pid, String action, String title, String content) {
        super(authObject);
        this.mTid = tid;
        this.mPid = pid;
        this.mAction = action;
        this.mTitle = title;
        this.mContent = content;
    }

    public EditPostRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long tid, long pid, String action, String title, String content) {
        super(httpDelegate, authObject);
        this.mTid = tid;
        this.mPid = pid;
        this.mAction = action;
        this.mTitle = title;
        this.mContent = content;
    }

    @Override
    protected Request buildRequest() throws Exception {
        FormEncodingBuilder builder= new FormEncodingBuilder()
                .add("type", "edit")
                .add("tid", Long.toString(mTid))
                .add("pid", Long.toString(mPid))
                .add("ac", mAction)
                .add("content", mContent);
        if(!TextUtils.isEmpty(mTitle) && mAction.equalsIgnoreCase("thread")) {
            builder.add("title", mTitle);
        }
        RequestBody formBody = builder.build();
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url("http://lkong.cn/post/edit/index.php?mod=post")
                .post(formBody)
                .build();
    }

    @Override
    protected EditPostResult parseResponse(Response response) throws Exception {
        String responseBody = gzipToString(response);
        JSONObject jsonObject = new JSONObject(responseBody);
        EditPostResult editPostResult = new EditPostResult();
        editPostResult.setTid(jsonObject.getLong("tid"));
        editPostResult.setSuccess(jsonObject.getBoolean("success"));
        if (jsonObject.has("errorMessage"))
            editPostResult.setErrorMessage(jsonObject.getString("errorMessage"));

        return editPostResult;
    }
}
