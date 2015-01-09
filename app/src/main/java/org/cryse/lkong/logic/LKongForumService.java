package org.cryse.lkong.logic;

import org.cryse.lkong.logic.restservice.LKongRestService;
import org.cryse.lkong.logic.restservice.model.UserConfigInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

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

    public Observable<UserConfigInfo> getUserConfigInfo() {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(mLKongRestService.getUserConfigInfo());
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
