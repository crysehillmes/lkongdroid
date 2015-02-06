package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.NewThreadResult;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.NewThreadView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NewThreadPresenter implements BasePresenter<NewThreadView> {
    public static final String LOG_TAG = NewThreadPresenter.class.getName();

    LKongForumService mLKongForumService;
    NewThreadView mView;
    Subscription mUploadSubscription;

    @Inject
    public NewThreadPresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
        this.mView = new EmptyNewThreadView();
    }

    public void newThread(LKAuthObject authObject, String title, long fid, String content, boolean follow) {
        SubscriptionUtils.checkAndUnsubscribe(mUploadSubscription);
        mUploadSubscription = mLKongForumService.newPostThread(authObject, title, fid, content, follow)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("NewThreadPresenter::newThread() onNext().", LOG_TAG);
                            mView.onPostThreadComplete(result);
                        },
                        error -> {
                            Timber.e(error, "NewThreadPresenter::newThread() onError().", LOG_TAG);
                            mView.onPostThreadComplete(null);
                        },
                        () -> {
                            Timber.d("NewThreadPresenter::newThread() onComplete().", LOG_TAG);
                        }
                );
    }

    @Override
    public void bindView(NewThreadView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = new EmptyNewThreadView();
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mUploadSubscription);
    }

    private class EmptyNewThreadView implements NewThreadView {

        @Override
        public void onPostThreadComplete(NewThreadResult result) {

        }
    }
}
