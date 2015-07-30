package org.cryse.lkong.logic.request;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.model.PrivateChatConfigModel;
import org.cryse.lkong.utils.LKAuthObject;
import org.json.JSONObject;

public class GetPrivateMessageConfigRequest extends AbstractAuthedHttpRequest<PrivateChatConfigModel> {
    private long mTargetUserId;
    public GetPrivateMessageConfigRequest(LKAuthObject authObject, long targetUserId) {
        super(authObject);
        this.mTargetUserId = targetUserId;
    }

    public GetPrivateMessageConfigRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long targetUserId) {
        super(httpDelegate, authObject);
        this.mTargetUserId = targetUserId;
    }

    @Override
    protected Request buildRequest() {
        String url = String.format(LKongWebConstants.PRIVATE_CHAT_CONFIG_URL, mTargetUserId);
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected PrivateChatConfigModel parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        JSONObject rootObject = new JSONObject(responseString);
        PrivateChatConfigModel configModel = new PrivateChatConfigModel();
        if(!rootObject.has("isok")) return null;
        if(rootObject.has("uid"))
            configModel.setUserId(rootObject.getLong("uid"));
        if(rootObject.has("username"))
            configModel.setUsername(rootObject.getString("username"));
        if(rootObject.has("touid"))
            configModel.setTargetUserId(rootObject.getLong("touid"));
        if(rootObject.has("touser"))
            configModel.setTargetUserName(rootObject.getString("touser"));
        return configModel;
    }
}
