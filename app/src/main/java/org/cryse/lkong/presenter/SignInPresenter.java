package org.cryse.lkong.presenter;

import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.utils.ToastErrorConstant;
import org.cryse.lkong.utils.ToastSupport;
import org.cryse.lkong.view.SignInView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SignInPresenter implements BasePresenter<SignInView> {
    public static final String LOG_TAG = SignInPresenter.class.getName();
    LKongForumService mLKongForumService;

    Subscription mSignInSubscription;
    Subscription mPersistUserAccountSubscription;
    SignInView mView;

    @Inject
    public SignInPresenter(LKongForumService lKongForumService) {
        this.mLKongForumService = lKongForumService;
        this.mView = new EmptySignInView();
    }

    @Override
    public void bindView(SignInView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = new EmptySignInView();
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mSignInSubscription);
        SubscriptionUtils.checkAndUnsubscribe(mPersistUserAccountSubscription);
    }

    public void SignIn(String email, String password) {
        SubscriptionUtils.checkAndUnsubscribe(mSignInSubscription);
        mSignInSubscription = mLKongForumService.signIn(email, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("SignInPresenter::SignIn() onNext().", LOG_TAG);
                            mView.signInComplete(result);
                        },
                        error -> {
                            Timber.e(error, "SignInPresenter::SignIn() onError().", LOG_TAG);
                            mView.signInComplete(null);
                            mView.showToast(ToastErrorConstant.TOAST_FAILURE_SIGNIN, ToastSupport.TOAST_ALERT);
                        },
                        () -> {
                            Timber.d("SignInPresenter::SignIn() onComplete().", LOG_TAG);
                        });
    }

    public void persistUserAccount(UserAccountEntity userAccount) {
        SubscriptionUtils.checkAndUnsubscribe(mPersistUserAccountSubscription);
        mPersistUserAccountSubscription = mLKongForumService.persistUserAccount(userAccount)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("SignInPresenter::persistUserAccount() onNext().", LOG_TAG);
                            mView.onPersistUserAccountComplete(userAccount);
                        },
                        error -> {
                            Timber.e(error, "SignInPresenter::persistUserAccount() onError().", LOG_TAG);
                            mView.onPersistUserAccountComplete(null);
                            mView.showToast(ToastErrorConstant.TOAST_FAILURE_SIGNIN, ToastSupport.TOAST_ALERT);
                        },
                        () -> {
                            Timber.d("SignInPresenter::persistUserAccount() onComplete().", LOG_TAG);
                        });
    }

    private class EmptySignInView implements SignInView {

        @Override
        public void setLoading(Boolean value) {

        }

        @Override
        public Boolean isLoading() {
            return null;
        }

        @Override
        public void showToast(int text, int toastType) {

        }

        @Override
        public void signInComplete(SignInResult signInResult) {

        }

        @Override
        public void onPersistUserAccountComplete(UserAccountEntity userAccount) {

        }
    }
}
