package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.MainActivityView;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivityPresenter implements BasePresenter<MainActivityView> {
    public static final String LOG_TAG = MainActivityPresenter.class.getName();
    LKongForumService mLKongForumService;
    Subscription mPunchSubscription;
    MainActivityView mView;

    @Inject
    public MainActivityPresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
        this.mView = null;
    }
    public void punch(List<LKAuthObject> authObjectList) {
        SubscriptionUtils.checkAndUnsubscribe(mPunchSubscription);
        mPunchSubscription = mLKongForumService.punch(authObjectList)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onPunchUserComplete(result);
                        },
                        error -> {
                            Timber.e(error, "MainActivityPresenter::punch() onError().", LOG_TAG);
                        },
                        () -> {
                        }
                );
    }

    @Override
    public void bindView(MainActivityView view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mPunchSubscription);
    }
}
