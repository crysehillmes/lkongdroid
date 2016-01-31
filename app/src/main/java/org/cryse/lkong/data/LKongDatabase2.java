package org.cryse.lkong.data;

import android.content.Context;
import android.support.v4.util.Pair;

import org.cryse.lkong.data.model.CachedForum;
import org.cryse.lkong.data.model.FollowRecord;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.converter.ModelConverter;

import java.util.ArrayList;
import java.util.Date;
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

    public void cacheForum(ForumModel forum) {
        mRealm.beginTransaction();
        CachedForum cachedForum = new CachedForum();
        cachedForum.setId(forum.getFid());
        cachedForum.setName(forum.getName());
        cachedForum.setIcon(forum.getIcon());
        cachedForum.setDescription(forum.getDescription());
        cachedForum.setBlackboard(forum.getBlackboard());
        cachedForum.setFansNum(forum.getFansNum());
        cachedForum.setStatus(forum.getStatus());
        cachedForum.setSortByDateline(forum.getSortByDateline());
        cachedForum.setThreads(forum.getThreads());
        cachedForum.setTodayPosts(forum.getTodayPosts());
        cachedForum.setLastUpdate(new Date().getTime());
        mRealm.copyToRealmOrUpdate(cachedForum);
        mRealm.commitTransaction();
    }

    public void cacheForums(List<ForumModel> forums) {
        mRealm.beginTransaction();
        for(ForumModel forum : forums) {
            CachedForum cachedForum = new CachedForum();
            cachedForum.setId(forum.getFid());
            cachedForum.setName(forum.getName());
            cachedForum.setIcon(forum.getIcon());
            cachedForum.setDescription(forum.getDescription());
            cachedForum.setBlackboard(forum.getBlackboard());
            cachedForum.setFansNum(forum.getFansNum());
            cachedForum.setStatus(forum.getStatus());
            cachedForum.setSortByDateline(forum.getSortByDateline());
            cachedForum.setThreads(forum.getThreads());
            cachedForum.setTodayPosts(forum.getTodayPosts());
            cachedForum.setLastUpdate(new Date().getTime());
            mRealm.copyToRealmOrUpdate(cachedForum);
        }
        mRealm.commitTransaction();
    }

    public ForumModel getCachedForum(long id) {
        RealmQuery<CachedForum> query = mRealm.where(CachedForum.class);
        query.equalTo("id", id);
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

    public Pair<List<ForumModel>, List<Long>> getCachedForums(long[] ids) {
        List<ForumModel> results = new ArrayList<>();
        List<Long> notexists = new ArrayList<>();
        for(Long id : ids) {
            RealmQuery<CachedForum> query = mRealm.where(CachedForum.class);
            query.equalTo("id", id);
            CachedForum result = query.findFirst();
            if(result != null)
                results.add(cachedToModel(result));
            else
                notexists.add(id);
        }
        return Pair.create(results, notexists);
    }

    public List<ForumModel> getCachedForums() {
        RealmQuery<CachedForum> query = mRealm.where(CachedForum.class);
        RealmResults<CachedForum> realmResults =  query.findAll();
        List<ForumModel> results = new ArrayList<>(realmResults.size());
        for(CachedForum cached : realmResults) {
            results.add(cachedToModel(cached));
        }
        return results;
    }

    public void removeCachedForum(long id) {
        // obtain the results of a query
        RealmQuery<CachedForum> query = mRealm.where(CachedForum.class);
        query.equalTo("id", id);
        RealmResults<CachedForum> results = query.findAll();
        mRealm.beginTransaction();
        results.removeLast();
        mRealm.commitTransaction();
    }

    public void addFollowRecord(int type, long userId, long targetId) {
        addFollowRecord(type, userId, targetId, getMaxForumRecordOrdinal());
    }

    public void addFollowRecord(int type, long userId, long targetId, int ordinal) {
        mRealm.beginTransaction();
        FollowRecord forumRecord = new FollowRecord();
        forumRecord.setKey(String.format("%d|%d|%d", type, userId, targetId));
        forumRecord.setType(type);
        forumRecord.setTargetId(targetId);
        forumRecord.setUserid(userId);
        forumRecord.setOrdinal(ordinal);
        mRealm.copyToRealmOrUpdate(forumRecord);
        mRealm.commitTransaction();
    }

    public int getMaxForumRecordOrdinal() {
        int maxId;
        if(mRealm.where(FollowRecord.class).max("ordinal") == null) {
            maxId = 0;
        } else {
            maxId = mRealm.where(FollowRecord.class).max("ordinal").intValue() + 1;
        }
        return maxId;
    }

    public void removeFollowRecord(int type, long userId, long targetId) {
        RealmQuery<FollowRecord> query = mRealm.where(FollowRecord.class);
        query.equalTo("type", type);
        query.equalTo("userid", userId);
        query.equalTo("targetId", targetId);
        RealmResults<FollowRecord> results = query.findAll();
        mRealm.beginTransaction();
        results.removeLast();
        mRealm.commitTransaction();
    }

    public void removeAllFollowRecord(int type, long userId) {
        RealmQuery<FollowRecord> query = mRealm.where(FollowRecord.class);
        query.equalTo("type", type);
        query.equalTo("userid", userId);
        RealmResults<FollowRecord> results = query.findAll();
        mRealm.beginTransaction();
        results.removeLast();
        mRealm.commitTransaction();
    }

    public FollowRecord getFollowRecord(int type, long userId, long targetId) {
        RealmQuery<FollowRecord> query = mRealm.where(FollowRecord.class);
        query.equalTo("type", type);
        query.equalTo("userid", userId);
        query.equalTo("targetId", targetId);
        FollowRecord record = query.findFirst();
        if(record != null)
            return mRealm.copyFromRealm(record);
        else
            return null;
    }

    public List<FollowRecord> getFollowRecords(int type, long userId) {
        RealmQuery<FollowRecord> query = mRealm.where(FollowRecord.class);
        query.equalTo("type", type);
        query.equalTo("userid", userId);
        RealmResults<FollowRecord> records = query.findAll();
        if(records != null && records.size() > 0)
            return mRealm.copyFromRealm(records);
        else
            return null;
    }

    public void destroy() {
        if(mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }
    }

    private static ForumModel cachedToModel(CachedForum cached) {
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
    }
}
