package org.cryse.lkong.modules.userprofile;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.modules.base.BasePresenter;
import org.cryse.lkong.utils.SubscriptionUtils;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UserProfilePresenter implements BasePresenter<UserProfileView> {
    private static final String LOG_TAG = UserProfilePresenter.class.getName();
    LKongForumService mLKongForumService;
    Subscription mUserProfileSubscription;
    Subscription mGetUserFollowStatusSubscription;
    Subscription mFollowUserSubscription;
    Subscription mBlockUserSubscription;
    UserProfileView mView;
    @Inject
    public UserProfilePresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
    }

    public void getUserProfile(LKAuthObject authObject, long uid, boolean isSelf) {
        SubscriptionUtils.checkAndUnsubscribe(mUserProfileSubscription);
        setLoadingStatus(true);
        mUserProfileSubscription = mLKongForumService.getUserInfo(authObject, uid, isSelf)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onLoadUserProfileComplete(result);
                        },
                        error -> {
                            if(mView != null) {
                                Timber.e(error, "UserProfilePresenter::getUserProfile() onError().", LOG_TAG);
                            }
                            setLoadingStatus(false);
                        },
                        () -> {
                            setLoadingStatus(false);
                        }
                );
    }

    public void isUserFollowed(LKAuthObject authObject, long targetUid) {
        SubscriptionUtils.checkAndUnsubscribe(mGetUserFollowStatusSubscription);
        mGetUserFollowStatusSubscription = mLKongForumService.isUserFollowed(authObject, targetUid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onCheckFollowStatusComplete(result);
                        },
                        error -> {
                            if(mView != null) {
                                Timber.e(error, "UserProfilePresenter::isUserFollowed() onError().", LOG_TAG);
                            }
                        },
                        () -> {
                        }
                );
    }

    public void isUserBlocked(LKAuthObject authObject, long targetUid) {
        SubscriptionUtils.checkAndUnsubscribe(mBlockUserSubscription);
        mBlockUserSubscription = mLKongForumService.isUserBlocked(authObject, targetUid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onCheckBlockStatusComplete(result);
                        },
                        error -> {
                            if(mView != null) {
                                Timber.e(error, "UserProfilePresenter::isUserFollowed() onError().", LOG_TAG);
                            }
                        },
                        () -> {
                        }
                );
    }

    public void followUser(LKAuthObject authObject, long targetUid, boolean follow) {
        SubscriptionUtils.checkAndUnsubscribe(mFollowUserSubscription);
        mFollowUserSubscription = mLKongForumService.followUser(authObject, targetUid, follow)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onCheckFollowStatusComplete(result);
                        },
                        error -> {
                            if(mView != null) {
                                Timber.e(error, "UserProfilePresenter::followUser() onError().", LOG_TAG);
                            }
                        },
                        () -> {
                        }
                );
    }

    public void blockUser(LKAuthObject authObject, long targetUid, boolean block) {
        SubscriptionUtils.checkAndUnsubscribe(mBlockUserSubscription);
        mBlockUserSubscription = mLKongForumService.blockUser(authObject, targetUid, block)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onCheckBlockStatusComplete(result);
                        },
                        error -> {
                            if(mView != null) {
                                Timber.e(error, "UserProfilePresenter::blockUser() onError().", LOG_TAG);
                            }
                        },
                        () -> {
                        }
                );
    }

    public void setLoadingStatus(boolean isLoading) {
        if(mView == null) return;
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
        SubscriptionUtils.checkAndUnsubscribe(mUserProfileSubscription);
        SubscriptionUtils.checkAndUnsubscribe(mGetUserFollowStatusSubscription);
        SubscriptionUtils.checkAndUnsubscribe(mFollowUserSubscription);
        SubscriptionUtils.checkAndUnsubscribe(mBlockUserSubscription);
    }
}
