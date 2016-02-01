package org.cryse.lkong.logic.request;

import android.text.TextUtils;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.model.FollowInfo;
import org.cryse.lkong.utils.GzipUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetFollowInfoRequest extends AbstractAuthedHttpRequest<FollowInfo> {
    public GetFollowInfoRequest(LKAuthObject authObject) {
        super(authObject);
    }

    public GetFollowInfoRequest(HttpDelegate httpDelegate, LKAuthObject authObject) {
        super(httpDelegate, authObject);
    }

    @Override
    protected Request buildRequest() throws Exception {
        String url = "http://lkong.cn";
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected FollowInfo parseResponse(Response response) throws Exception {
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String responseString = GzipUtils.responseToString(response);
        FollowInfo result = new FollowInfo();
        Document document = Jsoup.parseBodyFragment(responseString);
        Elements elements = document.select("#setfollows");
        if(elements != null && elements.size() > 0) {
            Element element = elements.get(0);
            String followedJson = element.html();
            JSONObject rootObject = new JSONObject(followedJson);

            // Get followed forums
            JSONArray forumsArray = rootObject.getJSONArray("fid");
            int fidsCount = forumsArray.length();
            long[] fids = new long[fidsCount];
            for(int i = 0; i <= fidsCount - 1; i++) {
                String value = forumsArray.getString(i);
                if(!TextUtils.isEmpty(value) && TextUtils.isDigitsOnly(value))
                    fids[i] = Long.valueOf(value);
                else
                    fids[i] = -1;
            }
            result.followedForumIds = fids;

            // Get followed users
            JSONArray usersArray = rootObject.getJSONArray("uid");
            int uidsCount = usersArray.length();
            long[] uids = new long[uidsCount];
            for(int i = 0; i <= uidsCount - 1; i++) {
                String value = usersArray.getString(i);
                if(!TextUtils.isEmpty(value) && TextUtils.isDigitsOnly(value))
                    uids[i] = Long.valueOf(value);
                else
                    uids[i] = -1;
            }
            result.followedUserIds = uids;

            // Get followed users
            JSONArray threadsArray = rootObject.getJSONArray("tid");
            int tidsCount = threadsArray.length();
            long[] tids = new long[tidsCount];
            for(int i = 0; i <= tidsCount - 1; i++) {
                String value = threadsArray.getString(i);
                if(!TextUtils.isEmpty(value) && TextUtils.isDigitsOnly(value))
                    tids[i] = Long.valueOf(value);
                else
                    tids[i] = -1;
            }
            result.followedThreadIds = tids;

            // Get followed users
            JSONArray blacksArray = rootObject.getJSONArray("black");
            int blacksCount = blacksArray.length();
            long[] blacks = new long[blacksCount];
            for(int i = 0; i <= blacksCount - 1; i++) {
                String value = blacksArray.getString(i);
                if(!TextUtils.isEmpty(value) && TextUtils.isDigitsOnly(value))
                    blacks[i] = Long.valueOf(value);
                else
                    blacks[i] = -1;
            }
            result.blacklistUserIds = blacks;
        }
        return result;
    }
}
