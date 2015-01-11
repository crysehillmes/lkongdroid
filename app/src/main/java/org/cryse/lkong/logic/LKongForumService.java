package org.cryse.lkong.logic;

import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.logic.restservice.LKongRestService;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.model.converter.ModelConverter;

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

    public Observable<SignInResult> signIn(String email, String password) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(mLKongRestService.signIn(email, password));
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

    public Observable<UserInfoModel> getUserConfigInfo() {
        return Observable.create(subscriber -> {
            try {
                if(mLKongRestService.isSignedIn() != LKongRestService.STATUS_SIGNEDIN) {
                    subscriber.onNext(null);
                } else {
                    UserInfoModel userInfoModel = mLKongRestService.getUserConfigInfo();
                    updateUserAvatar(userInfoModel);
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

    public int isSignedIn() {
        return mLKongRestService.isSignedIn();
    }

    public void updateUserAvatar(UserInfoModel userInfo) throws Exception {
        if(mLKongDatabase.isUserAccountExist(userInfo.getUid())) {
            UserAccountEntity accountEntity = mLKongDatabase.getUserAccount(userInfo.getUid());
            accountEntity.setUserAvatar(ModelConverter.uidToAvatarUrl(userInfo.getUid()));
            mLKongDatabase.updateUserAccount(accountEntity);
        }
    }

    private void clearCachedForumList() {
        try {
            mLKongDatabase.removeCachedForumList();
        } catch (Exception e) {
            Timber.e(e, "Error clearCachedForumList()", LOG_TAG);
        }
    }
}
