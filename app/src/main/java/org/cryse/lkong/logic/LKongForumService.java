package org.cryse.lkong.logic;

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
                        Timber.d("LKongForumService::getUserConfigInfo() from database.", LOG_TAG);
                    }
                    UserInfoModel userInfoModel = mLKongRestService.getUserConfigInfo();
                    mLKongDatabase.cacheUserInfo(userInfoModel);
                    subscriber.onNext(userInfoModel);
                }
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<List<ForumModel>> getForumList() {
        return Observable.create(subscriber -> {
            try {
                if(mLKongDatabase.isCachedForumList()) {
                    subscriber.onNext(mLKongDatabase.getCachedForumList());
                    Timber.d("LKongForumService::getForumList() from database.", LOG_TAG);
                }
                List<ForumModel> forumModelList = mLKongRestService.getForumList();
                mLKongDatabase.cacheForumList(forumModelList);
                subscriber.onNext(forumModelList);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public int isSignedIn() {
        return mLKongRestService.isSignedIn();
    }
}
