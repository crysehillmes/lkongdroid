package org.cryse.lkong.logic;

import org.cryse.lkong.logic.restservice.LKongRestService;
import org.cryse.lkong.logic.restservice.model.UserInfo;
import org.cryse.lkong.model.converter.ForumModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

public class LKongForumService {
    LKongRestService mLKongRestService;

    @Inject
    @Singleton
    public LKongForumService(LKongRestService lKongRestService) {
        this.mLKongRestService = lKongRestService;
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

    public Observable<UserInfo> getUserConfigInfo() {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(mLKongRestService.getUserConfigInfo());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public Observable<List<ForumModel>> getForumList() {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(mLKongRestService.getForumList());
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
