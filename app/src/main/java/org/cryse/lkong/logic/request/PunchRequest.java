package org.cryse.lkong.logic.request;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.model.PunchResult;
import org.json.JSONObject;

import java.util.Date;

public class PunchRequest extends AbstractAuthedHttpRequest<PunchResult> {
    public PunchRequest(LKAuthObject authObject) {
        super(authObject);
    }

    public PunchRequest(HttpDelegate httpDelegate, LKAuthObject authObject) {
        super(httpDelegate, authObject);
    }

    @Override
    protected Request buildRequest() throws Exception {
       String url = "http://lkong.cn/index.php?mod=ajax&action=punch";
       return new Request.Builder()
                .addHeader(ACCEPT_ENCODING, ACCEPT_ENCODING_GZIP)
                .url(url)
                .build();
    }

    @Override
    protected PunchResult parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        JSONObject jsonObject = new JSONObject(responseString);
        if(!jsonObject.has("punchtime") || !jsonObject.has("punchday")) {
            clearCookies();
            return null;
        }
        PunchResult result = new PunchResult();
        long dateLong = jsonObject.getLong("punchtime");
        int punchDay = jsonObject.getInt("punchday");
        result.setPunchDay(punchDay);
        result.setPunchTime(new Date(dateLong * 1000));
        result.setUserId(getAuthObject().getUserId());
        return result;
    }
}
