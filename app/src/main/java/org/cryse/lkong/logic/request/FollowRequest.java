package org.cryse.lkong.logic.request;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.model.FollowResult;
import org.cryse.lkong.utils.LKAuthObject;
import org.json.JSONObject;

public class FollowRequest extends AbstractAuthedHttpRequest<FollowResult> {
    private int mAction;
    private int mType;
    private long mId;
    public FollowRequest(LKAuthObject authObject, int action, int type, long id ) {
        super(authObject);
        this.mAction = action;
        this.mType = type;
        this.mId = id;
    }

    public FollowRequest(HttpDelegate httpDelegate, LKAuthObject authObject) {
        super(httpDelegate, authObject);
    }

    @Override
    protected Request buildRequest() {
        String followid;
        String action;
        switch (mType) {
            case FollowResult.TYPE_FORUM:
                followid = "fid-" + Long.toString(mId);
                break;
            case FollowResult.TYPE_THREAD:
                followid = "tid-" + Long.toString(mId);
                break;
            case FollowResult.TYPE_USER:
                followid = "uid-" + Long.toString(mId);
                break;
            default:
                throw new IllegalArgumentException("Unknown follow type.");
        }
        switch (mAction) {
            case FollowResult.ACTION_UNFOLLOW:
                action = "unfollow";
                break;
            case FollowResult.ACTION_FOLLOW:
            default:
                action = "follow";
                break;

        }
        FormEncodingBuilder builder= new FormEncodingBuilder()
                .add("followid", followid)
                .add("followtype", action);
        RequestBody formBody = builder.build();
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(LKongWebConstants.FOLLOW_URL)
                .post(formBody)
                .build();
    }

    @Override
    protected FollowResult parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        JSONObject responseJson = new JSONObject(responseString);
        if(responseJson.has("ok") && responseJson.getBoolean("ok")) {
            FollowResult result = new FollowResult();
            String action = responseJson.getString("dos");
            if(action.equals("follow")) {
                result.setAction(FollowResult.ACTION_FOLLOW);
            } else {
                result.setAction(FollowResult.ACTION_UNFOLLOW);
            }
            String type = responseJson.getString("type");
            switch (type) {
                case "fid":
                    result.setType(FollowResult.TYPE_FORUM);
                    break;
                case "tid":
                    result.setType(FollowResult.TYPE_THREAD);
                    break;
                case "uid":
                    result.setType(FollowResult.TYPE_USER);
                    break;
            }
            long id = responseJson.getLong("theid");
            result.setId(id);
            return result;
        }
        return null;
    }


}
