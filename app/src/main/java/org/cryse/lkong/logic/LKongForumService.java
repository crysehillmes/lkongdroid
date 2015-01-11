package org.cryse.lkong.logic;

import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.logic.restservice.LKongRestService;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.model.UserInfoModel;
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
}
