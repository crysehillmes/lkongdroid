package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import okhttp3.Request;
import okhttp3.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.restservice.model.LKDataItemLocation;
import org.cryse.lkong.model.DataItemLocationModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;

public class GetDataItemLocationRequest extends AbstractAuthedHttpRequest<DataItemLocationModel> {
    private String mDataItem;
    public GetDataItemLocationRequest(LKAuthObject authObject, String dataItem) {
        super(authObject);
        this.mDataItem = dataItem;
    }

    public GetDataItemLocationRequest(HttpDelegate httpDelegate, LKAuthObject authObject, String dataItem) {
        super(httpDelegate, authObject);
        this.mDataItem = dataItem;
    }

    @Override
    protected Request buildRequest() throws Exception {
        String url = String.format("http://lkong.cn/index.php?mod=ajax&action=panelocation&dataitem=%s", mDataItem);
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected DataItemLocationModel parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        Gson gson = GsonUtils.getGson();
        LKDataItemLocation lkDataItemLocation = gson.fromJson(responseString, LKDataItemLocation.class);
        DataItemLocationModel locationModel= ModelConverter.toNoticeRateModel(lkDataItemLocation);
        clearCookies();
        return locationModel;
    }
}
