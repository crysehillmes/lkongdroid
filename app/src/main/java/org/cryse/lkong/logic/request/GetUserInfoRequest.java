package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.logic.restservice.model.LKUserInfo;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;
import org.cryse.lkong.utils.LKAuthObject;

public class GetUserInfoRequest extends AbstractAuthedHttpRequest<UserInfoModel> {
    private long mUserId;
    public GetUserInfoRequest(LKAuthObject authObject, long userId) {
        super(authObject);
        this.mUserId = userId;
    }

    public GetUserInfoRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long userId) {
        super(httpDelegate, authObject);
        this.mUserId = userId;
    }

    @Override
    protected Request buildRequest() {
        String url = String.format(LKongWebConstants.USER_CONFIG_URL, mUserId);
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected UserInfoModel parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        Gson gson = GsonUtils.getGson();
        LKUserInfo lkUserInfo = gson.fromJson(responseString, LKUserInfo.class);
        return ModelConverter.toUserInfoModel(lkUserInfo);
    }
}
