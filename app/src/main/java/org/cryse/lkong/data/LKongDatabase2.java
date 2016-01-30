package org.cryse.lkong.data;

import android.content.Context;

import org.cryse.lkong.data.model.CachedForum;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.converter.ModelConverter;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class LKongDatabase2 {
    private static LKongDatabase2 instance;

    public static void init(Context context) {
        if (instance == null) {
            synchronized(LKongDatabase2.class) {
                if (instance == null) {
                    instance = new LKongDatabase2(context);
                }
            }
        }
    }

    public static LKongDatabase2 getInstance() {
        return instance;
    }

    private Context mContext;
    private Realm mRealm;

    public LKongDatabase2(Context context) {
        this.mContext = context;
        this.mRealm = Realm.getInstance(mContext);
    }

    public Context getContext() {
        return mContext;
    }

    public void cacheForum(int type, long uid, ForumModel forum) {
        mRealm.beginTransaction();
        CachedForum cachedForum = new CachedForum();
        cachedForum.setId(forum.getFid());
        cachedForum.setUid(uid);
        cachedForum.setType(type);
        cachedForum.setName(forum.getName());
        cachedForum.setIcon(forum.getIcon());
        cachedForum.setDescription(forum.getDescription());
        cachedForum.setBlackboard(forum.getBlackboard());
        cachedForum.setFansNum(forum.getFansNum());
        cachedForum.setStatus(forum.getStatus());
        cachedForum.setSortByDateline(forum.getSortByDateline());
        cachedForum.setThreads(forum.getThreads());
        cachedForum.setTodayPosts(forum.getTodayPosts());
        mRealm.copyToRealmOrUpdate(cachedForum);
        mRealm.commitTransaction();
    }

    public void cacheForums(int type, long uid, List<ForumModel> forums) {
        mRealm.beginTransaction();
        for(ForumModel forum : forums) {
            CachedForum cachedForum = new CachedForum();
            cachedForum.setId(forum.getFid());
            cachedForum.setUid(uid);
            cachedForum.setType(type);
            cachedForum.setName(forum.getName());
            cachedForum.setIcon(forum.getIcon());
            cachedForum.setDescription(forum.getDescription());
            cachedForum.setBlackboard(forum.getBlackboard());
            cachedForum.setFansNum(forum.getFansNum());
            cachedForum.setStatus(forum.getStatus());
            cachedForum.setSortByDateline(forum.getSortByDateline());
            cachedForum.setThreads(forum.getThreads());
            cachedForum.setTodayPosts(forum.getTodayPosts());
            mRealm.copyToRealmOrUpdate(cachedForum);
        }
        mRealm.commitTransaction();
    }

    public ForumModel getCachedForum(int type, long id, long uid) {
        RealmQuery<CachedForum> query = mRealm.where(CachedForum.class);
        query.equalTo("id", id);
        query.equalTo("type", type);
        query.equalTo("uid", uid);
        RealmResults<CachedForum> results = query.findAll();
        if(results.size() > 0) {
            CachedForum cached = results.get(0);
            ForumModel forumModel = new ForumModel();
            forumModel.setFid(cached.getId());
            forumModel.setName(cached.getName());
            forumModel.setIcon(ModelConverter.fidToForumIconUrl(cached.getId()));
            forumModel.setDescription(cached.getDescription());
            forumModel.setBlackboard(cached.getBlackboard());
            forumModel.setFansNum(cached.getFansNum());
            forumModel.setStatus(cached.getStatus());
            forumModel.setSortByDateline(cached.getSortByDateline());
            forumModel.setThreads(cached.getThreads());
            forumModel.setTodayPosts(cached.getTodayPosts());
            return forumModel;
        } else {
            return null;
        }
    }

    public List<ForumModel> getCachedForums(int type, long uid) {

        RealmQuery<CachedForum> query = mRealm.where(CachedForum.class);
        query.equalTo("type", type);
        query.equalTo("uid", uid);
        RealmResults<CachedForum> realmResults =  query.findAll();
        List<ForumModel> results = new ArrayList<>(realmResults.size());
        for(CachedForum cached : realmResults) {
            ForumModel forumModel = new ForumModel();
            forumModel.setFid(cached.getId());
            forumModel.setName(cached.getName());
            forumModel.setIcon(ModelConverter.fidToForumIconUrl(cached.getId()));
            forumModel.setDescription(cached.getDescription());
            forumModel.setBlackboard(cached.getBlackboard());
            forumModel.setFansNum(cached.getFansNum());
            forumModel.setStatus(cached.getStatus());
            forumModel.setSortByDateline(cached.getSortByDateline());
            forumModel.setThreads(cached.getThreads());
            forumModel.setTodayPosts(cached.getTodayPosts());
            results.add(forumModel);
        }
        return results;
    }

    public void removeCachedForum(int type, long id) {
        // obtain the results of a query
        RealmQuery<CachedForum> query = mRealm.where(CachedForum.class);
        query.equalTo("id", id);
        query.equalTo("type", type);
        RealmResults<CachedForum> results = query.findAll();
        mRealm.beginTransaction();
        results.removeLast();
        mRealm.commitTransaction();
    }

    public void destroy() {
        if(mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }
    }
}
