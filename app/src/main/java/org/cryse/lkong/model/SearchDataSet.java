package org.cryse.lkong.model;

import android.text.Html;

import org.cryse.lkong.model.converter.ModelConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchDataSet {
    public static final int TYPE_POST = 111;
    public static final int TYPE_USER = 112;
    public static final int TYPE_GROUP = 113;

    private int dataType;
    private long nextTime;
    private List<AbstractSearchResult> searchResultItems = new ArrayList<>();

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public long getNextTime() {
        return nextTime;
    }

    public void setNextTime(long nextTime) {
        this.nextTime = nextTime;
    }

    public List<AbstractSearchResult> getSearchResultItems() {
        return searchResultItems;
    }

    public void parseData(String data) throws JSONException {
        JSONObject rootObject = new JSONObject(data);
        Long nextTime = rootObject.getLong("nexttime");
        if(rootObject.has("isend")) {
            if(rootObject.getInt("isend") == 1) {
                nextTime = 0l;
            }
        }
        setNextTime(nextTime);
        String tmpSignature = rootObject.getString("tmp");
        if(tmpSignature.equalsIgnoreCase("d_forum")) {
            parsePostDataSet(rootObject.getJSONArray("data"));
        } else if(tmpSignature.equalsIgnoreCase("d_user")) {
            parseUserDataSet(rootObject.getJSONArray("data"));
        } else if(tmpSignature.equalsIgnoreCase("d_group")) {
            parseGroupDataSet(rootObject.getJSONArray("data"));
        } else
            throw new IllegalArgumentException("Wrong json input.");
    }

    private void parsePostDataSet(JSONArray dataArray) throws JSONException {
        setDataType(TYPE_POST);
        searchResultItems.clear();
        int count = dataArray.length();
        for(int i = 0; i < count; i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);
            SearchPostItem item = new SearchPostItem();
            item.setId(jsonObject.getString("id"));
            item.setSortKey(jsonObject.getLong("sortkey"));
            item.setSubject(Html.fromHtml(jsonObject.getString("subject")));
            item.setReplyCount(Integer.valueOf(jsonObject.getString("replynum")));
            item.setUserId(Long.valueOf(jsonObject.getString("uid")));
            item.setUserName(jsonObject.getString("username"));
            searchResultItems.add(item);
        }
    }

    private void parseUserDataSet(JSONArray dataArray) throws JSONException {
        setDataType(TYPE_USER);
        searchResultItems.clear();
        int count = dataArray.length();
        for(int i = 0; i < count; i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);
            SearchUserItem item = new SearchUserItem();
            item.setId(jsonObject.getString("id"));
            item.setUserId(Long.valueOf(jsonObject.getString("uid")));
            item.setUserName(htmlToCharSequence(jsonObject.getString("username")));
            item.setGender(jsonObject.getInt("gender"));
            item.setSignHtml(htmlToCharSequence(jsonObject.getString("sightml")));
            item.setCustomStatus(htmlToCharSequence(jsonObject.getString("customstatus")));
            item.setAvatarUrl(ModelConverter.uidToAvatarUrl(item.getUserId()));
            searchResultItems.add(item);
        }
    }

    private void parseGroupDataSet(JSONArray dataArray) throws JSONException {
        setDataType(TYPE_GROUP);
        searchResultItems.clear();
        int count = dataArray.length();
        for(int i = 0; i < count; i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);
            SearchGroupItem item = new SearchGroupItem();
            item.setId(jsonObject.getString("id"));
            item.setForumId(Long.valueOf(jsonObject.getString("fid")));
            item.setFansCount(Integer.valueOf(jsonObject.getString("fansnum")));
            item.setGroupName(htmlToCharSequence(jsonObject.getString("name")));
            item.setGroupDescription(htmlToCharSequence(jsonObject.getString("description")));
            item.setIconUrl(ModelConverter.fidToForumIconUrl(item.getForumId()));
            searchResultItems.add(item);
        }
    }

    private CharSequence htmlToCharSequence(String html) {
        return Html.fromHtml(html);
    }
}
