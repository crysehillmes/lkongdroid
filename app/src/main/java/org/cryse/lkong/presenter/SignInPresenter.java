package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.SignInView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SignInPresenter implements BasePresenter<SignInView> {

    LKongForumService mLKongForumService;

    Subscription mSignInSubscription;

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
    }

    public void SignIn(String email, String password) {
        mSignInSubscription = mLKongForumService.signIn(email, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.signInComplete(result);
                        },
                        error -> {
                            mView.signInComplete(null);
                        },
                        () -> {
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
    }
}
