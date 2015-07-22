package org.cryse.lkong.data.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.model.FollowedForum;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectContentValues;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectCursor;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectSelection;
import org.cryse.lkong.data.provider.followedforum.FollowedForumContentValues;
import org.cryse.lkong.data.provider.followedforum.FollowedForumCursor;
import org.cryse.lkong.data.provider.followedforum.FollowedForumModel;
import org.cryse.lkong.data.provider.followedforum.FollowedForumSelection;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.PunchResult;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LKongDatabaseSqliteImpl implements LKongDatabase {
    Gson mGson;
    ContentResolver mContentResolver;
    @Inject
    public LKongDatabaseSqliteImpl(@ApplicationContext Context context) {
        this.mGson = new Gson();
        this.mContentResolver = context.getContentResolver();
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public boolean isOpen() throws Exception {
        return mContentResolver != null;
    }

    private static final String CACHE_KEY_FORUM_LIST = "cache_forum_list";
    private static final String CACHE_KEY_PUNCH_RESULT = "cache_punch_result";
    private static final String CACHE_KEY_NOTIFICATION_COUNT = "cache_notification_count";
    @Override
    public void cacheForumList(List<ForumModel> forumModels) throws Exception {
        String json = mGson.toJson(forumModels, new TypeToken<List<ForumModel>>() {
        }.getType());
        CacheObjectContentValues values = new CacheObjectContentValues();
        values.putCacheKey(CACHE_KEY_FORUM_LIST).putCacheValue(json);
        values.insert(mContentResolver);
    }

    @Override
    public List<ForumModel> getCachedForumList() throws Exception {
        String json = getCachedValue(CACHE_KEY_FORUM_LIST);
        if(!TextUtils.isEmpty(json)) {
            return mGson.fromJson(json, new TypeToken<List<ForumModel>>() {}.getType());
        } else {
            return null;
        }
    }

    @Override
    public void removeCachedForumList() throws Exception {
        CacheObjectSelection cacheSelection = new CacheObjectSelection();
        cacheSelection.cacheKey(CACHE_KEY_FORUM_LIST).delete(mContentResolver);
    }

    @Override
    public boolean isCachedForumList() throws Exception {
        CacheObjectSelection cacheSelection = new CacheObjectSelection();
        CacheObjectCursor cursor = cacheSelection.cacheKey(CACHE_KEY_FORUM_LIST).query(mContentResolver);
        boolean exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }

    @Override
    public void followForum(FollowedForumModel model) throws Exception {
        FollowedForumContentValues values = new FollowedForumContentValues();
        values.putUserId(model.getUserId())
                .putForumId(model.getForumId())
                .putForumName(model.getForumName())
                .putForumIcon(model.getForumIcon())
                .putForumSortValue(model.getForumSortValue());
        values.insert(mContentResolver);
    }

    @Override
    public void removePinnedForum(long uid, long fid) throws Exception {
        FollowedForumSelection followedForumSelection = new FollowedForumSelection();
        followedForumSelection.userId(uid).and().forumId(fid).delete(mContentResolver);
    }

    @Override
    public void removePinnedForums(long uid) throws Exception {
        FollowedForumSelection followedForumSelection = new FollowedForumSelection();
        followedForumSelection.userId(uid).delete(mContentResolver);
    }

    @Override
    public boolean isForumPinned(long uid, long fid) throws Exception {
        FollowedForumSelection forumSelection = new FollowedForumSelection();
        FollowedForumCursor cursor = forumSelection.userId(uid).and().forumId(fid).query(mContentResolver);
        boolean exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }

    @Override
    public List<FollowedForum> loadAllForUser(long uid) throws Exception {
        FollowedForumSelection forumSelection = new FollowedForumSelection();
        forumSelection.userId(uid);
        FollowedForumCursor cursor = forumSelection.query(mContentResolver);
        List<FollowedForum> result = new ArrayList<>(cursor.getCount());
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            result.add(new FollowedForum(cursor));
        }
        cursor.close();
        return result;
    }

    @Override
    public void cachePunchResult(PunchResult punchResult) {
        String json = mGson.toJson(punchResult, PunchResult.class);
        CacheObjectContentValues values = new CacheObjectContentValues();
        values.putCacheKey(generatePunchResultKey(punchResult.getUserId()))
                .putCacheValue(json);
        values.insert(mContentResolver);
    }

    @Override
    public void removePunchResult(long uid) {
        CacheObjectSelection cacheSelection = new CacheObjectSelection();
        cacheSelection.cacheKey(generatePunchResultKey(uid)).delete(mContentResolver);
    }

    @Override
    public PunchResult getCachePunchResult(long uid) {
        String json = getCachedValue(generatePunchResultKey(uid));
        if(!TextUtils.isEmpty(json)) {
            return mGson.fromJson(json, PunchResult.class);
        } else {
            return null;
        }
    }

    @Override
    public void cacheNoticeCount(long uid, NoticeCountModel noticeCountModel) {
        String json = mGson.toJson(noticeCountModel, NoticeCountModel.class);
        CacheObjectContentValues values = new CacheObjectContentValues();
        values.putCacheKey(generateNoticeCountKey(uid))
                .putCacheValue(json);
        values.insert(mContentResolver);
    }

    @Override
    public void removeNoticeCount(long uid) {
        CacheObjectSelection cacheSelection = new CacheObjectSelection();
        cacheSelection.cacheKey(generateNoticeCountKey(uid)).delete(mContentResolver);
    }

    @Override
    public NoticeCountModel loadNoticeCount(long uid) {
        String json = getCachedValue(generateNoticeCountKey(uid));
        if(!TextUtils.isEmpty(json)) {
            return mGson.fromJson(json, NoticeCountModel.class);
        } else {
            return null;
        }
    }

    private String generatePunchResultKey(long uid) {
        return CACHE_KEY_PUNCH_RESULT + "|||" + uid;
    }

    private String generateNoticeCountKey(long uid) {
        return CACHE_KEY_NOTIFICATION_COUNT + "|||" + uid;
    }

    private String getCachedValue(String key) {
        CacheObjectSelection cacheSelection = new CacheObjectSelection();
        cacheSelection.cacheKey(key);
        CacheObjectCursor cursor = cacheSelection.query(mContentResolver);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String json = cursor.getCacheValue();
            cursor.close();
            return json;
        } else {
            return null;
        }
    }
}
