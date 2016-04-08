package org.cryse.lkong.logic.request;

import okhttp3.Request;
import okhttp3.Response;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.LKongWebConstants;
import org.cryse.lkong.model.SearchDataSet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchRequest extends AbstractAuthedHttpRequest<SearchDataSet> {
    private long mStart;
    private String mQueryString;
    public SearchRequest(LKAuthObject authObject, long start, String queryString) {
        super(authObject);
        this.mStart = start;
        this.mQueryString = queryString;
    }

    public SearchRequest(HttpDelegate httpDelegate, LKAuthObject authObject, long start, String queryString) {
        super(httpDelegate, authObject);
        this.mStart = start;
        this.mQueryString = queryString;
    }

    @Override
    protected Request buildRequest() {
        String url;
        try {
            url = LKongWebConstants.SEARCH_URL + URLEncoder.encode(mQueryString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            url = LKongWebConstants.SEARCH_URL + mQueryString;
        }
        if(!mQueryString.startsWith("@") && !mQueryString.startsWith("#")) {
            url = url + "/time";
        }
        if(mStart > 0) {
            url = url + "&nexttime=" + Long.toString(mStart);
        }
        return new Request.Builder()
                .addHeader("Accept-Encoding", "gzip")
                .url(url)
                .build();
    }

    @Override
    protected SearchDataSet parseResponse(Response response) throws Exception {
        String responseString = gzipToString(response);
        SearchDataSet dataSet = new SearchDataSet();
        dataSet.parseData(responseString);
        clearCookies();
        return dataSet;
    }
}
