package org.cryse.lkong.logic;

import com.snappydb.SnappydbException;

import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.logic.restservice.LKongRestService;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.UserInfoModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
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

    public Observable<Boolean> signIn(String email, String password) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(mLKongRestService.signIn(email, password));
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<UserInfoModel> getUserConfigInfo() {
        return Observable.create(subscriber -> {
            try {
                if(mLKongRestService.isSignedIn() != LKongRestService.STATUS_SIGNEDIN) {
                    subscriber.onNext(null);
                } else {
                    if(mLKongDatabase.isCachedUserInfo()) {
                        subscriber.onNext(mLKongDatabase.getCachedUserInfo());
                    }
                    UserInfoModel userInfoModel = mLKongRestService.getUserConfigInfo();
                    if(userInfoModel != null)
                        mLKongDatabase.cacheUserInfo(userInfoModel);
                    subscriber.onNext(userInfoModel);
                }
                subscriber.onCompleted();
            } catch (Exception e) {
                if(e instanceof SnappydbException)
                    clearCachedUserInfo();
                else
                    subscriber.onError(e);
            }
        });
    }

    public Observable<List<ForumModel>> getForumList() {
        return Observable.create(subscriber -> {
            try {
                if(mLKongDatabase.isCachedForumList()) {
                    subscriber.onNext(mLKongDatabase.getCachedForumList());
                }
                List<ForumModel> forumModelList = mLKongRestService.getForumList();
                if(forumModelList != null)
                    mLKongDatabase.cacheForumList(forumModelList);
                subscriber.onNext(forumModelList);
                subscriber.onCompleted();
            } catch (Exception e) {
                if(e instanceof SnappydbException)
                    clearCachedForumList();
                else
                    subscriber.onError(e);
            }
        });
    }

    public int isSignedIn() {
        return mLKongRestService.isSignedIn();
    }

    private void clearCachedForumList() {
        try {
            mLKongDatabase.removeCachedForumList();
        } catch (Exception e) {
            Timber.e(e, "Error clearCachedForumList()", LOG_TAG);
        }
    }

    private void clearCachedUserInfo() {
        try {
            mLKongDatabase.removeCachedUserInfo();
        } catch (Exception e) {
            Timber.e(e, "Error clearCachedUserInfo()", LOG_TAG);
        }
    }
}
