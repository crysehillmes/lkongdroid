package org.cryse.lkong.logic;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.format.DateUtils;

import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.LKongDatabase2;
import org.cryse.lkong.data.model.CachedForum;
import org.cryse.lkong.data.model.FollowedForum;
import org.cryse.lkong.data.model.ForumRecord;
import org.cryse.lkong.event.FavoritesChangedEvent;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.logic.request.AddOrRemoveFavoriteRequest;
import org.cryse.lkong.logic.request.FollowRequest;
import org.cryse.lkong.logic.request.ForumListRequest;
import org.cryse.lkong.logic.request.GetDataItemLocationRequest;
import org.cryse.lkong.logic.request.GetFavoritesRequest;
import org.cryse.lkong.logic.request.GetForumInfoRequest;
import org.cryse.lkong.logic.request.GetHotThreadRequest;
import org.cryse.lkong.logic.request.GetNoticeRateLogRequest;
import org.cryse.lkong.logic.request.GetNoticeRequest;
import org.cryse.lkong.logic.request.GetPrivateChatListRequest;
import org.cryse.lkong.logic.request.GetPrivateMessagesRequest;
import org.cryse.lkong.logic.request.GetThreadInfoRequest;
import org.cryse.lkong.logic.request.GetThreadListRequest;
import org.cryse.lkong.logic.request.GetThreadPostListRequest;
import org.cryse.lkong.logic.request.GetTimelineRequest;
import org.cryse.lkong.logic.request.GetUserFollowRequest;
import org.cryse.lkong.logic.request.GetUserInfoRequest;
import org.cryse.lkong.logic.request.GetUserThreadsRequest;
import org.cryse.lkong.logic.request.GetUserTimelineRequest;
import org.cryse.lkong.logic.request.PunchRequest;
import org.cryse.lkong.logic.request.RatePostRequest;
import org.cryse.lkong.logic.request.SearchRequest;
import org.cryse.lkong.logic.request.SendNewPrivateMessageRequest;
import org.cryse.lkong.model.BrowseHistory;
import org.cryse.lkong.model.DataItemLocationModel;
import org.cryse.lkong.model.FollowResult;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.HotThreadModel;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.NoticeModel;
import org.cryse.lkong.model.NoticeRateModel;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.PrivateChatModel;
import org.cryse.lkong.model.PrivateMessageModel;
import org.cryse.lkong.model.PunchResult;
import org.cryse.lkong.model.SearchDataSet;
import org.cryse.lkong.model.SearchUserItem;
import org.cryse.lkong.model.SendNewPrivateMessageResult;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.account.LKAuthObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import timber.log.Timber;

public class LKongForumService {
    public static final String LOG_TAG = LKongForumService.class.getName();
    LKongDatabase mLKongDatabase;
    RxEventBus mEventBus = RxEventBus.getInstance();

    @Inject
    @Singleton
    public LKongForumService(LKongDatabase lKongDatabase) {
        this.mLKongDatabase = lKongDatabase;
        try {
            this.mLKongDatabase.initialize();
        } catch (Exception ex) {
            Timber.e(ex, "LKongForumService::LKongForumService() initialize database failed.", LOG_TAG);
            throw new IllegalStateException("Database initialize failed, app may work unproperly.");
        }
    }

