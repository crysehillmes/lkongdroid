package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import okhttp3.Request;
import okhttp3.Response;

import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.logic.restservice.model.LKCheckNoticeCountResult;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;
import org.cryse.lkong.account.LKAuthObject;
import org.json.JSONObject;

public class CheckNoticeCountRequest extends AbstractAuthedHttpRequest<NoticeCountModel> {
    public CheckNoticeCountRequest(LKAuthObject authObject) {
        super(authObject);
    }

    public CheckNoticeCountRequest(HttpDelegate httpDelegate, LKAuthObject authObject) {
        super(httpDelegate, authObject);
    }

    @Override
    protected Request buildRequest() {
        String url = LKongWebConstants.CHECK_NOTICE_COUNT_URL;
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected NoticeCountModel parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        Gson gson = GsonUtils.getGson();
        if(responseString.contains("\"error\":")) {
            NoticeCountModel errorModel = new NoticeCountModel();
            errorModel.setUserId(getAuthObject().getUserId());
            JSONObject jsonObject = new JSONObject(responseString);
            String errorMessage = jsonObject.getString("error");
            errorModel.setSuccess(false);
            errorModel.setErrorMessage(errorMessage);
            return errorModel;
        }

        LKCheckNoticeCountResult lkCheckNoticeCountResult = gson.fromJson(responseString, LKCheckNoticeCountResult.class);
        NoticeCountModel noticeCountModel = ModelConverter.toNoticeCountModel(lkCheckNoticeCountResult);
        noticeCountModel.setUserId(getAuthObject().getUserId());
        clearCookies();
        return noticeCountModel;
    }
}
