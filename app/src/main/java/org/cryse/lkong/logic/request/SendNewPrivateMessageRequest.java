package org.cryse.lkong.logic.request;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.model.SendNewPrivateMessageResult;
import org.cryse.lkong.account.LKAuthObject;
import org.json.JSONObject;

public class SendNewPrivateMessageRequest extends AbstractAuthedHttpRequest<SendNewPrivateMessageResult> {
    private long mTargetUserId;
    private String mTargetUserName;
    private String mMessage;
    public SendNewPrivateMessageRequest(LKAuthObject authObject, long targetUserId, String targetUserName, String message) {
        super(authObject);
        this.mTargetUserId = targetUserId;
        this.mTargetUserName = targetUserName;
        this.mMessage = message;
    }

    public SendNewPrivateMessageRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long targetUserId, String targetUserName, String message) {
        super(httpDelegate, authObject);
        this.mTargetUserId = targetUserId;
        this.mTargetUserName = targetUserName;
        this.mMessage = message;
    }

    @Override
    protected Request buildRequest() {
        FormEncodingBuilder builder= new FormEncodingBuilder()
                .add("message", mMessage)
                .add("request", "pm_" + Long.toString(mTargetUserId));
        RequestBody formBody = builder.build();
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(LKongWebConstants.SUBMIT_BOX_URL)
                .post(formBody)
                .build();
    }

    @Override
    protected SendNewPrivateMessageResult parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        SendNewPrivateMessageResult result = new SendNewPrivateMessageResult(
                getAuthObject().getUserId(),
                getAuthObject().getUserName(),
                mTargetUserId,
                mTargetUserName,
                false);
        JSONObject jsonObject = new JSONObject(responseString);
        if(jsonObject.has("type") && jsonObject.has("uid") && !jsonObject.has("error")) {
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
        }
        return result;
    }
}
