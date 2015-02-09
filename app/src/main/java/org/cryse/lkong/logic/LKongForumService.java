package org.cryse.lkong.logic;

import org.apache.commons.lang3.StringEscapeUtils;
import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.event.FavoritesChangedEvent;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.logic.restservice.LKongRestService;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.NewPostResult;
import org.cryse.lkong.model.NewThreadResult;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.utils.ContentProcessor;
import org.cryse.lkong.utils.LKAuthObject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import timber.log.Timber;

public class LKongForumService {
    public static final String LOG_TAG = LKongForumService.class.getName();
    LKongRestService mLKongRestService;
    LKongDatabase mLKongDatabase;
    RxEventBus mEventBus;

    @Inject
    @Singleton
    public LKongForumService(LKongRestService lKongRestService, LKongDatabase lKongDatabase, RxEventBus rxEventBus) {
        this.mLKongRestService = lKongRestService;
        this.mLKongDatabase = lKongDatabase;
        this.mEventBus = rxEventBus;
        try {
            this.mLKongDatabase.initialize();
        } catch (Exception ex) {
            Timber.e(ex, "LKongForumService::LKongForumService() initialize database failed.", LOG_TAG);
            throw new IllegalStateException("Database initialize failed, app may work unproperly.");
        }
    }

    public Observable<SignInResult> signIn(String email, String password) {
        return Observable.create(subscriber -> {
            try {
                SignInResult signInResult = mLKongRestService.signIn(email, password);

                if(signInResult != null && signInResult.isSuccess()) {
                    UserAccountEntity userAccountEntity = new UserAccountEntity(
                            signInResult.getMe().getUid(),
                            email,
                            signInResult.getMe().getUserName(),
                            signInResult.getMe().getUserIcon(),
                            signInResult.getAuthCookie(),
                            signInResult.getDzsbheyCookie(),
                            signInResult.getIdentityCookie()
                    );
                    if (mLKongDatabase != null && mLKongDatabase.isOpen()) {
                        if (mLKongDatabase.isUserAccountExist(userAccountEntity.getUserId())) {
                            mLKongDatabase.updateUserAccount(userAccountEntity);
                        } else {
                            mLKongDatabase.addUserAccount(userAccountEntity);
                        }
                    }
                }
                subscriber.onNext(signInResult);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<UserAccountEntity> getUserAccount(long uid) {
        return Observable.create(subscriber -> {
            try {

                subscriber.onNext(mLKongDatabase.getUserAccount(uid));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<UserAccountEntity> updateUserAccount(long uid, LKAuthObject authObject) {
        return Observable.create(subscriber -> {
            try {
                UserInfoModel userInfoModel = mLKongRestService.getUserInfo(authObject);
                UserAccountEntity userAccountEntity = mLKongDatabase.getUserAccount(uid);
                userAccountEntity.setUserName(userInfoModel.getUserName());
                userAccountEntity.setUserAvatar(userInfoModel.getUserIcon());
                mLKongDatabase.updateUserAccount(userAccountEntity);
                subscriber.onNext(userAccountEntity);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<List<UserAccountEntity>> getAllUserAccounts() {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(mLKongDatabase.getAllUserAccounts());
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
                    List<ForumModel> forumModelList = mLKongRestService.getForumList();
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
                List<ThreadModel> forumModelList = mLKongRestService.getForumThreadList(fid, start, listType);
                subscriber.onNext(forumModelList);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<ThreadInfoModel> getThreadInfo(long tid) {
        return Observable.create(subscriber -> {
            try {
                ThreadInfoModel threadModel = mLKongRestService.getThreadInfo(tid);
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
               List<PostModel> postList = mLKongRestService.getThreadPostList(authObject, tid, page);
               subscriber.onNext(postList);
               subscriber.onCompleted();
           } catch (Exception ex) {
               subscriber.onError(ex);
           }
        });
    }


    public Observable<NewPostResult> newPostReply(LKAuthObject authObject, long tid, Long pid, String content) {
        return Observable.create(subscriber -> {
            try {
                String unescapedContent = StringEscapeUtils.unescapeHtml4(content);
                ContentProcessor contentProcessor = new ContentProcessor(unescapedContent);
                contentProcessor.setUploadImageCallback(path -> {
                    String uploadUrl = "";
                    try {
                        Timber.d("setUploadImageCallback start", LOG_TAG);
                        uploadUrl = mLKongRestService.uploadImageToLKong(authObject, path);
                        Timber.d(String.format("uploadImageToLKong result %s", uploadUrl), LOG_TAG);
                    } catch(Exception ex) {
                        Timber.e(ex, "uploadImageToLKong failed", LOG_TAG);
                    } finally {
                        return uploadUrl;
                    }
                });
                contentProcessor.run();
                String replaceResult = contentProcessor.getResultContent();

                Timber.d(replaceResult, LOG_TAG);
                NewPostResult result = mLKongRestService.newPostReply(authObject, tid, pid, replaceResult);
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }

    public Observable<NewThreadResult> newPostThread(LKAuthObject authObject, String title, long fid, String content, boolean follow) {
        return Observable.create(subscriber -> {
            try {
                String unescapedContent = StringEscapeUtils.unescapeHtml4(content);
                ContentProcessor contentProcessor = new ContentProcessor(unescapedContent);
                contentProcessor.setUploadImageCallback(path -> {
                    String uploadUrl = "";
                    try {
                        Timber.d("setUploadImageCallback start", LOG_TAG);
                        uploadUrl = mLKongRestService.uploadImageToLKong(authObject, path);
                        Timber.d(String.format("uploadImageToLKong result %s", uploadUrl), LOG_TAG);
                    } catch(Exception ex) {
                        Timber.e(ex, "uploadImageToLKong failed", LOG_TAG);
                    } finally {
                        return uploadUrl;
                    }
                });
                contentProcessor.run();
                String replaceResult = contentProcessor.getResultContent();

                Timber.d(replaceResult, LOG_TAG);
                NewThreadResult result = mLKongRestService.newPostThread(authObject, title, fid, replaceResult, follow);
                subscriber.onNext(result);
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

    public Observable<List<TimelineModel>> getTimeline(LKAuthObject authObject, long start, int listType) {
        return Observable.create(subscriber -> {
            try {
                List<TimelineModel> result = mLKongRestService.getTimeline(authObject, start, listType);
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }
}
