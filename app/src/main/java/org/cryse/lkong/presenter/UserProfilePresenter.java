package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.UserProfileView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UserProfilePresenter implements BasePresenter<UserProfileView> {
    private static final String LOG_TAG = UserProfilePresenter.class.getName();
    LKongForumService mLKongForumService;
    Subscription mSearchSubscription;
    Subscription mGetUserAllSubscription;
    Subscription mGetUserThreadsSubscription;
    UserProfileView mView;
    @Inject
    public UserProfilePresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
    }

    public void getUserProfile(LKAuthObject authObject, long uid) {
        SubscriptionUtils.checkAndUnsubscribe(mSearchSubscription);
        setLoadingStatus(false, true);
        mSearchSubscription = mLKongForumService.getUserInfo(authObject, uid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onLoadUserProfileComplete(result);
                        },
                        error -> {
                            if(mView != null) {
                                mView.onLoadUserProfileError(error);
                                Timber.e(error, "SearchPresenter::search() onError().", LOG_TAG);
                                mView.setLoading(false);
                            }
                            setLoadingStatus(false, false);
                        },
                        () -> {
                            setLoadingStatus(false, false);
                        }
                );
    }

    public void getUserAllData(LKAuthObject authObject, long start, long uid, boolean isLoadingMore) {
        SubscriptionUtils.checkAndUnsubscribe(mGetUserAllSubscription);
        setLoadingStatus(isLoadingMore, true);
        mGetUserAllSubscription = mLKongForumService.getUserAll(authObject, start, uid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onLoadUserAllData(result, isLoadingMore);
                        },
                        error -> {
                            if(mView != null) {
                                mView.onLoadUserProfileError(error);
                                Timber.e(error, "SearchPresenter::search() onError().", LOG_TAG);
                                mView.setLoading(false);
                            }
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }

    public void getUserThreads(LKAuthObject authObject, long start, long uid, boolean digest, boolean isLoadingMore) {
        SubscriptionUtils.checkAndUnsubscribe(mGetUserThreadsSubscription);
        setLoadingStatus(isLoadingMore, true);
        mGetUserThreadsSubscription = mLKongForumService.getUserThreads(authObject, start, uid, digest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onLoadUserThreads(result, digest, isLoadingMore);
                        },
                        error -> {
                            if(mView != null) {
                                mView.onLoadUserProfileError(error);
                                Timber.e(error, "SearchPresenter::search() onError().", LOG_TAG);
                                mView.setLoading(false);
                            }
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }

    public void setLoadingStatus(boolean loadingMore, boolean isLoading) {
        if (loadingMore)
            mView.setLoadingMore(isLoading);
        else
            mView.setLoading(isLoading);
    }

    @Override
    public void bindView(UserProfileView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = null;
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mSearchSubscription);
        SubscriptionUtils.checkAndUnsubscribe(mGetUserAllSubscription);
        SubscriptionUtils.checkAndUnsubscribe(mGetUserThreadsSubscription);
    }
}
