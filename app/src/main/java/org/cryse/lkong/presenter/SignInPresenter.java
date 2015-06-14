package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.utils.ToastErrorConstant;
import org.cryse.lkong.utils.snackbar.SimpleSnackbarType;
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
    SignInView mView;

    @Inject
    public SignInPresenter(LKongForumService lKongForumService) {
        this.mLKongForumService = lKongForumService;
        this.mView = null;
    }

    @Override
    public void bindView(SignInView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = null;
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mSignInSubscription);
    }

    public void SignIn(String email, String password) {
        SubscriptionUtils.checkAndUnsubscribe(mSignInSubscription);
        mSignInSubscription = mLKongForumService.signIn(email, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("SignInPresenter::SignIn() onNext().", LOG_TAG);
                            if (mView != null) {
                                mView.signInComplete(result);
                            }
                        },
                        error -> {
                            Timber.e(error, "SignInPresenter::SignIn() onError().", LOG_TAG);
                            if (mView != null) {
                                SignInResult signInResult = new SignInResult();
                                signInResult.setSuccess(false);
                                mView.signInComplete(signInResult);
                                mView.showSnackbar(
                                        null,
                                        SimpleSnackbarType.ERROR,
                                        SimpleSnackbarType.LENGTH_SHORT,
                                        ToastErrorConstant.TOAST_FAILURE_SIGNIN
                                );
                            }
                        },
                        () -> {
                            Timber.d("SignInPresenter::SignIn() onComplete().", LOG_TAG);
                        });
    }
}
