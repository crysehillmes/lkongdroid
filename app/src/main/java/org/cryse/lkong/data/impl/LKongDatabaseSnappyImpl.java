package org.cryse.lkong.data.impl;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;

import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.UserInfoModel;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class LKongDatabaseSnappyImpl implements LKongDatabase {
    private Context mAppContext;
    private DB mSnappydb;

    private static final String CACHE_KEY_USER_INFO_MODEL = "cache_user_info_me";
    private static final String CACHE_KEY_FORUM_MODEL_LIST = "cache_user_forum_list";

    @Inject
    public LKongDatabaseSnappyImpl(@ApplicationContext Context appContext) {
        this.mAppContext = appContext;
    }

    @Override
    public void initialize() throws Exception {
        if(mSnappydb == null || !mSnappydb.isOpen()) {
            this.mSnappydb = DBFactory.open(mAppContext);
        }
    }

    @Override
    public void close() throws Exception {
        if(mSnappydb != null && mSnappydb.isOpen()) {
            mSnappydb.close();
        }
    }

    @Override
    public boolean isOpen() throws Exception {
        return mSnappydb != null && mSnappydb.isOpen();
    }

    @Override
    public void cacheUserInfo(UserInfoModel userInfoModel) throws Exception {
        this.mSnappydb.put(CACHE_KEY_USER_INFO_MODEL, userInfoModel);
    }

    @Override
    public UserInfoModel getCachedUserInfo() throws Exception {
        return this.mSnappydb.getObject(CACHE_KEY_USER_INFO_MODEL, UserInfoModel.class);
    }

    @Override
    public boolean isCachedUserInfo() throws Exception {
        return this.mSnappydb.exists(CACHE_KEY_USER_INFO_MODEL);
    }

    @Override
    public void removeCachedUserInfo() throws Exception {
        this.mSnappydb.del(CACHE_KEY_USER_INFO_MODEL);
    }

    @Override
    public void cacheForumList(List<ForumModel> forumModels) throws Exception {
        ForumModel[] forumModelArray = new ForumModel[forumModels.size()];
        forumModelArray = forumModels.toArray(forumModelArray);
        this.mSnappydb.put(CACHE_KEY_FORUM_MODEL_LIST, forumModelArray);
    }

    @Override
    public List<ForumModel> getCachedForumList() throws Exception {
        ForumModel[] forumModelArray =  this.mSnappydb.getObjectArray(CACHE_KEY_FORUM_MODEL_LIST, ForumModel.class);
        return Arrays.asList(forumModelArray);
    }

    @Override
    public void removeCachedForumList() throws Exception {
        this.mSnappydb.del(CACHE_KEY_FORUM_MODEL_LIST);
    }

    @Override
    public boolean isCachedForumList() throws Exception {
        return this.mSnappydb.exists(CACHE_KEY_FORUM_MODEL_LIST);
    }
}
