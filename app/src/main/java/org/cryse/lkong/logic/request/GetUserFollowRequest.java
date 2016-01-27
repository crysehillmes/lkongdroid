package org.cryse.lkong.logic.request;

import android.text.Html;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.model.SearchUserItem;
import org.cryse.lkong.model.converter.ModelConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GetUserFollowRequest extends AbstractAuthedHttpRequest<List<SearchUserItem>> {
    private long mUid;
    private boolean mFollower;
    private long mStartSortKey;
    public GetUserFollowRequest(LKAuthObject authObject, long uid, boolean follower, long startSortKey) {
        super(authObject);
        this.mUid = uid;
        this.mFollower = follower;
        this.mStartSortKey = startSortKey;
    }

    public GetUserFollowRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long uid, boolean follower, long startSortKey) {
        super(httpDelegate, authObject);
        this.mUid = uid;
        this.mFollower = follower;
        this.mStartSortKey = startSortKey;
    }

    @Override
    protected Request buildRequest() throws Exception {
        String url = String.format("http://lkong.cn/user/%06d/index.php?mod=data&sars=user/%06d/%s", mUid, mUid, mFollower ? "fans" : "follow");
        url = url + (mStartSortKey >= 0 ? "&nexttime=" + Long.toString(mStartSortKey) : "");
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected List<SearchUserItem> parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        List<SearchUserItem> users = new ArrayList<>();
        JSONObject object = new JSONObject(responseString);
        if(object.has("data")) {
            JSONArray array = object.getJSONArray("data");
            int size = array.length();
            for(int i = 0; i < size; i++) {
                JSONObject dataItem = array.getJSONObject(i);
                SearchUserItem item = new SearchUserItem();
                if(dataItem.has("id"))
                    item.setId(dataItem.getString("id"));
                if(dataItem.has("uid"))
                    item.setUserId(Long.valueOf(dataItem.getString("uid")));
                if(dataItem.has("username"))
                    item.setUserName(htmlToCharSequence(dataItem.getString("username").replace("<em>","").replace("</em>","")));
                if(dataItem.has("gender"))
                    item.setGender(dataItem.getInt("gender"));
                if(dataItem.has("sightml"))
                    item.setSignHtml(htmlToCharSequence(dataItem.getString("sightml")));
                if(dataItem.has("customstatus"))
                    item.setCustomStatus(htmlToCharSequence(dataItem.getString("customstatus")));
                if(dataItem.has("uid"))
                    item.setAvatarUrl(ModelConverter.uidToAvatarUrl(item.getUserId()));
                if(dataItem.has("sortkey"))
                    item.setSortKey(dataItem.getLong("sortkey"));
                users.add(item);
            }
        }
        Collections.sort(users, new SortKeyComparator());
        return users;
    }

    private CharSequence htmlToCharSequence(String html) {
        return Html.fromHtml(html);
    }

    public class SortKeyComparator implements Comparator<SearchUserItem> {

        @Override
        public int compare(SearchUserItem lhs, SearchUserItem rhs) {
            return (int)(rhs.getSortKey() - lhs.getSortKey());
        }
    }
}
