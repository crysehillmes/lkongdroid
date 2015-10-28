package org.cryse.lkong.logic.request;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.restservice.model.LKForumThreadList;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

public class GetFavoritesRequest extends AbstractAuthedHttpRequest<List<ThreadModel>> {
    private long mStart;
    public GetFavoritesRequest(LKAuthObject authObject, long start) {
        super(authObject);
        this.mStart = start;
    }

    public GetFavoritesRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long start) {
        super(httpDelegate, authObject);
        this.mStart = start;
    }

    @Override
    protected Request buildRequest() throws Exception {
        String url = "http://lkong.cn/index.php?mod=data&sars=my/favorite";
        url = url + (mStart >= 0 ? "&nexttime=" + Long.toString(mStart) : "");
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected List<ThreadModel> parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        Gson gson = GsonUtils.getGson();
        LKForumThreadList lKThreadList = gson.fromJson(responseString, LKForumThreadList.class);
        if(lKThreadList.getData() == null || lKThreadList.getData().size() == 0)
            return new ArrayList<ThreadModel>();
        List<ThreadModel> favorites = ModelConverter.toForumThreadModel(lKThreadList, true);
        return favorites;
    }
}
