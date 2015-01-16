package org.cryse.lkong.presenter;

import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.utils.ToastErrorConstant;
import org.cryse.lkong.utils.ToastSupport;
import org.cryse.lkong.view.UserAccountView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UserAccountPresenter implements BasePresenter<UserAccountView> {
    public static final String LOG_TAG = UserAccountPresenter.class.getName();

    LKongForumService lKongForumService;

    Subscription mUpdateUserAccountSubscription;
    Subscription mGetUserAccountSubscription;
    UserAccountView mView;

    @Inject
    public UserAccountPresenter(LKongForumService lKongForumService) {
        this.lKongForumService = lKongForumService;
        this.mView = new EmptyUserAccountView();
    }

    public void getUserAccount(long uid) {
        SubscriptionUtils.checkAndUnsubscribe(mGetUserAccountSubscription);
        mGetUserAccountSubscription = lKongForumService.getUserAccount(uid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("UserAccountPresenter::getUserAccount() onNext()",  LOG_TAG);
                            mView.showUserAccount(result);
                        },
                        error -> {
                            Timber.e(error, "UserAccountPresenter::getUserAccount() onError()",  LOG_TAG);
                            mView.showUserAccount(null);
                            mView.showToast(ToastErrorConstant.TOAST_FAILURE_USER_INFO, ToastSupport.TOAST_ALERT);
                        },
                        () -> {
                            // getUserInfo finished.
                            Timber.d("UserAccountPresenter::getUserAccount() onComplete()", LOG_TAG);
                        });
    }

    public void updateUserAccount(long uid, LKAuthObject authObject) {
        SubscriptionUtils.checkAndUnsubscribe(mUpdateUserAccountSubscription);
        mUpdateUserAccountSubscription = lKongForumService.updateUserAccount(uid, authObject)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("UserAccountPresenter::updateUserAccount() onNext()",  LOG_TAG);
                            mView.showUserAccount(result);
                        },
                        error -> {
                            Timber.e(error, "UserAccountPresenter::updateUserAccount() onError()",  LOG_TAG);
                            mView.showUserAccount(null);
                            mView.showToast(ToastErrorConstant.TOAST_FAILURE_USER_INFO, ToastSupport.TOAST_ALERT);
                        },
                        () -> {
                            // getUserInfo finished.
                            Timber.d("UserAccountPresenter::updateUserAccount() onComplete()",  LOG_TAG);
                        });
    }

    @Override
    public void bindView(UserAccountView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = new EmptyUserAccountView();
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mUpdateUserAccountSubscription);
    }

    private class EmptyUserAccountView implements UserAccountView {

        @Override
        public void showUserAccount(UserAccountEntity userAccount) {

        }

        @Override
        public void setLoading(Boolean value) {

        }

        @Override
        public Boolean isLoading() {
            return null;
        }

        @Override
        public void showToast(int text_value, int toastType) {

        }
    }
}
