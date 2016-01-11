package org.cryse.lkong.logic.request;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.json.JSONObject;

public class AddOrRemoveFavoriteRequest extends AbstractAuthedHttpRequest<Boolean> {
    private long mTid;
    private boolean mRemove;
    public AddOrRemoveFavoriteRequest(LKAuthObject authObject, long tid, boolean remove) {
        super(authObject);
        this.mTid = tid;
        this.mRemove = remove;
    }

    public AddOrRemoveFavoriteRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long tid, boolean remove) {
        super(httpDelegate, authObject);
        this.mTid = tid;
        this.mRemove = remove;
    }

    @Override
    protected Request buildRequest() throws Exception {
        String url = String.format("http://lkong.cn/index.php?mod=ajax&action=favorite&tid=%d", mTid);
        url = url + (mRemove ? "&type=-1" : "");
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected Boolean parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        JSONObject jsonObject = new JSONObject(responseString);
        if(!jsonObject.has("isfavorite")) return false;
        Boolean isFavorite = jsonObject.getInt("isfavorite") != 0;
        return isFavorite;
    }
}
