package org.cryse.lkong.logic.request;

import okhttp3.Request;
import okhttp3.Response;

import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.model.PrivateChatConfigModel;
import org.cryse.lkong.model.PrivateChatModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.account.LKAuthObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GetPrivateChatListRequest extends AbstractAuthedHttpRequest<List<PrivateChatModel>> {
    private long mStartSortKey;
    public GetPrivateChatListRequest(LKAuthObject authObject, long startSortKey) {
        super(authObject);
        this.mStartSortKey = startSortKey;
    }

    public GetPrivateChatListRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long startSortKey) {
        super(httpDelegate, authObject);
        this.mStartSortKey = startSortKey;
    }

    @Override
    protected Request buildRequest() {
        String url = LKongWebConstants.PRIVATE_CHAT_LIST_URL;
        url = url + (mStartSortKey >= 0 ? "&nexttime=" + Long.toString(mStartSortKey) : "");
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected List<PrivateChatModel> parseResponse(Response response) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<PrivateChatModel> results = new ArrayList<PrivateChatModel>();
        String responseString = gzipToString(response);
        JSONObject rootObject = new JSONObject(responseString);
        if(!rootObject.has("data")) {
            return results;
        }
        JSONArray jsonArray = rootObject.getJSONArray("data");

        int dataSetLength = jsonArray.length();
        for(int i = 0; i < dataSetLength; i++ ) {
            PrivateChatModel model = new PrivateChatModel();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            model.setUserId(getAuthObject().getUserId());
            if(jsonObject.has("uid"))
                model.setTargetUserId(jsonObject.getLong("uid"));
            if(jsonObject.has("username"))
                model.setUserName(jsonObject.getString("username"));
            if(jsonObject.has("typeId"))
                model.setTypeId(jsonObject.getLong("typeId"));
            if(jsonObject.has("sortkey"))
                model.setSortKey(jsonObject.getLong("sortkey"));
            if(jsonObject.has("message"))
                model.setMessage(jsonObject.getString("message"));
            if (jsonObject.has("id"))
                model.setId(jsonObject.getString("id"));
            if (jsonObject.has("dateline"))
                model.setDateline(dateFormat.parse(jsonObject.getString("dateline")));
            model.setTargetUserAvatar(ModelConverter.uidToAvatarUrl(model.getTargetUserId()));

            if(getAuthObject().getUserName().equals(model.getUserName())) {
                // 错误的目标用户名，需要手动读取
                GetPrivateMessageConfigRequest request = new GetPrivateMessageConfigRequest(getAuthObject(), model.getTargetUserId());
                PrivateChatConfigModel configModel = request.execute();
                model.setTargetUserName(configModel.getTargetUserName());
                model.setUserName(getAuthObject().getUserName());
            } else {
                model.setTargetUserName(model.getUserName());
                model.setUserName(getAuthObject().getUserName());
            }
            results.add(model);
        }
        return results;
    }
}
