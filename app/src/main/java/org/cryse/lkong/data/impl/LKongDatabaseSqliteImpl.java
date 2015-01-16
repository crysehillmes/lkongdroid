package org.cryse.lkong.data.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.cryse.lkong.data.dao.CacheObjectDao;
import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.dao.UserAccountDao;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.model.ForumModel;

import java.util.List;

import javax.inject.Inject;

public class LKongDatabaseSqliteImpl implements LKongDatabase {
    CacheObjectDao mCacheObjectDao;
    UserAccountDao mUserAccountDao;
    Gson mGson;

    @Inject
    public LKongDatabaseSqliteImpl(CacheObjectDao cacheObjectDao, UserAccountDao userAccountDao) {
        this.mCacheObjectDao = cacheObjectDao;
        this.mUserAccountDao = userAccountDao;
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
        mUserAccountDao.insert(userAccountEntity);
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
}
