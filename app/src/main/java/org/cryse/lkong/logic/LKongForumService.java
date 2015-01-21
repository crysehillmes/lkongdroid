package org.cryse.lkong.logic;

import android.util.Log;

import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.logic.restservice.LKongRestService;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.NewPostResult;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.model.ForumThreadModel;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.utils.LKAuthObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

public class LKongForumService {
    public static final String LOG_TAG = LKongForumService.class.getName();
    LKongRestService mLKongRestService;
    LKongDatabase mLKongDatabase;

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

    public Observable<SignInResult> signIn(String email, String password) {
        return Observable.create(subscriber -> {
            try {
                SignInResult signInResult = mLKongRestService.signIn(email, password);

                subscriber.onNext(signInResult);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<Void> persistUserAccount(UserAccountEntity userAccountEntity) {
        return Observable.create(subscriber -> {
            try {if(mLKongDatabase != null && mLKongDatabase.isOpen() && userAccountEntity != null) {
            if(mLKongDatabase.isUserAccountExist(userAccountEntity.getUserId())) {
                mLKongDatabase.updateUserAccount(userAccountEntity);
            } else {
                mLKongDatabase.addUserAccount(userAccountEntity);}
            }
                subscriber.onNext(null);
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

    public Observable<List<ForumModel>> getForumList() {
        return Observable.create(subscriber -> {
            try {
                if (mLKongDatabase.isCachedForumList()) {
                    subscriber.onNext(mLKongDatabase.getCachedForumList());
                }
                List<ForumModel> forumModelList = mLKongRestService.getForumList();
                if (forumModelList != null)
                    mLKongDatabase.cacheForumList(forumModelList);
                subscriber.onNext(forumModelList);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<List<ForumThreadModel>> getForumThread(long fid, long start, int listType) {
        return Observable.create(subscriber -> {
            try {
                List<ForumThreadModel> forumModelList = mLKongRestService.getForumThreadList(fid, start, listType);
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

    public Observable<List<PostModel>> getPostList(long tid, int page) {
        return Observable.create(subscriber -> {
           try {
               List<PostModel> postList = mLKongRestService.getThreadPostList(tid, page);
               subscriber.onNext(postList);
               subscriber.onCompleted();
           } catch (Exception ex) {
               subscriber.onError(ex);
           }
        });
    }

    private static final int IMG_TYPE_LOCAL = 1;
    private static final int IMG_TYPE_URL = 2;
    private static final int IMG_TYPE_EMOJI = 3;
    private static final String IMG_TAG_FORMAT = "([([[%d][%s]])])";

    public Observable<NewPostResult> newPostReply(LKAuthObject authObject, long tid, Long pid, String content) {
        return Observable.create(subscriber -> {
            try {
                Pattern pattern = Pattern.compile("\\(\\[\\(\\[\\[(\\d)\\]\\[([^\\]]+)\\]\\]\\)\\]\\)");
                Matcher matcher = pattern.matcher(content);
                StringBuffer s = new StringBuffer();
                while (matcher.find()) {
                    Log.d("tst", "group " + matcher.group());
                    Log.d("tst", "groupCount: " + Integer.toString(matcher.groupCount()));
                    Log.d("tst", "group 1: " + matcher.group(1));
                    Log.d("tst", "group 2: " + matcher.group(2));
                    switch(Integer.valueOf(matcher.group(1))) {
                        case IMG_TYPE_URL:
                            matcher.appendReplacement(s, matcher.group(2));
                            break;
                        case IMG_TYPE_EMOJI:
                            matcher.appendReplacement(s, "http://img.lkong.cn/bq/" + matcher.group(2) + ".gif\"" + " em=\"" + matcher.group(2).substring(2));
                            break;
                        case IMG_TYPE_LOCAL:
                            try {
                                String uploadUrl = mLKongRestService.uploadImageToLKong(matcher.group(2));
                                matcher.appendReplacement(s, uploadUrl);
                            } catch(Exception ex) {
                                Timber.e(ex, "uploadImageToLKong failed", LOG_TAG);
                                continue;
                            }
                            break;
                    }
                }
                matcher.appendTail(s);
                Timber.d(s.toString(), LOG_TAG);
                String finalContent = s.toString();

                NewPostResult result = mLKongRestService.newPostReply(authObject, tid, pid, finalContent);
                subscriber.onNext(result);
                subscriber.onCompleted();
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
        });
    }
}
