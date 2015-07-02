package org.cryse.lkong.data.impl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.dao.CacheObjectDao;
import org.cryse.lkong.data.dao.PinnedForumDao;
import org.cryse.lkong.data.dao.UserAccountDao;
import org.cryse.lkong.data.model.PinnedForumEntity;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.PunchResult;

import java.util.List;

import javax.inject.Inject;

public class LKongDatabaseSqliteImpl implements LKongDatabase {
    CacheObjectDao mCacheObjectDao;
    UserAccountDao mUserAccountDao;
    PinnedForumDao mPinnedForumDao;
    Gson mGson;

    @Inject
    public LKongDatabaseSqliteImpl(CacheObjectDao cacheObjectDao, UserAccountDao userAccountDao, PinnedForumDao pinnedForumDao) {
        this.mCacheObjectDao = cacheObjectDao;
        this.mUserAccountDao = userAccountDao;
        this.mPinnedForumDao = pinnedForumDao;
        this.mGson = new Gson();
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public boolean isOpen() throws Exception {
        return mCacheObjectDao.isOpen() && mUserAccountDao.isOpen();
    }

    @Override
    public void addUserAccount(UserAccountEntity userAccountEntity) throws Exception {
        mUserAccountDao.insertOrReplace(userAccountEntity);
    }

    @Override
    public void updateUserAccount(UserAccountEntity userAccountEntity) throws Exception {
        mUserAccountDao.update(userAccountEntity);
    }

    @Override
    public UserAccountEntity getUserAccount(long uid) throws Exception {
        return mUserAccountDao.load(uid);
    }

    @Override
    public List<UserAccountEntity> getAllUserAccounts() throws Exception {
        return mUserAccountDao.loadAll();
    }

    @Override
    public boolean isUserAccountExist(long uid) throws Exception {
        return mUserAccountDao.exist(uid);
    }

    @Override
    public void removeUserAccount(long uid) throws Exception {
        mUserAccountDao.delete(uid);
    }

    private static final String CACHE_KEY_FORUM_LIST = "cache_forum_list";
    private static final String CACHE_KEY_PUNCH_RESULT = "cache_forum_list";
    private static final String CACHE_KEY_NOTIFICATION_COUNT = "cache_notification_count";
    @Override
    public void cacheForumList(List<ForumModel> forumModels) throws Exception {
        String json = mGson.toJson(forumModels, new TypeToken<List<ForumModel>>() {}.getType());
        mCacheObjectDao.putCache(CACHE_KEY_FORUM_LIST, json, null);
    }

    @Override
    public List<ForumModel> getCachedForumList() throws Exception {
        String json = mCacheObjectDao.getCache(CACHE_KEY_FORUM_LIST);
        return mGson.fromJson(json, new TypeToken<List<ForumModel>>() {}.getType());
    }

    @Override
    public void removeCachedForumList() throws Exception {
        mCacheObjectDao.delete(CACHE_KEY_FORUM_LIST);
    }

    @Override
    public boolean isCachedForumList() throws Exception {
        return mCacheObjectDao.exist(CACHE_KEY_FORUM_LIST);
    }

    @Override
    public void pinForum(PinnedForumEntity pinnedForumEntity) throws Exception {
        mPinnedForumDao.insertOrReplace(pinnedForumEntity);
    }

    @Override
    public void removePinnedForum(long uid, long fid) throws Exception {
        mPinnedForumDao.unpinForum(uid, fid);
    }

    @Override
    public boolean isForumPinned(long uid, long fid) throws Exception {
        return mPinnedForumDao.isPinned(uid, fid);
    }

    @Override
    public List<PinnedForumEntity> loadAllForUser(long uid) throws Exception {
        return mPinnedForumDao.loadAllForUser(uid);
    }

    @Override
    public List<PinnedForumEntity> loadAllPinnedForums() throws Exception {
        return mPinnedForumDao.loadAll();
    }

    @Override
    public void cachePunchResult(PunchResult punchResult) {
        String json = mGson.toJson(punchResult, PunchResult.class);
        mCacheObjectDao.putCache(generatePunchResultKey(punchResult.getUserId()), json, null);
    }

    @Override
    public void removePunchResult(long uid) {
        mCacheObjectDao.delete(generatePunchResultKey(uid));
    }

    @Override
    public PunchResult getCachePunchResult(long uid) {
        String json = mCacheObjectDao.getCache(generatePunchResultKey(uid));
        if(TextUtils.isEmpty(json))
            return null;
        return mGson.fromJson(json, PunchResult.class);
    }

    @Override
    public void cacheNoticeCount(long uid, NoticeCountModel noticeCountModel) {
        String json = mGson.toJson(noticeCountModel, NoticeCountModel.class);
        mCacheObjectDao.putCache(generateNoticeCountKey(uid), json, null);
    }

    @Override
    public void removeNoticeCount(long uid) {
        mCacheObjectDao.delete(generateNoticeCountKey(uid));
    }

    @Override
    public NoticeCountModel loadNoticeCount(long uid) {
        String json = mCacheObjectDao.getCache(generateNoticeCountKey(uid));
        if(TextUtils.isEmpty(json))
            return null;
        return mGson.fromJson(json, NoticeCountModel.class);
    }

    private String generatePunchResultKey(long uid) {
        return CACHE_KEY_PUNCH_RESULT + "|||" + uid;
    }

    private String generateNoticeCountKey(long uid) {
        return CACHE_KEY_NOTIFICATION_COUNT + "|||" + uid;
    }
}
