package org.cryse.lkong.logic;

import android.support.annotation.Nullable;
import android.text.format.DateUtils;

import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.model.FollowedForum;
import org.cryse.lkong.event.FavoritesChangedEvent;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.logic.request.FollowRequest;
import org.cryse.lkong.logic.request.ForumListRequest;
import org.cryse.lkong.logic.request.GetPrivateChatListRequest;
import org.cryse.lkong.logic.request.GetPrivateMessagesRequest;
import org.cryse.lkong.logic.request.GetThreadInfoRequest;
import org.cryse.lkong.logic.request.GetThreadListRequest;
import org.cryse.lkong.logic.request.GetThreadPostListRequest;
import org.cryse.lkong.logic.request.GetUserInfoRequest;
import org.cryse.lkong.logic.request.SearchRequest;
import org.cryse.lkong.logic.request.SendNewPrivateMessageRequest;
import org.cryse.lkong.logic.restservice.LKongRestService;
import org.cryse.lkong.model.BrowseHistory;
import org.cryse.lkong.model.DataItemLocationModel;
import org.cryse.lkong.model.FollowResult;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.NoticeModel;
import org.cryse.lkong.model.NoticeRateModel;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.PrivateChatModel;
import org.cryse.lkong.model.PrivateMessageModel;
import org.cryse.lkong.model.PunchResult;
import org.cryse.lkong.model.SearchDataSet;
import org.cryse.lkong.model.SendNewPrivateMessageResult;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.account.LKAuthObject;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import timber.log.Timber;

public class LKongForumService {
    public static final String LOG_TAG = LKongForumService.class.getName();
    LKongRestService mLKongRestService;
    LKongDatabase mLKongDatabase;
    RxEventBus mEventBus = RxEventBus.getInstance();

    @Inject
    @Singleton
    public LKongForumService(LKongRestService lKongRestService, LKongDatabase lKongDatabase) {
        this.mLKongRestService = lKongRestService;
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

    public Observable<List<ForumModel>> getForumList(boolean updateFromWeb) {
        return Observable.create(subscriber -> {
            try {
                if (mLKongDatabase.isCachedForumList()) {
                    subscriber.onNext(mLKongDatabase.getCachedForumList());
                }
                if(updateFromWeb || !mLKongDatabase.isCachedForumList()) {
                    ForumListRequest request = new ForumListRequest();
                    List<ForumModel> forumModelList = request.execute();
                    if (forumModelList != null)
                        mLKongDatabase.cacheForumList(forumModelList);
                    subscriber.onNext(forumModelList);
                }
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<List<ThreadModel>> getForumThread(long fid, long start, int listType) {
        return Observable.create(subscriber -> {
            try {
                GetThreadListRequest request = new GetThreadListRequest(fid, start, listType);
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
                List<ThreadModel> forumModelList = mLKongRestService.getFavorites(authObject, start);
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
                Boolean result = mLKongRestService.addOrRemoveFavorite(authObject, tid, remove);
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
                List<TimelineModel> result = mLKongRestService.getTimeline(authObject, start, listType, onlyThread);
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
                List<NoticeModel> result = mLKongRestService.getNotice(authObject, start);
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
                List<NoticeRateModel> result = mLKongRestService.getNoticeRateLog(authObject, start);
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
                DataItemLocationModel locationModel = mLKongRestService.getDataItemLocation(authObject, String.format("post_%d", postId));
                subscriber.onNext(locationModel);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<PostModel.PostRate> ratePost(LKAuthObject authObject, long postId, int score, String reaseon) {
        return Observable.create(subscriber -> {
            try {
                PostModel.PostRate postRate = mLKongRestService.ratePost(authObject, postId, score, reaseon);
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
                List<TimelineModel> dataSet = mLKongRestService.getUserAll(authObject, uid, start);
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
                List<ThreadModel> dataSet = mLKongRestService.getUserThreads(authObject, uid, start, isDigest);
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
                        result = mLKongRestService.punch(authObject);
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
                mLKongDatabase.followForum(new FollowedForum(
                        authObject.getUserId(),
                        fid,
                        forumName,
                        forumIcon,
                        new Date().getTime()
                ));
                FollowRequest request = new FollowRequest(authObject, FollowResult.ACTION_FOLLOW, FollowResult.TYPE_FORUM, fid);
                FollowResult result = request.execute();
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
                mLKongDatabase.unfollowForum(authObject.getUserId(), fid);
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
                boolean isForumPinned = mLKongDatabase.isForumFollowed(uid, fid);
                subscriber.onNext(isForumPinned);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<List<FollowedForum>> loadUserFollowedForums(long uid) {
        return Observable.create(subscriber -> {
            try {
                List<FollowedForum> result = mLKongDatabase.loadAllFollowedForumsForUser(uid);
                subscriber.onNext(result);
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

    public Observable<Void> saveBrowseHistory(long uid,
                                              long threadId,
                                              String threadTitle,
                                              @Nullable Long forumId,
                                              @Nullable String forumTitle,
                                              @Nullable Long postId,
                                              long authorId,
                                              String authorName,
                                              long lastReadTime) {
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
