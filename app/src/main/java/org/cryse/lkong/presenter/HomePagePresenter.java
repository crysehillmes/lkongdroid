package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.HomePageView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class HomePagePresenter implements BasePresenter<HomePageView> {
    public static final String LOG_TAG = HomePagePresenter.class.getName();
    LKongForumService mLKongForumService;
    Subscription mPunchSubscription;
    Subscription mCheckNoticeCountSubscription;
    HomePageView mView;

    @Inject
    public HomePagePresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
        this.mView = null;
    }

    public void punch(LKAuthObject authObject) {
        SubscriptionUtils.checkAndUnsubscribe(mPunchSubscription);
        mPunchSubscription = mLKongForumService.punch(authObject)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onPunchUserComplete(result);
                        },
                        error -> {
                            Timber.e(error, "HomePagePresenter::punch() onError().", LOG_TAG);
                        },
                        () -> {
                        }
                );
    }

    public void checkNoticeCountFromDatabase(long uid) {
        SubscriptionUtils.checkAndUnsubscribe(mCheckNoticeCountSubscription);
        mCheckNoticeCountSubscription = mLKongForumService.checkNoticeCountFromDatabase(uid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onCheckNoticeCountComplete(result);
                        },
                        error -> {
                            Timber.e(error, "HomePagePresenter::checkNoticeCountFromDatabase() onError().", LOG_TAG);
                        },
                        () -> {
                        }
                );
    }

    @Override
    public void bindView(HomePageView view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        mView = null;
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mPunchSubscription);
        SubscriptionUtils.checkAndUnsubscribe(mCheckNoticeCountSubscription);
    }
}