    public Observable<UserInfoModel> getUserInfo(LKAuthObject authObject, long uid, boolean isSelf) {
        return Observable.create(subscriber -> {
            try {
                GetUserInfoRequest request = new GetUserInfoRequest(authObject, uid);
                UserInfoModel userInfoModel = request.execute();
                subscriber.onNext(userInfoModel);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<List<ForumModel>> getForumList(LKAuthObject authObject, boolean updateFromWeb) {
        return Observable.create(subscriber -> {
            try {
                List<ForumModel> cachedForums = new ArrayList<ForumModel>();
                LKongDatabase2 database = new LKongDatabase2(LKongDatabase2.getInstance().getContext());
                Pair<List<ForumModel>, List<Long>> resultPair = database.getCachedForums(LKongConst.MAIN_FORUM_IDS);
                cachedForums.addAll(resultPair.first);
                if (cachedForums.size() > 0) {
                    subscriber.onNext(cachedForums);
                }
                if(updateFromWeb || cachedForums.size() == 0) {
                    // ForumListRequest request = new ForumListRequest();
                    // List<ForumModel> forumModelList = request.execute();
                    // if (forumModelList != null)
                    //     database.cacheForums(CachedForum.TYPE_MAIN, 0, forumModelList);
                    List<ForumModel> updatedForums = new ArrayList<>();
                    for(long fid : LKongConst.MAIN_FORUM_IDS) {
                        GetForumInfoRequest request = new GetForumInfoRequest(authObject, fid);
                        updatedForums.add(request.execute());
                    }
                    database.cacheForums(updatedForums);
                    subscriber.onNext(updatedForums);
                }
                database.destroy();
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<List<ThreadModel>> getForumThread(LKAuthObject authObject, long fid, long start, int listType) {
        return Observable.create(subscriber -> {
            try {
                GetThreadListRequest request = new GetThreadListRequest(authObject, fid, start, listType);
                List<ThreadModel> forumModelList = request.execute();
                subscriber.onNext(forumModelList);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<ThreadInfoModel> getThreadInfo(LKAuthObject authObject, long tid) {
        return Observable.create(subscriber -> {
            try {
                GetThreadInfoRequest request = new GetThreadInfoRequest(authObject, tid);
                ThreadInfoModel threadModel = request.execute();
                subscriber.onNext(threadModel);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<List<PostModel>> getPostList(LKAuthObject authObject, long tid, int page) {
        return Observable.create(subscriber -> {
           try {
               GetThreadPostListRequest request = new GetThreadPostListRequest(authObject, tid, page);
               List<PostModel> postList = request.execute();
               subscriber.onNext(postList);
               subscriber.onCompleted();
           } catch (Exception ex) {
               subscriber.onError(ex);
           }
        });
    }

    public Observable<List<ThreadModel>> getFavorite(LKAuthObject authObject, long start) {
        return Observable.create(subscriber -> {
            try {
                GetFavoritesRequest request = new GetFavoritesRequest(authObject, start);
                List<ThreadModel> forumModelList = request.execute();
                subscriber.onNext(forumModelList);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<Boolean> addOrRemoveFavorite(LKAuthObject authObject, long tid, boolean remove) {
        return Observable.create(subscriber -> {
            try {
                AddOrRemoveFavoriteRequest request = new AddOrRemoveFavoriteRequest(authObject, tid, remove);
                Boolean result = request.execute();
                mEventBus.sendEvent(new FavoritesChangedEvent());
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<List<TimelineModel>> getTimeline(LKAuthObject authObject, long start, int listType, boolean onlyThread) {
        return Observable.create(subscriber -> {
            try {
                GetTimelineRequest request = new GetTimelineRequest(authObject, start, listType, onlyThread);
                List<TimelineModel> result = request.execute();
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<List<NoticeModel>> getNotice(LKAuthObject authObject, long start) {
        return Observable.create(subscriber -> {
            try {
                GetNoticeRequest request = new GetNoticeRequest(authObject, start);
                List<NoticeModel> result = request.execute();
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<List<NoticeRateModel>> getNoticeRateLog(LKAuthObject authObject, long start) {
        return Observable.create(subscriber -> {
            try {
                GetNoticeRateLogRequest request = new GetNoticeRateLogRequest(authObject, start);
                List<NoticeRateModel> result = request.execute();
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<List<PrivateChatModel>> getNoticePrivateChats(LKAuthObject authObject, long start) {
        return Observable.create(subscriber -> {
            try {
                GetPrivateChatListRequest request = new GetPrivateChatListRequest(authObject, start);
                List<PrivateChatModel> result = request.execute();
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<DataItemLocationModel> getPostIdLocation(LKAuthObject authObject, long postId) {
        return Observable.create(subscriber -> {
            try {
                GetDataItemLocationRequest request = new GetDataItemLocationRequest(authObject, String.format("post_%d", postId));
                DataItemLocationModel locationModel = request.execute();
                subscriber.onNext(locationModel);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<PostModel.PostRate> ratePost(LKAuthObject authObject, long postId, int score, String reason) {
        return Observable.create(subscriber -> {
            try {
                RatePostRequest request = new RatePostRequest(authObject, postId, score, reason);
                PostModel.PostRate postRate = request.execute();
                subscriber.onNext(postRate);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<SearchDataSet> search(LKAuthObject authObject, long start, String queryString) {
        return Observable.create(subscriber -> {
            try {
                SearchRequest request = new SearchRequest(authObject, start, queryString);
                SearchDataSet dataSet = request.execute();
                subscriber.onNext(dataSet);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<List<TimelineModel>> getUserAll(LKAuthObject authObject, long uid, long start) {
        return Observable.create(subscriber -> {
            try {
                GetUserTimelineRequest request = new GetUserTimelineRequest(authObject, uid, start);
                List<TimelineModel> dataSet = request.execute();
                subscriber.onNext(dataSet);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<List<ThreadModel>> getUserThreads(LKAuthObject authObject, long uid, long start, boolean isDigest) {
        return Observable.create(subscriber -> {
            try {
                GetUserThreadsRequest request = new GetUserThreadsRequest(authObject, uid, start, isDigest);
                List<ThreadModel> dataSet = request.execute();
                subscriber.onNext(dataSet);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<List<HotThreadModel>> getHotThread(boolean digest) {
        return Observable.create(subscriber -> {
            try {
                GetHotThreadRequest request = new GetHotThreadRequest(digest);
                List<HotThreadModel> hotThreadModels = request.execute();
                subscriber.onNext(hotThreadModels);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<List<SearchUserItem>> getUserFollow(LKAuthObject authObject, long uid, boolean follower, long start) {
        return Observable.create(subscriber -> {
            try {
                GetUserFollowRequest request = new GetUserFollowRequest(authObject, uid, follower, start);
                List<SearchUserItem> dataSet = request.execute();
                subscriber.onNext(dataSet);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<PunchResult> punch(LKAuthObject authObject) {
        return Observable.create(subscriber -> {
            try {
                    PunchResult result = null;
                    result = mLKongDatabase.getCachePunchResult(authObject.getUserId());
                    if(result != null && result.getPunchTime() != null && DateUtils.isToday(result.getPunchTime().getTime())) {
                        subscriber.onNext(result);
                        return;
                    } else {
                        PunchRequest request = new PunchRequest(authObject);
                        result = request.execute();
                        if(result != null)
                            mLKongDatabase.cachePunchResult(result);
                    }
                    subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<Boolean> followForum(LKAuthObject authObject, long fid, String forumName, String forumIcon) {
        return Observable.create(subscriber -> {
            try {
                LKongDatabase2 database2 = new LKongDatabase2(LKongDatabase2.getInstance().getContext());
                database2.addFollowedForum(authObject.getUserId(), fid);
                database2.destroy();
                FollowRequest followRequest = new FollowRequest(authObject, FollowResult.ACTION_FOLLOW, FollowResult.TYPE_FORUM, fid);
                FollowResult result = followRequest.execute();
                subscriber.onNext(result != null);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<Void> unfollowForum(LKAuthObject authObject, long fid) {
        return Observable.create(subscriber -> {
            try {
                LKongDatabase2 database2 = new LKongDatabase2(LKongDatabase2.getInstance().getContext());
                database2.removeFollowedForum(authObject.getUserId(), fid);
                database2.destroy();
                FollowRequest request = new FollowRequest(authObject, FollowResult.ACTION_UNFOLLOW, FollowResult.TYPE_FORUM, fid);
                FollowResult result = request.execute();
                subscriber.onNext(null);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<Boolean> isForumFollowed(long uid, long fid) {
        return Observable.create(subscriber -> {
            try {
                LKongDatabase2 database2 = new LKongDatabase2(LKongDatabase2.getInstance().getContext());
                boolean isForumPinned = database2.getFollowedForum(uid, fid) != null;
                database2.destroy();
                subscriber.onNext(isForumPinned);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<List<ForumModel>> loadUserFollowedForums(LKAuthObject authObject) {
        return Observable.create(subscriber -> {
            try {
                LKongDatabase2 database2 = new LKongDatabase2(LKongDatabase2.getInstance().getContext());
                List<ForumRecord> followedForums = database2.getFollowedForums(authObject.getUserId());
                List<ForumModel> results = new ArrayList<>();
                if(followedForums != null && followedForums.size() > 0) {
                    long[] fids = new long[followedForums.size()];
                    for (int i = 0; i < followedForums.size(); i++) {
                        ForumRecord record = followedForums.get(i);
                        fids[i] = record.getForumid();
                    }
                    Pair<List<ForumModel>, List<Long>> resultPair = database2.getCachedForums(fids);
                    results.addAll(resultPair.first);
                    for(Long id : resultPair.second) {
                        GetForumInfoRequest request = new GetForumInfoRequest(authObject, id);
                        ForumModel model = request.execute();
                        database2.cacheForum(model);
                        results.add(model);
                    }
                }
                database2.destroy();
                subscriber.onNext(results);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<NoticeCountModel> checkNoticeCountFromDatabase(long uid) {
        return Observable.create(subscriber -> {
            try {
                NoticeCountModel result = mLKongDatabase.loadNoticeCount(uid);
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }


    public Observable<Boolean> followUser(LKAuthObject authObject, long targetUserId) {
        return Observable.create(subscriber -> {
            try {
                mLKongDatabase.followUser(
                        authObject.getUserId(),
                        targetUserId
                );
                FollowRequest request = new FollowRequest(authObject, FollowResult.ACTION_FOLLOW, FollowResult.TYPE_USER, targetUserId);
                FollowResult result = request.execute();
                subscriber.onNext(result != null);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<Boolean> unfollowUser(LKAuthObject authObject, long targetUserId) {
        return Observable.create(subscriber -> {
            try {
                mLKongDatabase.unfollowUser(authObject.getUserId(), targetUserId);
                FollowRequest request = new FollowRequest(authObject, FollowResult.ACTION_UNFOLLOW, FollowResult.TYPE_USER, targetUserId);
                FollowResult result = request.execute();
                subscriber.onNext(false);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<Boolean> isUserFollowed(long uid, long targetUserId) {
        return Observable.create(subscriber -> {
            try {
                boolean isFollowed = mLKongDatabase.isUserFollowed(uid, targetUserId);
                subscriber.onNext(isFollowed);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<Boolean> blockUser(LKAuthObject authObject, long targetUserId, boolean follow) {
        return Observable.create(subscriber -> {
            try {
                FollowRequest request = new FollowRequest(authObject, follow ? FollowResult.ACTION_FOLLOW : FollowResult.ACTION_UNFOLLOW, FollowResult.TYPE_BLACKLIST, targetUserId);
                FollowResult result = request.execute();
                subscriber.onNext(follow ? result != null && result.isOk() : false);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<Boolean> isUserBlocked(long uid, long targetUserId) {
        return Observable.create(subscriber -> {
            try {
                boolean isBlocked = mLKongDatabase.isUserFollowed(uid, targetUserId);
                subscriber.onNext(false);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<List<PrivateMessageModel>> loadPrivateMessages(LKAuthObject authObject, long targetUserId, long startSortKey, int pointerType) {
        return Observable.create(subscriber -> {
            try {
                GetPrivateMessagesRequest request = new GetPrivateMessagesRequest(authObject, targetUserId, startSortKey, pointerType);
                List<PrivateMessageModel> result = request.execute();
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<SendNewPrivateMessageResult> sendPrivateMessage(LKAuthObject authObject, long targetUserId, String targetUserName, String message) {
        return Observable.create(subscriber -> {
            try {
                SendNewPrivateMessageRequest request = new SendNewPrivateMessageRequest(authObject, targetUserId, targetUserName, message);
                SendNewPrivateMessageResult result = request.execute();
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<Void> saveBrowseHistory(
            long uid,
            long threadId,
            String threadTitle,
            @Nullable Long forumId,
            @Nullable String forumTitle,
            @Nullable Long postId,
            long authorId,
            String authorName,
            long lastReadTime
    ) {
        return Observable.create(subscriber -> {
            try {
                mLKongDatabase.saveBrowseHistory(
                        uid,
                        threadId,
                        threadTitle,
                        forumId,
                        forumTitle,
                        postId,
                        authorId,
                        authorName,
                        lastReadTime
                );
                subscriber.onNext(null);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<List<BrowseHistory>> getBrowseHistory(long uid, int start) {
        return Observable.create(subscriber -> {
            try {
                List<BrowseHistory> result = mLKongDatabase.getBrowseHistory(uid, start);
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<Void> cleanBrowseHistory(long uid) {
        return Observable.create(subscriber -> {
            try {
                mLKongDatabase.clearBrowserHistory(uid);
                subscriber.onNext(null);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }
}
